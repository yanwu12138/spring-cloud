package com.yanwu.spring.cloud.common.mvc.controller;

import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import com.yanwu.spring.cloud.common.mvc.vo.ValueObject;
import com.yanwu.spring.cloud.common.service.SimpleCrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
public class AbsreactCoreCrudRestController<VO extends ValueObject, DO extends BaseObject, SERVICE extends SimpleCrudService<DO>>
        extends AbsreactCoreRoRestController<VO, DO, SERVICE> {

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseEnvelope<VO>> create(@RequestBody VO vo, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            log.error("POST: Invalid VO - " + getVoClass());
            throw new Exception("ServiceErrorCodes.invalidData");
        }

        DO do1 = (DO) voDoUtil.convertVoToDo(vo, getDoClass());
        DO savedDo = getService().create(do1);

        VO savedVo = voDoUtil.convertDoToVo(savedDo, getVoClass());
        ResponseEnvelope<VO> responseEnv = new ResponseEnvelope<>(savedVo);
        return new ResponseEntity<>(responseEnv, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "{id}", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseEnvelope<VO>> update(@RequestBody VO vo, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            log.error("PUT: Invalid VO - " + getVoClass());
            throw new Exception("ServiceErrorCodes.invalidData");
        }
        DO do1 = (DO) voDoUtil.convertVoToDo(vo, getDoClass());
        DO savedDo = getService().update(do1);

        VO savedVo = voDoUtil.convertDoToVo(savedDo, getVoClass());
        ResponseEnvelope<VO> responseEnv = new ResponseEnvelope<>(savedVo);
        return new ResponseEntity<>(responseEnv, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ResponseEnvelope<Void>> delete(@PathVariable Long id) throws Exception {
        getService().delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<ResponseEnvelope<Void>> deleteMultiple(
            @RequestParam(required = true, value = "ids") Set<Long> idSet) throws Exception {
        getService().delete(idSet.toArray(new Long[idSet.size()]));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}