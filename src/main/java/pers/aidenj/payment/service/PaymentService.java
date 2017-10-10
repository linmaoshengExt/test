package pers.aidenj.payment.service;

import java.util.Map;

import pers.aidenj.payment.model.ALiPayNotifyPojo;
import pers.aidenj.payment.model.Order;


public interface PaymentService
{
	/****************************************************** 新的支付系统 *********************************************************/
	/**
	 * 创建订单
	 * 
	 * @author hj
	 * @param order
	 * @return
	 */
	public Map<String, Object> addOrder(Order order);

	/**
	 * 订单查询
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	public Map<String,Object> getOrderState(String orderNumber);
	/****************************************************** 支付宝  *********************************************************/
	/**
	 * 支付宝APP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	public Map<String, Object> paymentAPPa(String orderNumber);
	
	/**
	 * 支付宝WAP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	public String paymentWAPa(String orderNumber);

	/**
	 * 支付宝WAP支付——老版本
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	public String paymentWAPaOld(String orderNumber);

	/**
	 * 支付宝即时到帐
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	public String paymentARRIVALa(String orderNumber);
	
	/**
	 * 同步回调（支付宝）
	 * 
	 * @author hj
	 * @return
	 */
	public Map<String, Object> verificationAPPa(String resultDate);

	/**
	 * 异步回调（支付宝）
	 * 
	 * @author hj
	 * @return
	 */
	public String payNotifya(ALiPayNotifyPojo aLiPayNotifyPojo);
	
	/****************************************************** 微信  *********************************************************/
	/**
	 * 微信APP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	public Map<String, Object> paymentAPPw(String orderNumber);
	
	/**
	 * 微信WAP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	public String paymentWAPw(String orderNumber);

	/**
	 * 微信扫码支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	public Map<String, Object> paymentNATIVEw(String orderNumber);
	
	/**
	 * 微信公众号支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	public Map<String, Object> paymentJSAPIw(String orderNumber, String codeId);

	/**
	 * 异步回调（微信）
	 * 
	 * @author hj
	 * @return
	 */
	public String payNotifyw(ALiPayNotifyPojo aLiPayNotifyPojo);

	/**
	 * 公众号支付获取openId
	 * 
	 * @author hj
	 * @return
	 */
	public int payOpenId(String codeId, String code);

}
