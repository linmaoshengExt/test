package pers.aidenj.payment.mapper;

import pers.aidenj.payment.model.WeixinPayOpenId;

public interface WeixinPayOpenIdMapper {

    int insertSelective(WeixinPayOpenId record);

	int updateByPrimaryKeySelective(WeixinPayOpenId record);

	WeixinPayOpenId findOrderNumber(String orderNumber);
}