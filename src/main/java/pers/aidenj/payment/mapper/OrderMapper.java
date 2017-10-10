package pers.aidenj.payment.mapper;

import java.util.List;

import pers.aidenj.payment.model.Order;

public interface OrderMapper
{
	int insertSelective(Order record);

	Order selectByPrimaryKey(Integer id);

	Order findOrderNumber(String orderNumber);

	int updateByPrimaryKeySelective(Order record);
	
	List<Order> getListOrder(Order order);

}