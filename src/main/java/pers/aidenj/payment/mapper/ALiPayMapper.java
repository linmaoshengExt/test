package pers.aidenj.payment.mapper;

import pers.aidenj.payment.model.ALiPay;

public interface ALiPayMapper
{
	int insertSelective(ALiPay record);

	ALiPay findOrderNumber(String orderNumber);

	int updateByPrimaryKeySelective(ALiPay aLiPay);
}