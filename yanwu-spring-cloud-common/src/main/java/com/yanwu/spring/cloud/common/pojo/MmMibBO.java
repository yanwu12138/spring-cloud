package com.yanwu.spring.cloud.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import net.percederberg.mibble.MibValueSymbol;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2023/10/7 10:51.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MmMibBO implements Serializable {
    private static final long serialVersionUID = 5247773334344978883L;

    private String name;
    private String value;
    private Boolean tableColumn;
    private String parent;
    private String syntax;
    private String access;
    private String status;

    public static MmMibBO getInstance(MibValueSymbol mvs, String parent, String syntax, String access, String status) {
        return new MmMibBO()
                .setName(mvs.getName())
                .setValue(mvs.getValue().toString())
                .setTableColumn(mvs.isTableColumn())
                .setParent(parent)
                .setSyntax(syntax)
                .setAccess(access)
                .setStatus(status);
    }

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }
}
