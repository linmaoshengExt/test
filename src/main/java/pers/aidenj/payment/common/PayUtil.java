package pers.aidenj.payment.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.XML;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;

import pers.aidenj.payment.model.ALiPay;
import pers.aidenj.payment.model.ALiPayNotifyPojo;
import pers.aidenj.payment.model.Order;
import pers.aidenj.payment.model.WeiXinPay;

public class PayUtil {
	
	// PKCS8私钥
	private static final String ALIPAY_RSA_PRIVATE_KEY_PKCS8 = FileUtil.initConfigFile("file.properties").get("ALIPAY_RSA_PRIVATE_KEY_PKCS8").toString();
	// 阿里公钥
	private static final String ALIPAY_PUBLIC_KEY = FileUtil.initConfigFile("file.properties").get("ALIPAY_PUBLIC_KEY").toString();
	
	/****************************支付宝自定义工具**********************************/
	/**
	 * 支付宝参数拼接
	 * 
	 * @author hj
	 * @param order
	 * @param aLiPay
	 * @return
	 */
	public static Map<String, String> requestData(Order order, ALiPay aLiPay, String method)
	{
		
		/*业务请求参数的集合*/
		Map<String, Object> bizcontentMap = new HashMap<String, Object>();
		bizcontentMap.put("body", order.getCommoditydescribe());
		bizcontentMap.put("subject", order.getCommodityname());
		bizcontentMap.put("out_trade_no", order.getOrdernumber());
		bizcontentMap.put("timeout_express", String.valueOf(DataConstant.ALIPAY_PAY_TIMEOUTEXPRESS) + "m");
		bizcontentMap.put("total_amount", order.getOrderamount());
		bizcontentMap.put("seller_id", DataConstant.ALIPAY_SELLER_ID);
		if ("APP".equals(order.getPaymenttype()) || "APP" == order.getPaymenttype())
		{
			bizcontentMap.put("product_code", DataConstant.ALIPAY_PRODUCT_CODE_APP);
		}
		else
		{
			bizcontentMap.put("product_code", DataConstant.ALIPAY_PRODUCT_CODE_WAP);
		}

		/*结果集*/
		String bizcontent = JSONObject.toJSONString(bizcontentMap);
		aLiPay.setBizcontent(bizcontent);// 结果集

		/* 未签名的原始字符集*/
		Map<String, String> reqDataMap = new HashMap<String, String>();
		reqDataMap.put("app_id", aLiPay.getAppid());
		reqDataMap.put("biz_content", bizcontent);
		reqDataMap.put("charset", aLiPay.getCharset());
		reqDataMap.put("format", aLiPay.getFormat());
		reqDataMap.put("method", method);
		reqDataMap.put("notify_url", aLiPay.getNotifyurl());
		reqDataMap.put("sign_type", aLiPay.getSigntype());
		reqDataMap.put("timestamp", ConvertUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		reqDataMap.put("version", aLiPay.getVersion());

		/*未签名的原始字符串*/
		String notSign = AlipayCore.createLinkString(reqDataMap);
		// System.out.println("未签名：" + notSign);
		/*得到签名*/
		String sign = AlipayRSA.sign(notSign, ALIPAY_RSA_PRIVATE_KEY_PKCS8, DataConstant.ALIPAY_CHARSET);
		// System.out.println("已签名：" + sign);

		reqDataMap.put("sign", sign);

		// 请求参数
		return reqDataMap;
	}

	/**
	 * URL加密
	 * 
	 * @author hj
	 * @param valueMap
	 * @return
	 */
	public static Map<String, String> valueEncode(Map<String, String> valueMap)
	{
		try
		{
			List<String> keys = new ArrayList<String>(valueMap.keySet());
			for (int i = 0; i < keys.size(); i++)
			{
				String key = keys.get(i);
				String value = URLEncoder.encode(valueMap.get(key), DataConstant.ALIPAY_CHARSET);
				valueMap.put(key, value);
			}
			return valueMap;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * URL解密
	 * 
	 * @author hj
	 * @param valueMap
	 * @return
	 */
	public static Map<String, String> valueDecode(Map<String, Object> valueMap)
	{
		try
		{
			Map<String, String> valueDecode = new HashMap<String, String>();
			List<String> keys = new ArrayList<String>(valueMap.keySet());
			for (int i = 0; i < keys.size(); i++)
			{
				String key = keys.get(i);
				String value = URLDecoder.decode(valueMap.get(key).toString(), DataConstant.ALIPAY_CHARSET);
				valueDecode.put(key, value);
			}
			return valueDecode;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 验证订单参数是否正确
	 * 
	 * @author hj
	 * @param order
	 *            订单信息
	 * @param responseData
	 *            支付返回信息
	 * @return
	 * @throws AlipayApiException
	 */
	public static Map<String, Object> orderVerification(Order order, ALiPay aLiPay, String responseData)
	{
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = null;
		try
		{

			// 返回参数结果
			JSONObject resultJson = JSONObject.parseObject(responseData);
			// 订单相关信息
			JSONObject result = resultJson.getJSONObject("result");
			// 订单信息
			JSONObject orderInformation = result.getJSONObject("alipay_trade_app_pay_response");
			// 签名原始字符串
			// String notSign =
			// result.getString("alipay_trade_app_pay_response");
			String notSign = "{\"code\":\"10000\",\"msg\":\"Success\",\"app_id\":\"2016102502317142\",\"auth_app_id\":\"2016102502317142\",\"charset\":\"utf-8\",\"timestamp\":\"2016-11-18 10:42:08\",\"total_amount\":\"0.01\",\"trade_no\":\"2016111821001004870250689789\",\"seller_id\":\"2088011502416521\",\"out_trade_no\":\"0231714288440715843\"},\"sign\":\"A/UKsEUzxVsBHAhYiOotqHryQdty74b9HUZZc6QdAan4/d5SJU9TLUMfVu2ti8+it4y2irLsW5ngscOOfgJFhOvyH+sCSLsAwcxFhOiTjfcN86R4xOFLTIyY5JYIdK0WTnB7jIvfnyGYHVhwmsXuczgelPzaXNF5O9GwvSyRdhA=\",\"sign_type\":\"RSA\"}";
			String sign = "A/UKsEUzxVsBHAhYiOotqHryQdty74b9HUZZc6QdAan4/d5SJU9TLUMfVu2ti8+it4y2irLsW5ngscOOfgJFhOvyH+sCSLsAwcxFhOiTjfcN86R4xOFLTIyY5JYIdK0WTnB7jIvfnyGYHVhwmsXuczgelPzaXNF5O9GwvSyRdhA=";
			String alipay_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
			String charset = "utf-8";
			/*
			 * 验证签名是否合法
			 */
			boolean verify;
			verify = AlipaySignature.rsaCheckContent(notSign, result.getString("sign"), ALIPAY_PUBLIC_KEY, orderInformation.getString("charset"));
			System.out.println("同步签名验证结果1：" + verify);

			verify = AlipaySignature.rsaCheckContent(notSign, sign, alipay_public_key, charset);
			System.out.println("同步签名验证结果2：" + verify);
			if (verify)
			{
				// 订单号 && 总金额 && 商户ID &&　应用ID
				if (((order.getOrdernumber().equals(orderInformation.getString("out_trade_no"))) || (order.getOrdernumber() == orderInformation.getString("out_trade_no"))) && (order.getOrderamount().equals(orderInformation.getDouble("total_amount")))
						&& (aLiPay.getSellerid().equals(orderInformation.getString("seller_id")) || (aLiPay.getSellerid() == orderInformation.getString("seller_id")) && (aLiPay.getAppid().equals(orderInformation.getString("app_id")) || (aLiPay.getAppid() == orderInformation.getString("app_id")))))
				{
					resultMap = new HashMap<String, Object>();
					resultMap.put("resultType", "success");
					resultMap.put("resultCode", DataConstant.ALIPAY_RESULTSTATUS_CODE_SUCCESS);
					resultMap.put("resultData", order.getCommoditynumber());
					resultMap.put("resultMessage", "支付成功");
				}
				else
				{
					resultMap = new HashMap<String, Object>();
					resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_ORDER_INFORMATION);
				}
			}
			else
			{
				resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_SIGN_FAIL);
			}
			return resultMap;
		}
		catch (AlipayApiException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return resultMap;
		}
	}
	
	/**
	 * 订单信息校对
	 * 
	 * @author hj
	 * @return
	 */
	public static boolean orderInformationProofreading(Order order, ALiPay aLiPay, ALiPayNotifyPojo aLiPayNotifyPojo)
	{
		// 判断订单号是否存在
		if (!order.getOrdernumber().equals(aLiPayNotifyPojo.getOut_trade_no()))
		{
			return false;
		}
		else if (!aLiPay.getSellerid().equals(aLiPayNotifyPojo.getSeller_id()))
		{
			return false;
		}
		else if ((aLiPayNotifyPojo.getApp_id() != null) && (aLiPayNotifyPojo.getApp_id().equals("")) && (aLiPay.getAppid().equals(aLiPayNotifyPojo.getApp_id()) || (aLiPay.getAppid() == aLiPayNotifyPojo.getApp_id())))
		{
			if (order.getOrderamount().equals(Double.valueOf(aLiPayNotifyPojo.getTotal_amount())))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (order.getOrderamount().equals(Double.valueOf(aLiPayNotifyPojo.getTotal_fee())))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/****************************微信自定义工具**********************************/
	
	/**
	 * 微信异步回调参数验证
	 * 
	 * @author hj
	 * @param aLiPayNotifyPojo
	 * @param order
	 * @param weiXinPay
	 * @return
	 */
	public static Map<String, String> weiXinVerification(ALiPayNotifyPojo aLiPayNotifyPojo, Order order, WeiXinPay weiXinPay)
	{
		// TODO Auto-generated method stub
		Map<String, String> resultMap = new HashMap<String, String>();
		/**
		 * 验证返回参数和原订单参数是否相同
		 */
		// 应用ID
		if (!DataConstant.WEIXINPAY_YH_APPID.equals(aLiPayNotifyPojo.getAppid()) || DataConstant.WEIXINPAY_YH_APPID != aLiPayNotifyPojo.getAppid())
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "应用ID错误");
		}
		// 商户号
		/*else if (!DataConstant.WEIXINPAY_xld_MCH_ID.equals(aLiPayNotifyPojo.getMch_id()) || DataConstant.WEIXINPAY_MCH_ID != aLiPayNotifyPojo.getMch_id())
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "商户号错误");
		}*/
		// 随机字符串
		else if (!weiXinPay.getNonceStr().equals(aLiPayNotifyPojo.getNonce_str()) || weiXinPay.getNonceStr() != aLiPayNotifyPojo.getNonce_str())
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "随即字符串错误");
		}
		// 签名
		else if (!weiXinPay.getSign().equals(aLiPayNotifyPojo.getSign()) || weiXinPay.getSign() != aLiPayNotifyPojo.getSign())
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "签名错误");
		}
		// 用户标识
		else if (order.getUserid() != Long.parseLong(aLiPayNotifyPojo.getOpenid()))
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "用户不存在");
		}
		// 交易类型
		else if (!order.getPaymenttype().equals(aLiPayNotifyPojo.getTrade_type()) || order.getPaymenttype() != aLiPayNotifyPojo.getTrade_type())
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "交易类型错误");
		}
		// 总金额
		else if (order.getOrderamount() != Long.parseLong(aLiPayNotifyPojo.getTotal_fee()))
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "金额参数不对");
		}
		// 商户订单号
		else if (!order.getOrdernumber().equals(aLiPayNotifyPojo.getOut_trade_no()) || order.getOrdernumber() != aLiPayNotifyPojo.getOut_trade_no())
		{
			resultMap.put("return_code", "FAIL");
			resultMap.put("return_msg", "订单号不对");
		}
		else
		{
			resultMap.put("return_code", "SUCCESS");
			resultMap.put("return_msg", "OK");
		}

		return resultMap;
	}
	
	/**
	 * 微信请求参数
	 * 
	 * @author hj
	 * @param order
	 * @param weiXinPay
	 * @return
	 */
	public static Map<String, String> getWeiXinPayXml(Order order, WeiXinPay weiXinPay, int payType)
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		try
		{
			// 订单开始时间
			Date orderStartTimeDate = order.getStarttime();
			// 订单结束时间
			Date orderEndTimeDate = new Date(order.getStarttime().getTime() + (DataConstant.WEIXINPAY_PAY_TIMEOUTEXPRESS * 60 * 1000));

			Map<String, String> reqData = new HashMap<String, String>();

			reqData.put("nonce_str", weiXinPay.getNonceStr());
			reqData.put("body", order.getCommoditydescribe());
			reqData.put("device_info", DataConstant.WEIXINPAY_DECICE_INFO);
			reqData.put("attach", order.getUserid().toString());
			reqData.put("out_trade_no", order.getOrdernumber());
			reqData.put("notify_url", DataConstant.WEIXINPAY_NOTIFYURL);
			reqData.put("fee_type", "CNY");
			reqData.put("total_fee", String.valueOf(Math.round(order.getOrderamount() * 100)));
			reqData.put("spbill_create_ip", order.getIp());
			reqData.put("time_start", ConvertUtil.dateToStr(orderStartTimeDate, "yyyyMMddHHmmss"));
			reqData.put("time_expire", ConvertUtil.dateToStr(orderEndTimeDate, "yyyyMMddHHmmss"));
			reqData.put("goods_tag", order.getCommodityid().toString());
			reqData.put("trade_type", order.getPaymenttype());
			
			switch (payType)
			{
			case 1:// APP支付
				reqData.put("appid", DataConstant.WEIXINPAY_YH_APPID);// DataConstant.WEIXINPAY_APPID
				reqData.put("mch_id", DataConstant.WEIXINPAY_YH_MCH_ID_APP);
				reqData.put("detail", "");
				reqData.put("limit_pay", "");
				reqData.put("sign", getWeiXinPaySign(reqData, DataConstant.WEIXINPAY_PAY_YH_KEY));
				break;
			case 2:// 扫码支付
				reqData.put("appid", DataConstant.WEIXINPAY_XLD_OFFICIALACCOUNTS);// DataConstant.WEIXINPAY_APPID
				reqData.put("mch_id", DataConstant.WEIXINPAY_XLD_MCH_ID_JSAPI);
				reqData.put("detail", "");
				reqData.put("limit_pay", "");
				reqData.put("product_id", "");
				reqData.put("openid", "");
				reqData.put("sign", getWeiXinPaySign(reqData, DataConstant.WEIXINPAY_PAY_XLD_KEY));
				break;
			case 3:// WAP支付(暂无)
				reqData.put("appid", DataConstant.WEIXINPAY_YH_APPID);// DataConstant.WEIXINPAY_APPID
				reqData.put("mch_id", DataConstant.WEIXINPAY_YH_MCH_ID_APP);
				reqData.put("detail", "");
				reqData.put("limit_pay", "");
				reqData.put("product_id", "");
				reqData.put("openid", "");
				reqData.put("sign", getWeiXinPaySign(reqData, DataConstant.WEIXINPAY_PAY_YH_KEY));
				break;
			case 4:// 公众号支付
				reqData.put("appid", DataConstant.WEIXINPAY_XLD_OFFICIALACCOUNTS);// DataConstant.WEIXINPAY_APPID
				reqData.put("mch_id", DataConstant.WEIXINPAY_XLD_MCH_ID_JSAPI);
				reqData.put("detail", "");
				reqData.put("limit_pay", "");
				reqData.put("product_id", "");
				reqData.put("openid", weiXinPay.getOpenid());
				reqData.put("sign", getWeiXinPaySign(reqData, DataConstant.WEIXINPAY_PAY_XLD_KEY));
				break;
			}

			String resultDataStr;
			String toXml = toXml(reqData);
			// System.out.println("微信支付传递的参数：" + toXml);
			resultDataStr = HttpUtil.postXml(DataConstant.WEIXINPAY_REQUEST_ADDRESS, toXml);
			System.out.println("统一下单结果：" + resultDataStr);
			// XML格式参数转化json
			JSONObject resultData = JSONObject.parseObject(XML.toJSONObject(resultDataStr).getJSONObject("xml").toString());

			if (resultData != null && "SUCCESS".equals(resultData.getString("return_code")) && "SUCCESS".equals(resultData.getString("result_code")))
			{
				switch (payType)
				{
				case 1:
					resultMap.put("partnerid", DataConstant.WEIXINPAY_YH_MCH_ID_APP);
					resultMap.put("prepayid", resultData.getString("prepay_id"));
					resultMap.put("package", "Sign=WXPay");
					resultMap.put("noncestr", weiXinPay.getNonceStr());
					resultMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
					// 微信支付第二次签名
					resultMap.put("sign", getWeiXinPaySign(resultMap, DataConstant.WEIXINPAY_PAY_YH_KEY));
					break;
				case 2:
					resultMap.put("codeurl", resultData.getString("code_url"));
					resultMap.put("resultcode", resultData.getString("result_code"));
					break;
				case 3:
					// wap支付暂无
					break;
				case 4:
					// 签名参数
					Map<String, String> signMap = new HashMap<String, String>();
					/*signMap.put("return_code", resultData.getString("return_code"));
					signMap.put("return_msg", resultData.getString("return_msg"));
					signMap.put("appid", resultData.getString("appid"));
					signMap.put("mch_id", resultData.getString("mch_id"));
					signMap.put("device_info", resultData.getString("device_info"));
					signMap.put("nonce_str", resultData.getString("nonce_str"));
					signMap.put("sign", resultData.getString("sign"));
					signMap.put("result_code", resultData.getString("result_code"));
					signMap.put("prepay_id", resultData.getString("prepay_id"));
					signMap.put("trade_type", resultData.getString("trade_type"));
					signMap.put("tradeType", resultData.getString("trade_type"));*/
					// 二次签名

					// 返回参数
					signMap = new HashMap<String, String>();
					signMap.put("appId", resultData.getString("appid"));
					signMap.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
					signMap.put("nonceStr", resultData.getString("nonce_str"));
					signMap.put("package", "prepay_id=" + resultData.getString("prepay_id"));
					signMap.put("signType", "MD5");
					System.out.println("签名前参数：" + signMap.toString());

					resultMap.put("appid", resultData.getString("appid"));
					resultMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
					// resultMap.put("noncestr",
					// resultData.getString("nonce_str"));
					resultMap.put("noncestr", resultData.getString("nonce_str"));
					resultMap.put("packages", "prepay_id=" + resultData.getString("prepay_id"));
					resultMap.put("signtype", "MD5");
					resultMap.put("paySign", getSignUtil(signMap, DataConstant.WEIXINPAY_PAY_XLD_KEY));
					System.out.println("签名后的参数：" + resultMap.get("paySign"));
					break;
				}
			}
			else
			{
				resultMap.put("returncode", resultData.getString("return_code"));
				resultMap.put("returnmsg", resultData.getString("return_msg"));
				resultMap.put("resultcode", resultData.getString("result_code"));
				resultMap.put("errcode", resultData.getString("err_code"));
			}

			return resultMap;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return resultMap;
		}
	}
	
	/**
	 * 组装请求数据字符串
	 * 
	 * @author hj
	 * @param "xml" 根元素名称
	 * @param postData
	 *            请求数据
	 * @return
	 */
	public static String toXml(Map<String, String> postData)
	{
		Element root = DocumentHelper.createElement("xml");
		Document document = DocumentHelper.createDocument(root);

		List<String> keys = new ArrayList<String>(postData.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++)
		{
			String key = keys.get(i);
			String value = postData.get(key);
			Element element = root.addElement(key);
			element.addText(StringUtils.isEmpty(value) ? "" : value);
		}

		/*Iterator<String> it = postData.keySet().iterator();
		while (it.hasNext())
		{
			String key = it.next();
			String value = postData.get(key);
			Element element = root.addElement(key);
			element.addCDATA(StringUtils.isEmpty(value) ? "" : value);
		}*/
		return document.asXML();
	}

	/**
	 * 微信签名生成规则
	 * 
	 * @author hj
	 * @param order
	 * @param weiXinPay
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getWeiXinPaySign(Map<String, String> signMap, String key) throws UnsupportedEncodingException
	{
		// 未签名的原始字符串(数据&拼接(去除空值))
		String notSign = AlipayCore.createLinkString(AlipayCore.paraFilter(signMap));
		// 拼接key后的字符串
		String signTemp = notSign + "&key=" + key;
		// MD5加密后的signTemp
		String sign = MD5.weiXinMD5(signTemp.toString()).toUpperCase();

		return sign;
	}

	/**
	 * 微信订单错误结果判断
	 * 
	 * @author hj
	 * @param resultData
	 * @return
	 */
	public static Map<String, Object> resoultSwitch(String errCode)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		switch (errCode)
		{
		case "NOAUTH":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_SIGN_FAIL);
			break;
		case "NOTENOUGH":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_NOTENOUGH);
			break;
		case "ORDERPAID":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_ORDERPAID);
			break;
		case "ORDERCLOSED":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_ORDERCLOSED);
			break;
		case "SYSTEMERROR":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_SYSTEMERROR);
			break;
		case "APPID_NOT_EXIST":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_APPID_NOT_EXIST);
			break;
		case "MCHID_NOT_EXIST":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_MCHID_NOT_EXIST);
			break;
		case "APPID_MCHID_NOT_MATCH":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_APPID_MCHID_NOT_MATCH);
			break;
		case "LACK_PARAMS":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_LACK_PARAMS);
			break;
		case "OUT_TRADE_NO_USED":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_OUT_TRADE_NO_USED);
			break;
		case "SIGNERROR":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_SIGNERROR);
			break;
		case "XML_FORMAT_ERROR":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_XML_FORMAT_ERROR);
			break;
		case "REQUIRE_POST_METHOD":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_REQUIRE_POST_METHOD);
			break;
		case "POST_DATA_EMPTY":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_POST_DATA_EMPTY);
			break;
		case "NOT_UTF8":
			resultMap = ResultMap.getResultMap(DataConstant.ALIPAY_RESULTSTATUS_CODE_NOT_UTF8);
			break;
		default:
			resultMap = ResultMap.getResultMap(DataConstant.FAIL);
			break;
		}
		return resultMap;
	}
	
	/**
	 * 剔除空参并拼接参数
	 * @author hj
	 * @param map
	 * @param k
	 * @return
	 */
    public static String getSignUtil(Map<String, String> map, String k){
        Collection<String> keys = map.keySet();
        List<String> list = new ArrayList<String>(keys);
        Collections.sort(list);
        String sign = "";
        for (String key : list) {
            if(map.get(key).equals("")||map.get(key)==null){
                continue;
            }

            if (sign.equals("")) {
                sign += key+"="+map.get(key);
            }else {
                sign += "&"+key+"="+map.get(key);
            }
        }
        if (k!=null) {
			return MD5.weiXinMD5(sign + "&key=" + k).toUpperCase();
        }else {
			return MD5.weiXinMD5(sign).toUpperCase();
        }
	}
}
