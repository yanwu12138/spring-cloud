package com.yanwu.spring.cloud.common.service;

import com.yanwu.spring.cloud.common.data.entity.BaseObject;

public interface SimpleQuoteCrudService<DO extends BaseObject> extends QuoteRoService<DO>, SimpleCrudService<DO> {
}
