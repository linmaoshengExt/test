package pers.aidenj.payment.mapper;

import pers.aidenj.payment.model.WeiXinPay;

public interface WeiXinPayMapper {

	int insertSelective(WeiXinPay record);

	WeiXinPay findOrderNumber(String orderNumber);

	int updateByPrimaryKeySelective(WeiXinPay record);
}