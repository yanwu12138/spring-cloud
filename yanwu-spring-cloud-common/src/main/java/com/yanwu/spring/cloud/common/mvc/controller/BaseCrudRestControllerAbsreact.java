package com.yanwu.spring.cloud.common.mvc.controller;

import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import com.yanwu.spring.cloud.common.mvc.vo.ValueObject;
import com.yanwu.spring.cloud.common.service.SimpleQuoteCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * @author Administrator
 */
public abstract class BaseCrudRestControllerAbsreact<VO extends ValueObject, DO extends BaseObject, SERVICE extends SimpleQuoteCrudService<DO>>
        extends AbsreactCoreCrudRestController<VO, DO, SERVICE> {

    @RequestMapping(value = "", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseEnvelope<List<VO>>> findByIdsOrOwnerIds(
            @RequestParam(required = false, value = "ids") Set<Long> ids,
            @RequestParam(required = false, value = "ownerIds") Set<Long> ownerIds,
            @PageableDefault Pageable pageRequest)
            throws Exception {

        if (ids != null && ownerIds != null) {
            throw new Exception("(ServiceErrorCodes.invalidData);");
        }

        if (ids != null && ids.isEmpty() == false) {
            return findByIds(ids, pageRequest);
        } else if (ownerIds != null && ownerIds.isEmpty() == false) {
            return findByOwnerIds(ownerIds, pageRequest);
        } else {
            throw new Exception();

        }
    }

    protected ResponseEntity<ResponseEnvelope<List<VO>>> findByOwnerIds(Set<Long> ownerIds, Pageable pageRequest) throws Exception {
        // NOTE: only support to query single VHM in currently
        Page<DO> dataPage = getService().findByOwnerId(ownerIds.iterator().next(), pageRequest);
        return createResponse(dataPage);
    }
}