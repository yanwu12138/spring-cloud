package com.yanwu.spring.cloud.common.mvc.controller;

import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import com.yanwu.spring.cloud.common.mvc.vo.ValueObject;
import com.yanwu.spring.cloud.common.service.SimpleCrudService;
import com.yanwu.spring.cloud.common.service.SimpleMonopolyNamedCrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public abstract class BaseMonopolyCrudRestController<VO extends ValueObject, DO extends BaseObject, SERVICE extends SimpleCrudService<DO>>
		extends CoreCrudRestController<VO, DO, SERVICE> {

	@RequestMapping(value = "", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	protected ResponseEntity<ResponseEnvelope<VO>> findByName(
			@RequestParam(required = false, value = "name") String name) throws Exception {

		List<DO> doList = ((SimpleMonopolyNamedCrudService<DO>) getService()).findByName(name);

		if (!CollectionUtils.isEmpty(doList)) {
			DO do1 = doList.get(0);

			VO savedVo = voDoUtil.convertDoToVo(do1, getVoClass());
			ResponseEnvelope<VO> responseEnv = new ResponseEnvelope<>(savedVo);
			return new ResponseEntity<>(responseEnv, HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}
