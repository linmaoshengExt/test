package pers.aidenj.payment.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;

import pers.aidenj.payment.common.AlipayCore;
import pers.aidenj.payment.common.AlipayRSA;
import pers.aidenj.payment.common.ConvertUtil;
import pers.aidenj.payment.common.DataConstant;
import pers.aidenj.payment.common.FileUtil;
import pers.aidenj.payment.common.HttpsUtil;
import pers.aidenj.payment.common.MD5;
import pers.aidenj.payment.common.PayUtil;
import pers.aidenj.payment.common.ResultMap;
import pers.aidenj.payment.mapper.ALiPayMapper;
import pers.aidenj.payment.mapper.OrderMapper;
import pers.aidenj.payment.mapper.WeiXinPayMapper;
import pers.aidenj.payment.mapper.WeixinPayOpenIdMapper;
import pers.aidenj.payment.model.ALiPay;
import pers.aidenj.payment.model.ALiPayNotifyPojo;
import pers.aidenj.payment.model.Order;
import pers.aidenj.payment.model.WeiXinPay;
import pers.aidenj.payment.model.WeixinPayOpenId;
import pers.aidenj.payment.service.PaymentService;

@Service("paymentService")
public class PaymentServiceImpl implements PaymentService
{
	protected final Log log = LogFactory.getLog(getClass());

	// PKCS8私钥
	private static final String ALIPAY_RSA_PRIVATE_KEY_PKCS8 = FileUtil.initConfigFile("file.properties").get("ALIPAY_RSA_PRIVATE_KEY_PKCS8").toString();
	// 阿里公钥
	private static final String ALIPAY_PUBLIC_KEY = FileUtil.initConfigFile("file.properties").get("ALIPAY_PUBLIC_KEY").toString();

	@Resource
	private OrderMapper orderMapper;

	@Resource
	private ALiPayMapper aLiPayMapper;

	@Resource
	private WeiXinPayMapper weiXinPayMapper;
	
	@Resource
	private WeixinPayOpenIdMapper weixinPayOpenIdMapper;

	/**
	 * 创建订单
	 * 
	 * @author hj
	 * @param order
	 * @return
	 */
	@Override
	public Map<String, Object> addOrder(Order order)
	{
		// TODO Auto-generated method stub
		Date date = new Date();
		String dateString = String.valueOf(date.getTime());
		String ordernumber = DataConstant.ALIPAY_APPID.substring(8, DataConstant.ALIPAY_APPID.length()) + dateString.substring(dateString.length() - 6, dateString.length()) + order.getUserid() + (new Random().nextInt(8999) + 1000);
		double totalOrderAmount = order.getCommodityprice() * order.getCommoditynumber();
		// 订单数据
		order.setOrdernumber(ordernumber);// 订单号
		order.setOrderamount(totalOrderAmount);// 订单总金额
		order.setStarttime(date);// 创建时间
		order.setOrderstatus(DataConstant.ORDERSTATUS_WAIT);// 订单状态（待支付）

		// 支付宝数据
		ALiPay aLiPay = new ALiPay();
		aLiPay.setOrdernumber(ordernumber);
		aLiPay.setAppid(DataConstant.ALIPAY_APPID);
		aLiPay.setFormat(DataConstant.ALIPAY_FORMAT);
		aLiPay.setCharset(DataConstant.ALIPAY_CHARSET);
		aLiPay.setSigntype(DataConstant.ALIPAY_SIGN_TYPE);
		aLiPay.setTimestamp(date);
		aLiPay.setVersion(DataConstant.ALIPAY_VERSION);
		aLiPay.setSellerid(DataConstant.ALIPAY_SELLER_ID);
		aLiPay.setTotalamount(totalOrderAmount);
		aLiPay.setTimeoutexpress(new Date(date.getTime() + (DataConstant.ALIPAY_PAY_TIMEOUTEXPRESS * 60 * 1000)));

		// 微信数据
		WeiXinPay weiXinPay = new WeiXinPay();
		weiXinPay.setOrdernumber(ordernumber);
		// weiXinPay.setMchId(DataConstant.WEIXINPAY_MCH_ID);
		weiXinPay.setDeciceInfo(DataConstant.WEIXINPAY_DECICE_INFO);
		weiXinPay.setNonceStr(MD5.stringToMD5(ordernumber));
		weiXinPay.setTotalFee(totalOrderAmount);
		weiXinPay.setSpbillCreateIp(order.getIp());
		weiXinPay.setTimeStart(date);

		Map<String,Object> resultMap = new HashMap<String,Object>();
		if (orderMapper.insertSelective(order) > 0 && aLiPayMapper.insertSelective(aLiPay) > 0 && weiXinPayMapper.insertSelective(weiXinPay) > 0)
		{// 下单成功
			resultMap.put("ordernumber", ordernumber);
			return ResultMap.getResultMap(resultMap,"wait");
		}
		else
		{// 下单失败
			return ResultMap.getResultMap(DataConstant.FAIL);
		}
	}

	/**
	 * 订单查询
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	@Override
	public Map<String,Object> getOrderState(String orderNumber)
	{
		// TODO Auto-generated method stub
		Order order = orderMapper.findOrderNumber(orderNumber);
		if (null!=order) {
			return ResultMap.getResultMap(getOfficialOrderState(order),DataConstant.SUCCESS);
		} else {
			return ResultMap.getResultMap(0);
		}
		/*switch (order.getOrderstatus()) {
		case "wait"://待支付
			*//** 查询订单实际结果（防止未回调失败） *//*
			return ResultMap.getResultMap(getOfficialOrderState(order),DataConstant.SUCCESS);
		case "complete"://支付完成
			
			return null;
		case "fail"://支付失败
			
			return null;
		case "cancel"://支付取消
			
			return null;
		case "invalid"://订单失效
			
			return null;

		default:
			
		}*/
		
	}

	/**
	 * 查询官方的订单信息
	 * 
	 * @param order
	 * @return
	 */
	public Map<String,Object> getOfficialOrderState(Order order)
	{
		 Map<String,Object> orderState = new HashMap<String, Object>();
		switch (order.getPaymentmethod())
		{
		case "aliPay":// 支付宝
			orderState = getALiOrderState(order);
			break;
		case "WeiXinPay":// 微信
			orderState = getWXOrderState(order);
			break;
		default:
			//orderState = "非法订单";
			break;
		}
		return orderState;
	}

	/**
	 * 支付宝订单查询（官方）
	 * 
	 * @param orderNumber
	 * @return
	 */
	public Map<String,Object> getALiOrderState(Order order)
	{
		
		 /*List<Order> orderList = new ArrayList<Order>();
		 
		 Order order = new Order();
		 order.setPaymentmethod("aliPay");
		 order.setOrderstatus("wait");
		 orderList = orderMapper.getListOrder(order);
		JSONObject map1 = new JSONObject();
		 for (Order orders : orderList) {*/
			
		
		//请求参数
		Map<String, String> reqData = new HashMap<String, String>();
		reqData.put("app_id", DataConstant.ALIPAY_APPID);
		reqData.put("method", "alipay.trade.query");
		reqData.put("charset", DataConstant.ALIPAY_CHARSET);
		reqData.put("sign_type", DataConstant.ALIPAY_SIGN_TYPE);
		reqData.put("timestamp", ConvertUtil.getNowTimeStr());
		reqData.put("version", DataConstant.ALIPAY_VERSION);

		//结果集
		Map<String, String> bizcontentMap = new HashMap<String, String>();
		bizcontentMap.put("out_trade_no", order.getOrdernumber());//orderNumber);
//		bizcontentMap.put("trade_no", 暂无);
		
		/*结果集*/
		String bizcontent = JSONObject.toJSONString(bizcontentMap);
		log.info("结果集:"+bizcontent);
		reqData.put("biz_content", JSONObject.toJSONString(bizcontentMap));
		/*未签名的原始字符串*/
		String notSign = AlipayCore.createLinkString(reqData);
		/*得到签名*/
		String sign = AlipayRSA.sign(notSign, ALIPAY_RSA_PRIVATE_KEY_PKCS8, DataConstant.ALIPAY_CHARSET);
		reqData.put("sign", sign);
		
		String resoult = HttpsUtil.doPost("https://openapi.alipay.com/gateway.do", reqData);
		log.info("\r"+resoult);
		JSONObject resoultJSON = JSONObject.parseObject(resoult);
		JSONObject alipay_trade_query_response = JSONObject.parseObject(resoultJSON.getString("alipay_trade_query_response"));
		log.info("\r"+alipay_trade_query_response.get("code"));
		log.info("\r"+alipay_trade_query_response.get("msg"));
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		if("10000".equals(alipay_trade_query_response.get("code"))){
			
			//交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
			switch (alipay_trade_query_response.getString("trade_status")) {
			case "WAIT_BUYER_PAY":
				resultMap.put("trade_status", "交易创建，等待付款");
				break;
			case "TRADE_CLOSED":
				resultMap.put("trade_status", "未付款交易超时关闭，或支付完成后全额退款");
				break;
			case "TRADE_SUCCESS":
				resultMap.put("trade_status", "交易支付成功");
				break;
			case "TRADE_FINISHED":
				resultMap.put("trade_status", "交易结束，不可退款");
				break;

			default:
				break;
			}
		}else{
			resultMap.put("trade_status", alipay_trade_query_response.getString("sub_msg"));
		}
		resultMap.put("out_trade_no", alipay_trade_query_response.getString("out_trade_no"));
		resultMap.put("trade_no", alipay_trade_query_response.getString("trade_no"));
		resultMap.put("total_amount", alipay_trade_query_response.getString("total_amount"));
		resultMap.put("create_order_time", order.getStarttime());
		 //}
		 //log.info("\r\r\r最终结果："+map1.toJSONString()+"\r\r\r");
		return resultMap;
	}

	/**
	 * 微信订单查询（官方）
	 * 
	 * @param orderNumber
	 * @return
	 */
	public  Map<String,Object> getWXOrderState(Order order)
	{
		Map<String, String> reqData = new HashMap<String, String>();
		try
		{

			reqData.put("appid", DataConstant.WEIXINPAY_YH_APPID);
			reqData.put("mch_id", DataConstant.WEIXINPAY_YH_MCH_ID_APP);
			// 微信订单号：transaction_id、商户订单号：out_trade_no（二选一）
			// postData.put("transaction_id", value);
			reqData.put("out_trade_no", order.getOrdernumber());
			reqData.put("nonce_str", MD5.stringToMD5(order.getOrdernumber()));
			String sign;
			sign = PayUtil.getWeiXinPaySign(reqData, DataConstant.WEIXINPAY_PAY_YH_KEY);
			reqData.put("sign", sign);
			reqData.put("sign_type", "MD5");
			String xml = PayUtil.toXml(reqData);
			String result = HttpsUtil.postXml("https://api.mch.weixin.qq.com/pay/orderquery", xml);
			System.out.println(result);
			return null;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 支付宝APP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@Override
	public Map<String, Object> paymentAPPa(String orderNumber) {
		// TODO Auto-generated method stub
		Order order = new Order();
		order.setOrdernumber(orderNumber);
		order.setPaymentmethod("aliPay");
		order.setPaymenttype("APP");

		ALiPay aLiPay = new ALiPay();
		aLiPay.setOrdernumber(orderNumber);
		aLiPay.setMethod("alipay.trade.app.pay");

		aLiPay.setNotifyurl(DataConstant.ALIPAY_NOTIFYURL);

		int o = orderMapper.updateByPrimaryKeySelective(order);
		int a = aLiPayMapper.updateByPrimaryKeySelective(aLiPay);
		if (1 == o && 1 == a)
		{
			order = new Order();
			order = orderMapper.findOrderNumber(orderNumber);
			aLiPay = new ALiPay();
			aLiPay = aLiPayMapper.findOrderNumber(orderNumber);

			Map<String, String> reqDataMap = null;
			reqDataMap = new HashMap<String, String>();
			/** 拼接相关参数 */
			reqDataMap = PayUtil.requestData(order, aLiPay, aLiPay.getMethod());
			// 签名添加到数据库中
			aLiPay.setSign(reqDataMap.get("sign"));
			// System.out.println("签名参数：" + reqDataMap.get("sign"));
			aLiPayMapper.updateByPrimaryKeySelective(aLiPay);
			reqDataMap = PayUtil.valueEncode(reqDataMap);
			String reqData = reqDataMap.toString().replaceAll(", ", "&").substring(1, reqDataMap.toString().replaceAll(", ", "&").length() - 1);
			System.out.println("加密后：" + reqData);
			return ResultMap.getResultMap(reqData);
		}
		return null;
	}

	/**
	 * 支付宝WAP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@Override
	public String paymentWAPa(String orderNumber)
	{
		// TODO Auto-generated method stub
		String formData = null;

		Order order = new Order();
		order.setOrdernumber(orderNumber);
		order.setPaymentmethod("aliPay");
		order.setPaymenttype("WAP");
		ALiPay aLiPay = new ALiPay();
		aLiPay.setOrdernumber(orderNumber);
		aLiPay.setMethod("alipay.trade.wap.pay");
		aLiPay.setNotifyurl(DataConstant.ALIPAY_NOTIFYURL);

		int o = orderMapper.updateByPrimaryKeySelective(order);
		int a = aLiPayMapper.updateByPrimaryKeySelective(aLiPay);
		if (1 == o && 1 == a)
		{
			order = new Order();
			order = orderMapper.findOrderNumber(orderNumber);
			aLiPay = new ALiPay();
			aLiPay = aLiPayMapper.findOrderNumber(orderNumber);
			// 参数集合
			Map<String, String> reqDataMap = null;
			reqDataMap = new HashMap<String, String>();
			reqDataMap = PayUtil.requestData(order, aLiPay, aLiPay.getMethod());
			// 签名添加导数据库中
			aLiPay.setSign(reqDataMap.get("sign"));
			aLiPayMapper.updateByPrimaryKeySelective(aLiPay);

			/**
			 * 开始支付
			 */
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", DataConstant.ALIPAY_APPID, ALIPAY_RSA_PRIVATE_KEY_PKCS8, DataConstant.ALIPAY_FORMAT, DataConstant.ALIPAY_CHARSET, ALIPAY_PUBLIC_KEY); // 获得初始化的AlipayClient
			AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
			alipayRequest.setReturnUrl("");
			alipayRequest.setNotifyUrl(DataConstant.ALIPAY_NOTIFYURL);// 在公共参数中设置回跳和通知地址
			alipayRequest.setBizContent(reqDataMap.get("biz_content"));// 填充业务参数

			try
			{
				formData = alipayClient.pageExecute(alipayRequest).getBody();
			}
			catch (AlipayApiException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return formData;

		}
		else
		{
			return formData;
		}
	}

	/**
	 * 支付宝WAP支付——老版本
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@SuppressWarnings("null")
	@Override
	public String paymentWAPaOld(String orderNumber)
	{

		// TODO Auto-generated method stub
		StringBuffer sbHtml = null;

		Order order = new Order();
		order.setOrdernumber(orderNumber);
		order.setPaymentmethod("aliPay");
		order.setPaymenttype("WAP");
		ALiPay aLiPay = new ALiPay();
		aLiPay.setOrdernumber(orderNumber);
		aLiPay.setMethod("alipay.trade.wap.pay");
		aLiPay.setNotifyurl(DataConstant.ALIPAY_NOTIFYURL);

		int o = orderMapper.updateByPrimaryKeySelective(order);
		int a = aLiPayMapper.updateByPrimaryKeySelective(aLiPay);
		if (1 == o && 1 == a)
		{
			order = new Order();
			order = orderMapper.findOrderNumber(orderNumber);
			aLiPay = new ALiPay();
			aLiPay = aLiPayMapper.findOrderNumber(orderNumber);
			// 参数集合
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("service", "alipay.wap.create.direct.pay.by.user");
			dataMap.put("partner", DataConstant.ALIPAY_SELLER_ID);
			dataMap.put("seller_id", DataConstant.ALIPAY_SELLER_ID);
			dataMap.put("_input_charset", DataConstant.ALIPAY_CHARSET);
			dataMap.put("payment_type", "1");// 支付类型固定，无需修改
			dataMap.put("notify_url", DataConstant.ALIPAY_NOTIFYURL);
			// dataMap.put("return_url", DataConstant.ALIPAY_REQUEST_ADDRESS);
			dataMap.put("return_url", "");
			dataMap.put("out_trade_no", order.getOrdernumber());
			dataMap.put("subject", order.getCommodityname());
			dataMap.put("total_fee", String.valueOf(order.getOrderamount()));
			dataMap.put("show_url", "");
			dataMap.put("app_pay", "N");// 启用此参数可唤起钱包APP支付。
			dataMap.put("body", order.getCommoditydescribe());

			/**
			 * 1.删除空值和签名参数 2.拼接参数 3.签名 4.签名添加到请求参数中
			 */
			// 1.删除空值和签名参数
			Map<String, String> reqDataMap = AlipayCore.paraFilter(dataMap);
			// 2.拼接参数
			String notSign = AlipayCore.createLinkString(reqDataMap);
			String sign = AlipayRSA.sign(notSign, ALIPAY_RSA_PRIVATE_KEY_PKCS8, DataConstant.ALIPAY_CHARSET);
			reqDataMap.put("sign", sign);
			reqDataMap.put("sign_type", "RSA");
			// 签名添加导数据库中
			aLiPay.setSign(sign);
			aLiPayMapper.updateByPrimaryKeySelective(aLiPay);

			/**
			 * 开始支付
			 */
			String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
			sbHtml = new StringBuffer();
			sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY_NEW + "_input_charset=" + DataConstant.ALIPAY_CHARSET + "\" method=\"post\">");
			List<String> keys = new ArrayList<String>(reqDataMap.keySet());
			for (int i = 0; i < keys.size(); i++)
			{
				String name = keys.get(i);
				String value = reqDataMap.get(name);

				sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
			}

			// submit按钮控件请不要含有name属性
			sbHtml.append("<input type=\"submit\" value=\"确认支付\" style=\"display:none;\"></form>");
			sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

			return sbHtml.toString();

		}
		else
		{
			return sbHtml.toString();
		}
	}
	/**
	 * 支付宝即时到账
	 * 
	 * @author hj
	 * @param
	 * @return
	 */
	@Override
	public String paymentARRIVALa(String orderNumber)
	{
		// TODO Auto-generated method stub
		Order order = new Order();
		order.setOrdernumber(orderNumber);
		order.setPaymentmethod("aliPay");
		order.setPaymenttype("ARRIVAL");

		ALiPay aLiPay = new ALiPay();
		aLiPay.setOrdernumber(orderNumber);
		aLiPay.setMethod("create_direct_pay_by_user");

		aLiPay.setNotifyurl(DataConstant.ALIPAY_NOTIFYURL);

		int o = orderMapper.updateByPrimaryKeySelective(order);
		int a = aLiPayMapper.updateByPrimaryKeySelective(aLiPay);
		if (1 == o && 1 == a)
		{
			order = new Order();
			order = orderMapper.findOrderNumber(orderNumber);
			aLiPay = new ALiPay();
			aLiPay = aLiPayMapper.findOrderNumber(orderNumber);

			Map<String, String> dataMap = new HashMap<String, String>();
			
			
			/*基本参数*/
			dataMap.put("service", "create_direct_pay_by_user");
			dataMap.put("partner", DataConstant.ALIPAY_SELLER_ID);
			dataMap.put("_input_charset", "utf-8");
			dataMap.put("notify_url", DataConstant.ALIPAY_NOTIFYURL);
			dataMap.put("return_url", FileUtil.initConfigFile("file.properties").get("ALIAPY_ARRIVAL_SYNCHRONIZATION").toString());
			/*业务参数*/
			dataMap.put("out_trade_no", order.getOrdernumber());
			dataMap.put("subject", order.getCommodityname());
			dataMap.put("payment_type", "1");
			dataMap.put("total_fee", String.valueOf(order.getOrderamount()));
			dataMap.put("seller_id", DataConstant.ALIPAY_SELLER_ID);
			dataMap.put("body", order.getCommoditydescribe());
			
			// dataMap.put("qr_pay_mode", "2");
			// dataMap.put("goods_type", "0");

			//除去数组中的空值和签名参数
			Map<String, String> sPara = AlipayCore.paraFilter(dataMap);
			//拼接签名参数
			String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
			//获取签名
			String sign = AlipayRSA.sign(prestr, ALIPAY_RSA_PRIVATE_KEY_PKCS8, DataConstant.ALIPAY_CHARSET);
			System.out.println(DataConstant.ALIPAY_CHARSET);
			dataMap.put("sign", sign);
			dataMap.put("sign_type", DataConstant.ALIPAY_SIGN_TYPE);

			List<String> keys = new ArrayList<String>(dataMap.keySet());
			StringBuffer sbHtml = new StringBuffer();
			sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + DataConstant.ALIPAY_GATEWAY_NEW
					+ "_input_charset=" + DataConstant.ALIPAY_CHARSET + "\" method=\"get\">");
			for (int i = 0; i < keys.size(); i++) {
	            String name = keys.get(i);
				String value = dataMap.get(name);

				sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
			}
			sbHtml.append("<input type=\"submit\" value=\"确认\" style=\"display:none;\"></form>");
			sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
			System.out.println(sbHtml.toString());
			return sbHtml.toString();
		}
		return null;
	}
	
	/**
	 * APP同步验证支付结果（支付宝）
	 * 
	 * @author hj
	 * @param
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public Map<String, Object> verificationAPPa(String resultDate)
	{
		// TODO Auto-generated method stub
		System.out.println("\r\r\r支付宝同步回调参数结果resultDate:" + resultDate);
		Map<String, Object> resultMap = null;
		JSONObject resultJson = JSONObject.parseObject(resultDate);
		// 结果码
		String resultStatus = resultJson.getString("resultStatus");
		resultMap = new HashMap<String, Object>();
		switch (resultStatus)
		{
		case "9000":// 订单支付成功
			// 描述信息
			String memo = resultJson.getString("memo");
			// System.out.println("描述信息：" + memo);

			// 处理结果集
			JSONObject result = JSONObject.parseObject(resultJson.getString("result"));
			// 订单信息
			JSONObject response = result.getJSONObject("alipay_trade_app_pay_response");
			// 订单号
			String orderNumber = response.getString("out_trade_no");

			Order order = orderMapper.findOrderNumber(orderNumber);
			ALiPay aLiPay = aLiPayMapper.findOrderNumber(orderNumber);

			resultMap = PayUtil.orderVerification(order, aLiPay, resultDate);
			if (order == null && aLiPay == null)
			{
				resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_NOT_ORDER);
			}
			else
			{
				resultMap = PayUtil.orderVerification(order, aLiPay, resultDate);
			}
			break;
		case "8000":// 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_TREATMENT);
			break;
		case "4000":// 订单支付失败
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_FAIL);
			break;
		case "5000":// 重复请求
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_REPEAT);
			break;
		case "6001":// 用户中途取消
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_CANCEL);
			break;
		case "6002":// 网络连接出错
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_NETWORK_ERROR);
			break;
		case "6004":// 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_RESULT_UNKNOWN);
			break;
		default:// 其它支付错误
			resultMap = ResultMap.getResultMap(DataConstant.FAIL);
			break;
		}
		return resultMap;
	}

	/**
	 * APP异步回调验证支付结果（支付宝）
	 * 
	 * @author hj
	 * @param aLiPayNotifyPojo
	 */
	@Override
	public String payNotifya(ALiPayNotifyPojo aLiPayNotifyPojo)
	{
		JSONObject aliResultJson = JSONObject.parseObject(JSONObject.toJSONString(aLiPayNotifyPojo));
		System.out.println("进入异步传参" + aliResultJson.toJSONString());

		aliResultJson.remove("sign");
		aliResultJson.remove("sign_type");

		/*删除sign, sign_type*/
		Map<String, Object> aliResultMap = JSONObject.parseObject(JSONObject.toJSONString(aLiPayNotifyPojo));
		//aliResultMap.remove("sign");
		//aliResultMap.remove("sign_type");
		/*解密*/
		// PayUtil.valueEncode(aliResultMap);
		

		Map<String, String> valueDecode = new HashMap<String, String>();
		List<String> keys = new ArrayList<String>(aliResultMap.keySet());
		for (int i = 0; i < keys.size(); i++)
		{
			String key = keys.get(i);
			String value = aliResultMap.get(key).toString();
			valueDecode.put(key, value);
		}

		// try
		// {
			/** 验证方案1 */
			// boolean a = AlipaySignature.rsaCheckV1(valueDecode,
			// DataConstant.ALIPAY_PUBLIC_KEY, DataConstant.ALIPAY_CHARSET);
			// System.out.println(a);

			/** 验证方案2 */
			// boolean b = AlipaySignature.rsaCheckV2(valueDecode,
			// DataConstant.ALIPAY_PUBLIC_KEY, DataConstant.ALIPAY_CHARSET);
			// System.out.println(b);

			/** 验证方案3 */
			// String sign = aliResultMap.get("sign").toString();
			// 第一步：除去sign、sign_type
			// aliResultMap.remove("sign");
			// aliResultMap.remove("sign_type");
			// 第二部：将参数进行解密decode, 然后进行字典排序，组成字符串，得到待签名字符串
			// valueDecode = PayUtil.valueDecode(aliResultMap);
			// String notSign = AlipayCore.createLinkString(valueDecode);

			// boolean c = AlipaySignature.rsaCheckContent(notSign, sign,
			// DataConstant.ALIPAY_PUBLIC_KEY, DataConstant.ALIPAY_CHARSET);
			// System.out.println(c);
		// }
		// catch (AlipayApiException e)
		// {
			// TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// 支付宝返回结果
		// Map<String, Object> notifyMap =
		// ConvertUtil.convertBean(aLiPayNotifyPojo);
		// 没有sign和sign_type参数
		// Map<String, String> notify = AlipayCore.paraFilter(notifyMap);


		/*try
		{*/
		// TODO Auto-generated method stub
		// 根据返回的订单参数查找订单相关信息
		Date date;
		Order order;
		Order orders;
		ALiPay aLiPays;

		orders = orderMapper.findOrderNumber(aLiPayNotifyPojo.getOut_trade_no());
		aLiPays = aLiPayMapper.findOrderNumber(aLiPayNotifyPojo.getOut_trade_no());

		// 订单是否存在
		if (orders != null && aLiPays != null)
		{
			date = new Date();

			// 未解密
			Map<String, Object> notifyMap = ConvertUtil.convertBean(aLiPayNotifyPojo);
			// 解密后
			Map<String, String> notifyDecode = PayUtil.valueDecode(notifyMap);
			// 没有sign和sign_type参数
			Map<String, String> notify = AlipayCore.paraFilter(notifyDecode);
			// 未签名字符串
			String notSign = AlipayCore.createLinkString(notify);
			System.out.println(notSign);

			// 验证签名是否正确
			// boolean signB;
			// signB = AlipayRSA.verify(notSign, notifyDecode.get("sign"),
			// DataConstant.ALIPAY_PUBLIC_KEY, DataConstant.ALIPAY_CHARSET);
			// System.out.println("异步签名验证结果1：" + signB);

			// signB = AlipaySignature.rsaCheckContent(notSign,
			// notifyDecode.get("sign"), DataConstant.ALIPAY_PUBLIC_KEY,
			// DataConstant.ALIPAY_CHARSET);
			// System.out.println("异步签名验证结果2：" + signB);

			// Map<String, String> paramsMap = valueDecode(notifyMap);//
			// 将异步通知中收到的待验证所有参数都存放到map中
			// boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap,
			// DataConstant.ALIPAY_PUBLIC_KEY, DataConstant.ALIPAY_CHARSET); //
			// 调用SDK验证签名
			// System.out.println("异步签名验证结果3：" + signVerified);

			// signB =
			// AlipaySignature.rsaCheckV2(params,DataConstant.ALIPAY_PUBLIC_KEY,
			// DataConstant.ALIPAY_CHARSET);

			/*if (signB)
			{*/
			// 订单号 && 总金额 && 商户ID &&　应用ID
			// 判断 订单号 && 总金额 && 商户ID &&

			// 判断订单信息 boolean
			boolean orderProofreading = PayUtil.orderInformationProofreading(orders, aLiPays, aLiPayNotifyPojo);

			// 回调订单状态
			if (orderProofreading && ("TRADE_SUCCESS".equals(aLiPayNotifyPojo.getTrade_status()) || ("TRADE_FINISHED".equals(aLiPayNotifyPojo.getTrade_status()))))
			{
				order = new Order();
				order.setOrdernumber(orders.getOrdernumber());
				order.setEndtime(date);
				order.setOrderstatus(DataConstant.ORDERSTATUS_COMPLETE);
				orderMapper.updateByPrimaryKeySelective(order);
			}
			else
			{
				order = new Order();
				order.setOrdernumber(orders.getOrdernumber());
				order.setEndtime(date);
				order.setOrderstatus(DataConstant.ORDERSTATUS_FAIL);
				orderMapper.updateByPrimaryKeySelective(order);
			}

		}

		return "success";
		/*}
		catch (AlipayApiException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "success";
		}*/

	}
	
	/****************************************************** 新的支付系统（微信：1、APP支付；2、扫码支付；3、WAP支付（官方平台暂未开启此接口）；4、公众号支付） *********************************************************/
	/**
	 * 微信APP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@Override
	public Map<String, Object> paymentAPPw(String orderNumber)
	{
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = null;
		try
		{
			Order order = new Order();
			order.setOrdernumber(orderNumber);
			order.setPaymentmethod("WeiXinPay");
			order.setPaymenttype("APP");

			WeiXinPay weiXinPay = new WeiXinPay();
			weiXinPay.setOrdernumber(orderNumber);
			weiXinPay.setTradeType("APP");
			weiXinPay.setMchId(DataConstant.WEIXINPAY_YH_MCH_ID_APP);

			int o = orderMapper.updateByPrimaryKeySelective(order);
			int w = weiXinPayMapper.updateByPrimaryKeySelective(weiXinPay);

			if (1 == o && 1 == w)
			{
				order = new Order();
				order = orderMapper.findOrderNumber(orderNumber);

				weiXinPay = new WeiXinPay();
				weiXinPay = weiXinPayMapper.findOrderNumber(orderNumber);

				// 下单后的返回参数

				// String resultData =
				// JSONObject.toJSONString(getWeiXinPayXml(order, weiXinPay));
				Map<String, String> resultData = PayUtil.getWeiXinPayXml(order, weiXinPay, 1);

				// System.out.println("返回给移动端的结果集：" + resultData);
				if (resultData != null && !"".equals(resultData))
				{
					resultMap = ResultMap.getResultMap(resultData);

				}
				else
				{
					resultMap = ResultMap.getResultMap(DataConstant.FAIL);
				}
			}
			else
			{
				resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_NOT_ORDER);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap = ResultMap.getResultMap(DataConstant.FAIL);
		}
		return resultMap;
	}
	
	/**
	 * 微信扫码支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	@Override
	public Map<String, Object> paymentNATIVEw(String orderNumber)
	{
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = null;
		try
		{
			Order order = new Order();
			order.setOrdernumber(orderNumber);
			order.setPaymentmethod("WeiXinPay");
			order.setPaymenttype("NATIVE");

			WeiXinPay weiXinPay = new WeiXinPay();
			weiXinPay.setOrdernumber(orderNumber);
			weiXinPay.setTradeType("NATIVE");

			int o = orderMapper.updateByPrimaryKeySelective(order);
			int w = weiXinPayMapper.updateByPrimaryKeySelective(weiXinPay);

			if (1 == o && 1 == w)
			{
				order = new Order();
				order = orderMapper.findOrderNumber(orderNumber);

				weiXinPay = new WeiXinPay();
				weiXinPay = weiXinPayMapper.findOrderNumber(orderNumber);

				// 下单后的返回参数
				Map<String, String> resultData = PayUtil.getWeiXinPayXml(order, weiXinPay, 2);
				System.out.println(resultData);

				if (resultData != null && !"".equals(resultData))
				{
					resultMap = ResultMap.getResultMap(resultData);

				}
				else
				{
					resultMap = ResultMap.getResultMap(DataConstant.FAIL);
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap = ResultMap.getResultMap(DataConstant.FAIL);
		}
		return resultMap;
	}

	/**
	 * 微信WAP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public String paymentWAPw(String orderNumber)
	{
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = null;
		try
		{
			Order order = new Order();
			order.setOrdernumber(orderNumber);
			order.setPaymentmethod("WeiXinPay");
			order.setPaymenttype("WAP");

			WeiXinPay weiXinPay = new WeiXinPay();
			weiXinPay.setOrdernumber(orderNumber);
			weiXinPay.setTradeType("MWEB");

			int o = orderMapper.updateByPrimaryKeySelective(order);
			int w = weiXinPayMapper.updateByPrimaryKeySelective(weiXinPay);

			if (1 == o && 1 == w)
			{
				order = new Order();
				order = orderMapper.findOrderNumber(orderNumber);

				weiXinPay = new WeiXinPay();
				weiXinPay = weiXinPayMapper.findOrderNumber(orderNumber);

				// 下单后的返回参数

				// String resultData =
				// JSONObject.toJSONString(getWeiXinPayXml(order, weiXinPay));
				Map<String, String> resultData = PayUtil.getWeiXinPayXml(order, weiXinPay, 3);

				// System.out.println("返回给移动端的结果集：" + resultData);
				if (resultData != null && !"".equals(resultData))
				{
					resultMap = ResultMap.getResultMap(resultData);

				}
				else
				{
					resultMap = ResultMap.getResultMap(resultData);
				}
			}
			else
			{
				resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_NOT_ORDER);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap = ResultMap.getResultMap(DataConstant.FAIL);
		}
		return null;
	}
	
	/**
	 * 微信公众号支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param codeId
	 * @return
	 */
	@Override
	public Map<String, Object> paymentJSAPIw(String orderNumber, String codeId)
	{
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = null;
		System.out.println("传过来的参数：" + orderNumber + "	" + codeId);
		try
		{
			Order order = new Order();
			order.setOrdernumber(orderNumber);
			order.setPaymentmethod("WeiXinPay");
			order.setPaymenttype("JSAPI");

			WeiXinPay weiXinPay = new WeiXinPay();
			weiXinPay.setOrdernumber(orderNumber);
			weiXinPay.setTradeType("JSAPI");
			weiXinPay.setMchId(DataConstant.WEIXINPAY_XLD_MCH_ID_JSAPI);

			WeixinPayOpenId weixinPayOpenId = new WeixinPayOpenId();
			weixinPayOpenId.setCodeid(codeId);
			weixinPayOpenId.setOrdernumber(orderNumber);

			int o = orderMapper.updateByPrimaryKeySelective(order);
			int w = weiXinPayMapper.updateByPrimaryKeySelective(weiXinPay);
			int wo = weixinPayOpenIdMapper.updateByPrimaryKeySelective(weixinPayOpenId);
			System.out.println("保存的结果：" + o + "  " + w + "  " + wo);
			if (1 == o && 1 == w && 1 == wo)
			{
				order = new Order();
				order = orderMapper.findOrderNumber(orderNumber);

				weiXinPay = new WeiXinPay();
				weiXinPay = weiXinPayMapper.findOrderNumber(orderNumber);

				weixinPayOpenId = new WeixinPayOpenId();
				weixinPayOpenId = weixinPayOpenIdMapper.findOrderNumber(orderNumber);

				weiXinPay.setOpenid(weixinPayOpenId.getOpenid());
				// 下单后的返回参数
				Map<String, String> resultData = PayUtil.getWeiXinPayXml(order, weiXinPay, 4);
				System.out.println(resultData);
				if (resultData != null)
				{
					if ("FAIL".equals(resultData.get("resultcode")))
					{
						// 下单错误结果判断
						resultMap = PayUtil.resoultSwitch(resultData.get("errcode"));
					}
					else
					{
						resultMap = ResultMap.getResultMap(resultData);
					}
				}
				else
				{
					resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_NOT_ORDER);
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap = ResultMap.getResultMap(DataConstant.FAIL);
		}
		return resultMap;
	}
	
	/**
	 * APP异步回调验证支付结果（微信）
	 * 
	 * @author hj
	 * @param aLiPayNotifyPojo
	 */
	@Override
	public String payNotifyw(ALiPayNotifyPojo aLiPayNotifyPojo)
	{
		// TODO Auto-generated method stub

		System.out.println("异步回调参数：" + ConvertUtil.convertBean(aLiPayNotifyPojo).toString());
		System.out.println(aLiPayNotifyPojo.getReturn_code());
		System.out.println(aLiPayNotifyPojo.getReturn_msg());

		// 根据返回的订单参数查找订单相关信息
		// Date date = new Date();
		Map<String, String> resultMap = new HashMap<String, String>();
		Order order = orderMapper.findOrderNumber(aLiPayNotifyPojo.getOut_trade_no());
		WeiXinPay weiXinPay = weiXinPayMapper.findOrderNumber(aLiPayNotifyPojo.getOut_trade_no());

		// 订单是否存在
		if (order != null && weiXinPay != null)
		{// 判断订单是否存在
			if ("SUCCESS".equals(aLiPayNotifyPojo.getReturn_code()))
			{// 判断回调结果是否成功
				if (weiXinPay.getVerification() == 0)
				{// 判断订单是否处理过（0表示未处理）
					resultMap = PayUtil.weiXinVerification(aLiPayNotifyPojo, order, weiXinPay);
					if ("SUCCESS".equals(resultMap.get("return_code").toString()))
					{
						Order orders = new Order();
						orders.setOrdernumber(order.getOrdernumber());
						orders.setEndtime(new Date());
						orders.setOrderstatus(DataConstant.ORDERSTATUS_COMPLETE);
						orderMapper.updateByPrimaryKeySelective(orders);
					}
					else
					{
						Order orders = new Order();
						orders.setOrdernumber(order.getOrdernumber());
						orders.setEndtime(new Date());
						orders.setOrderstatus(DataConstant.ORDERSTATUS_FAIL);
						orderMapper.updateByPrimaryKeySelective(orders);
					}
				}
				else
				{
					resultMap.put("return_code", "SUCCESS");
					resultMap.put("return_msg", "OK");
				}
			}
			else
			{
				resultMap.put("return_code", aLiPayNotifyPojo.getReturn_code());
				resultMap.put("return_msg", aLiPayNotifyPojo.getReturn_msg());
			}
		}
		else
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "订单不存在");
		}
		return PayUtil.toXml(resultMap);
	}
	
	/**
	 * 保存获取公众账号用户的OpenId
	 * 
	 * @author hj
	 * @return
	 */
	@Override
	public int payOpenId(String codeId, String code)
	{
		// TODO Auto-generated method stub
		System.out.println("将要保存：codeId=" + codeId);
		System.out.println("将要保存：code=" + code);
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("appid", DataConstant.WEIXINPAY_YH_OFFICIALACCOUNTS);
		reqMap.put("secret", DataConstant.WEIXINPAY_YH_PAY_APPSECRET);
		reqMap.put("code", code);
		reqMap.put("grant_type", "authorization_code");
		String resultStr = HttpsUtil.doPost("https://api.weixin.qq.com/sns/oauth2/access_token", reqMap);
		System.out.println("通过code获取到的参数值：" + resultStr);
		JSONObject resultJson = JSONObject.parseObject(resultStr);
		System.out.println("解析后的参数" + resultJson.toJSONString());
		WeixinPayOpenId weixinPayOpenId = new WeixinPayOpenId();
		weixinPayOpenId.setCodeid(codeId);
		weixinPayOpenId.setCode(code);
		weixinPayOpenId.setOpenid(resultJson.getString("openid"));
		int i = weixinPayOpenIdMapper.insertSelective(weixinPayOpenId);
		System.out.println("数据库保存结果：" + i);
		return i;
	}
	
}
