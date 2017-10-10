package pers.aidenj.payment.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import pers.aidenj.payment.common.DataConstant;
import pers.aidenj.payment.common.FileUtil;
import pers.aidenj.payment.common.XmlTOJSONobject;
import pers.aidenj.payment.model.ALiPayNotifyPojo;
import pers.aidenj.payment.model.Order;
import pers.aidenj.payment.service.PaymentService;

@Controller
public class PaymentController
{
	protected final static Log log = LogFactory.getLog(PaymentController.class);
	
	// 回调地址
	public final String PATH_URL_HTTP = FileUtil.initConfigFile("file.properties").getProperty("URL");

	@Resource
	PaymentService paymentService;
	/**
	 * 创建订单
	 * 
	 * @author hj
	 * @param order
	 * @return
	 */
	@RequestMapping("create")
	@ResponseBody
	public Map<String, Object> createTransaction(Order order)
	{
		log.info("\r开始创建订单。。。。。。。。。。。。。。");
		return paymentService.addOrder(order);
	}

	/**
	 * 订单查询
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	@RequestMapping("getOrderState")
	@ResponseBody
	public Map<String,Object> getOrderState(String orderNumber)
	{
		log.info("\r开始查询订单。。。。。。。。。。。。。。");
		return paymentService.getOrderState(orderNumber);
	}
	
	/**
	 * APP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@RequestMapping("appOrderInformationa")
	@ResponseBody
	public Map<String, Object> paymentAPPa(String orderNumber)
	{
		return paymentService.paymentAPPa(orderNumber);
	}
	
	/**
	 * WAP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	@RequestMapping("wapOrderInformationa")
	public void paymentWAPa(HttpServletResponse httpResponse, String orderNumber)
	{
		String fromData = paymentService.paymentWAPa(orderNumber);
		try
		{
			httpResponse.setContentType("text/html;charset=" + DataConstant.ALIPAY_CHARSET);
			httpResponse.getWriter().write(fromData);
			httpResponse.getWriter().flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * WAP支付——老版本
	 * 
	 * @author hj
	 * @param orderNumber
	 * @return
	 */
	@RequestMapping("wapOrderInformationaOld")
	public void paymentWAPaOld(HttpServletResponse httpResponse, String orderNumber)
	{
		String fromData = paymentService.paymentWAPaOld(orderNumber);
		System.out.println(fromData);
		try
		{
			httpResponse.setContentType("text/html;charset=" + DataConstant.ALIPAY_CHARSET);
			httpResponse.getWriter().write(fromData);
			httpResponse.getWriter().flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 支付宝即使到账（二维码支付）
	 * 
	 * @author hj
	 * @return
	 */
	@RequestMapping("arrivalOrderInformationaOld")
	@ResponseBody
	public void paymentARRIVALa(HttpServletResponse httpResponse, String orderNumber)
	{
		try
		{
			String fromData = paymentService.paymentARRIVALa(orderNumber);
			httpResponse.setContentType("text/html;charset=" + DataConstant.ALIPAY_CHARSET);
			httpResponse.getWriter().write(fromData);
			httpResponse.getWriter().flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 同步回调地址
	 * 
	 * @author hj
	 * @param resultDate
	 * @return
	 */
	@RequestMapping("appVerificationa")
	@ResponseBody
	public Map<String, Object> verificationAPPa(String resultDate)
	{
		return paymentService.verificationAPPa(resultDate);
	}

	/**
	 * 异步回调地址
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@RequestMapping("notifya")
	@ResponseBody
	public String payNotifya(ALiPayNotifyPojo aLiPayNotifyPojo)
	{
		System.out.println("接收到异步结果");
		return paymentService.payNotifya(aLiPayNotifyPojo);
	}
	
	/************************************************** 微信 *************************************************/
	/**
	 * 微信APP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@RequestMapping("appOrderInformationw")
	@ResponseBody
	public Map<String, Object> paymentAPPw(String orderNumber)
	{
		return paymentService.paymentAPPw(orderNumber);
	}
	
	/**
	 * 微信WAP支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@RequestMapping("wapOrderInformationw")
	@ResponseBody
	public String paymentWAPw(String orderNumber)
	{
		return null;//paymentService.paymentWAPw(orderNumber);
	}

	/**
	 * 微信扫码支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@RequestMapping("nativeOrderInformationw")
	@ResponseBody
	public Map<String, Object> paymentNATIVEw(String orderNumber)
	{
		return paymentService.paymentNATIVEw(orderNumber);
	}
	
	/**
	 * 微信公众号支付
	 * 
	 * @author hj
	 * @param orderNumber
	 * @param userTokenId
	 * @return
	 */
	@RequestMapping("jsapiOrderInformationw")
	@ResponseBody
	public Map<String, Object> paymentJSAPIw(String orderNumber, String codeId)
	{
		System.out.println("===================进来了==============");
		return paymentService.paymentJSAPIw(orderNumber, codeId);
	}
	
	/**
	 * 异步回调地址
	 * 
	 * @author hj
	 * @param aLiPayNotifyPojo
	 * @return
	 */
	@RequestMapping(value = "notifyw", headers = { "content-type=application/xml" })/*只能接收XML格式的参数*/
	@ResponseBody
	// public Map<String, Object> payNotifyw(HttpServletRequest request)
	public String payNotifyw(@RequestBody String str)
	{
		System.out.println(".................微信异步回调开始");

		/*第一种获取方式（感觉复杂）
		 */
		/*
		String inputLine = null;
		// 接收到的数据
		StringBuffer recieveData = new StringBuffer();
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
			while ((inputLine = in.readLine()) != null)
			{
				recieveData.append(inputLine);
			}
		}
		catch (IOException e)
		{
		}
		finally
		{
			try
			{
				if (null != in)
				{
					in.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("获取到的参数：\r" + recieveData.toString());
		*/
		
		/*
		 * 第二种获取方式
		 */
		
		System.out.println("appid："+str);
		ALiPayNotifyPojo aLiPayNotifyPojo = null;
		try {
			aLiPayNotifyPojo = JSON.toJavaObject(XmlTOJSONobject.documentToJSONObject(str),ALiPayNotifyPojo.class);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		

		System.out.println("微信异步回调结束.................");

		return paymentService.payNotifyw(aLiPayNotifyPojo);
	}
	
	/**
	 * 公众号支付第一步：授权并获取Code
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	@RequestMapping("getCode")
	public void getCode(HttpServletResponse response)
	{
		// 只获取用户openid（无需授权）
		String snsapi_base = "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";

		// 获取用户昵称、性别、所在地（需要授权）
		String snsapi_userinfo = "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		try
		{

			String redirect_uri = URLEncoder.encode(PATH_URL_HTTP + "/rechanger/getOpenid?codeID=" + UUID.randomUUID().toString().replace("-", ""), "utf-8");
			String url = "http://open.weixin.qq.com/connect/oauth2/authorize?appid=" + DataConstant.WEIXINPAY_YH_OFFICIALACCOUNTS + "&redirect_uri=" + redirect_uri + snsapi_base;
			System.out.println("获取code参数：" + url);
			response.sendRedirect(url);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户openId
	 * 
	 * @param codeID
	 * @param code
	 */
	@RequestMapping("getOpenid")
	public void getOpenid(String codeID, String code, HttpServletResponse response)
	{
		try
		{
			String url;
			System.out.println("openId参数codeID：" + codeID);
			System.out.println("openId参数code：" + code);
			if (code != null)
			{
				if (paymentService.payOpenId(codeID, code) > 0)
				{
					url = PATH_URL_HTTP + "/weixinpay/pay.html?codeID=" + codeID;
				}
				else
				{
					url = PATH_URL_HTTP + "/weixinpay/pay.html?codeID=";
				}
			}
			else
			{
				url = PATH_URL_HTTP + "/weixinpay/pay.html?codeID=" + codeID;
			}

			response.sendRedirect(url);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
