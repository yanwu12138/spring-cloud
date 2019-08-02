package com.yanwu.spring.cloud.common.mvc.controller;

import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.utils.ReflectionUtil;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import com.yanwu.spring.cloud.common.mvc.res.PaginationInfo;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import com.yanwu.spring.cloud.common.mvc.vo.ValueObject;
import com.yanwu.spring.cloud.common.service.SimpleRoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public abstract class CoreRoRestController<VO extends ValueObject, DO extends BaseObject, SERVICE extends SimpleRoService<DO>>
        implements ApplicationContextAware {

    protected SERVICE service;

    @Autowired
    protected VoDoUtil voDoUtil;

    protected ApplicationContext applicationContext;

    @Override
    public final void setApplicationContext(final ApplicationContext applicationContext) {
        log.info("CoreRoRestController:setApplicationContext this={},applicationContext={}", this, applicationContext);
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    protected SERVICE getService() {
        if (service == null) {
            Class<SERVICE> serviceClass = (Class<SERVICE>) ReflectionUtil.getClassGenericType(getClass(), 2);
            service = applicationContext.getBean(serviceClass);
        }
        return service;
    }

    @SuppressWarnings("unchecked")
    protected Class<VO> getVoClass() {
        return (Class<VO>) ReflectionUtil.getClassGenericType(getClass(), 0);
    }

    @SuppressWarnings("unchecked")
    protected Class<VO> getDoClass() {
        return (Class<VO>) ReflectionUtil.getClassGenericType(getClass(), 1);
    }

    @SuppressWarnings("unchecked")
    protected DO convertVoToDo(VO vo) throws Exception {
        return (DO) voDoUtil.convertVoToDo(vo, getVoClass());
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseEnvelope<VO>> findById(@PathVariable(value = "id") Long id) throws Exception {
        DO policy = getService().findOne(id);
        VO vo = voDoUtil.convertDoToVo(policy, getVoClass());
        ResponseEnvelope<VO> responseEnv = new ResponseEnvelope<>(vo);
        return new ResponseEntity<>(responseEnv, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    protected <T extends ValueObject, G extends BaseObject> ResponseEntity<ResponseEnvelope<List<T>>> createResponse(
            Page<G> page) {
        List<G> list = new ArrayList<>();
        list.addAll(page.getContent());
        PaginationInfo paginationInfo = new PaginationInfo(page);

        List<T> resultList = new ArrayList<>();
        resultList = (List<T>) voDoUtil.mapList(list, this.getVoClass());

        ResponseEnvelope<List<T>> env = new ResponseEnvelope<>(resultList, paginationInfo);
        return new ResponseEntity<>(env, HttpStatus.OK);
    }

    protected ResponseEntity<ResponseEnvelope<List<VO>>> findByIds(Set<Long> ids, Pageable pageRequest)
            throws Exception {
        Page<DO> dataPage = getService().findByIds(ids, pageRequest);
        return createResponse(dataPage);
    }

    @ExceptionHandler
    public void exp(HttpServletRequest request, Exception ex) throws Exception {

        log.error("Request={},ex={},root={}", request.getRequestURI(), ex, ExceptionUtils.getRootCauseMessage(ex));

        // 根据不同错误转向不同页面  
        if (ex instanceof BusinessException) {
            log.info("BusinessException={}", ex);
            throw ex;
        } else {
            log.error("exp:", ex);
            throw ex;
        }

    }

}