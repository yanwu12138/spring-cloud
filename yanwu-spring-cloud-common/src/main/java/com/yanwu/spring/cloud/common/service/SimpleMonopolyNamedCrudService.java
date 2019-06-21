package com.yanwu.spring.cloud.common.service;

import com.yanwu.spring.cloud.common.data.entity.BaseObject;

public interface SimpleMonopolyNamedCrudService<DO extends BaseObject>
		extends NamedRoService<DO>, SimpleCrudService<DO> {

}
