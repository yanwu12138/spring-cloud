package com.yanwu.spring.cloud.common.config;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.yanwu.spring.cloud.common.core.annotation.DataScopeField;
import com.yanwu.spring.cloud.common.core.annotation.DataScopeTable;
import com.yanwu.spring.cloud.common.core.aspect.RequestHandlerAspect;
import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import com.yanwu.spring.cloud.common.pojo.UserAccessesInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XuBaofeng.
 * @date 2024/1/19 16:23.
 * <p>
 * description:
 */
@Slf4j
@Component
public class DataScopeInterceptor extends JsqlParserSupport implements InnerInterceptor {

    /*** 项目中表对应的实体类DO所在的目录 ***/
    private static final String MODEL_PACKAGE = "com.bird.billserver.entity";
    private static final String CLASS_PATTERN = "/**/*.class";
    /*************************************************************************
     * 所有被@DataScopeTable修饰的实体类的class文件缓存
     * 注意️️️️:
     * 表名对应的实体类，该类必须继承BaseDo，防止DataScopeTable被其它地方引用导致功能异常
     * 当数据表对应的实体类没有继承BaseDo时，本增强将不对该实体类生效
     *************************************************************************/
    private static final Map<String, Class<?>> CLAZZ_CACHE = new ConcurrentHashMap<>();

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        if (SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
            if (dataScopeIgnore(RequestHandlerAspect.getUserAccessesInfo())) {
                return;
            }
            // ===== 只对SELECT语句和添加了@UserAccesses注解的方法进行增强
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(parserMulti(mpBs.sql(), null));
        }
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        SelectBody selectBody = select.getSelectBody();
        PlainSelect plainSelect = (PlainSelect) selectBody;
        // ----- 获取表名/别名(如果是关联查询是取第一个join左侧的表名/别名)
        String tableName = getTableName(plainSelect);
        String aliasName = getAliasName(plainSelect);
        // ----- 构建用户权限控制条件
        buildPermissionSql(plainSelect, sql, tableName, aliasName);
    }

    /***
     * 是否跳过这个数据权限校验增强，检查条件如下：
     * 1.检查ThreadLocal中是否有userAccesses，如果没有说明该方法没有被@UserAccesses修饰
     * 2.如果有值，判断是否需要检查Agent或者Shop，如果都不需要则不需要增强该SQL
     * @return 【true: 跳过; false: 不跳过】
     */
    private boolean dataScopeIgnore(UserAccessesInfo userAccesses) {
        try {
            if (userAccesses == null) {
                return true;
            }
            return !(userAccesses.isUserScope() || userAccesses.isRoleScope());
        } catch (Exception e) {
            return false;
        }
    }

    /***
     * 获取SQL语句中的表名
     */
    public String getTableName(PlainSelect plainSelect) {
        Table table = (Table) plainSelect.getFromItem();
        return table.getName();
    }

    /***
     * 获取SQL语句中的别名
     */
    public String getAliasName(PlainSelect plainSelect) {
        Table table = (Table) plainSelect.getFromItem();
        Alias alias = table.getAlias();
        return null == alias ? null : alias.getName();
    }

    /***
     * 构建数据权限控制条件
     * @param plainSelect 用于解析SQL的类
     * @param sql         原始SQL
     * @param tableName   表名/别名(join查询左侧表名)
     */
    private void buildPermissionSql(PlainSelect plainSelect, String sql, String tableName, String aliasName) {
        UserAccessesInfo userAccesses = RequestHandlerAspect.getUserAccessesInfo();
        if (dataScopeIgnore(userAccesses)) {
            return;
        }
        Class<?> clazz = getClazzByTableName(tableName);
        // ===== 根据表名的该表名对应的DAO实体类，根据实体类上的@DataScopeTable和@DataScopeField这两个注解来构建控制条件
        if (clazz != null) {
            DataScopeTable scopeTable = clazz.getAnnotation(DataScopeTable.class);
            if (scopeTable == null || scopeTable.dataScope().length == 0) {
                // ----- 该实体类没有被@DataScopeTable注解修饰，跳过本次增强
                return;
            }
            // ===== 获取该表被@DataScopeField修饰的字段和对应的类型（Agent | Shop）,并根据参数构建控制条件
            for (DataScopeField scopeField : scopeTable.dataScope()) {
                if (scopeField == null) {
                    continue;
                }
                Set<Long> values = scopeField.type().accessIds(userAccesses);
                if (CollectionUtils.isEmpty(values)) {
                    // ----- 说明该用户没有对应的数据权限，抛出无权限访问异常
                    throw new BusinessException("无数据权限");
                }
                appendExpression(plainSelect, buildInExpression(aliasName, scopeTable, scopeField, values));
            }
            log.info("[DataScopeInterceptor] before SQL:[{}]", sql);
            log.info("[DataScopeInterceptor] after SQL:[{}]", plainSelect);
        }
    }

    /***
     * 根据实体上的@DataScopeTable和@DataScopeField来构建AND FIELD_NAME IN (...) 语句
     * @param aliasName      SQL语句中的表别名
     * @param dataScopeTable 表的相关信息
     * @param dataScopeField 表字段的相关信息
     * @param values         IN VALUE
     */
    private InExpression buildInExpression(String aliasName, DataScopeTable dataScopeTable, DataScopeField dataScopeField, Set<Long> values) {
        String columnName;
        // ----- 组装条件：如果SQL中有别名则使用别名；如果SQL中没有别名则使用表名
        if (StringUtils.isNotBlank(aliasName)) {
            columnName = aliasName + "." + dataScopeField.field();
        } else {
            columnName = dataScopeTable.table() + "." + dataScopeField.field();
        }
        List<Expression> expressions = new ArrayList<>();
        values.forEach(id -> expressions.add(new LongValue(id)));
        return new InExpression(new Column(columnName), new ExpressionList(expressions));
    }

    /***
     * 将2个where条件拼接到一起
     * @param plainSelect      plainSelect
     * @param appendExpression 待拼接条件
     */
    private void appendExpression(PlainSelect plainSelect, Expression appendExpression) {
        Expression where = plainSelect.getWhere() == null ? appendExpression : new AndExpression(plainSelect.getWhere(), appendExpression);
        plainSelect.setWhere(where);
    }

    /***
     * 根据表名获取对应实体类Class，该实体类必须满足两个条件：
     * 1、必须继承BaseDo
     * 2、必须被@DataScopeTable注解修饰
     * @param tableName 表名
     * @return clazz
     */
    private Class<?> getClazzByTableName(String tableName) {
        if (CLAZZ_CACHE.containsKey(tableName)) {
            return CLAZZ_CACHE.get(tableName);
        }
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(MODEL_PACKAGE) + CLASS_PATTERN;
            Resource[] resources = resolver.getResources(pattern);
            if (resources == null || resources.length == 0) {
                return null;
            }
            MetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);
            for (Resource resource : resources) {
                try {
                    MetadataReader reader = factory.getMetadataReader(resource);
                    // ----- 扫描到的指定目录下所有的Class
                    Class<?> clazz = Class.forName(reader.getClassMetadata().getClassName());
                    if (!clazz.getSuperclass().equals(BaseDo.class)) {
                        // ***** 该类必须继承BaseDo
                        continue;
                    }
                    // ----- 判断class是否被@DataScopeTable修饰
                    DataScopeTable scopeTable = clazz.getAnnotation(DataScopeTable.class);
                    if (scopeTable != null) {
                        CLAZZ_CACHE.put(scopeTable.table(), clazz);
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return CLAZZ_CACHE.get(tableName);
    }

}
