package pers.aidenj.payment.common;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 数据常量类
 * 
 * @author zhy
 *
 */
public class DataConstant {
	public final static Properties fileData = FileUtil.initConfigFile("file.properties");	
	/******************************公共响应码************************************/
	/**操作成功*/
	public final static int SUCCESS = 100;
	
	/**操作失败*/
	public final static int FAIL = 101;
	
	/********************************信息提示*********************************/
	
	public final static Map<Object, String> codeMessage = new HashMap<Object, String>() {
		private static final long serialVersionUID = 9129859292809731011L;
		{
			put(SUCCESS, "操作成功");
			put(FAIL, "操作失败");

			// 支付宝提示信息（hj）
			put(ALIPAY_RESULTSTATUS_CODE_SUCCESS, "支付成功");
			put(ALIPAY_RESULTSTATUS_CODE_SIGN_FAIL, "签名失败");
			put(ALIPAY_RESULTSTATUS_CODE_TREATMENT, "订单处理中，请稍候");
			put(ALIPAY_RESULTSTATUS_CODE_FAIL, "支付失败");
			put(ALIPAY_RESULTSTATUS_CODE_REPEAT, "重复提交");
			put(ALIPAY_RESULTSTATUS_CODE_CANCEL, "订单取消");
			put(ALIPAY_RESULTSTATUS_CODE_NETWORK_ERROR, "网络连接出错");
			put(ALIPAY_RESULTSTATUS_CODE_RESULT_UNKNOWN, "请前往订单列表查看");
			put(ALIPAY_RESULTSTATUS_CODE_NOT_ORDER, "订单不存在");
			put(ALIPAY_RESULTSTATUS_CODE_NOAUTH, "商户无此接口权限");
			put(ALIPAY_RESULTSTATUS_CODE_NOTENOUGH, "余额不足");
			put(ALIPAY_RESULTSTATUS_CODE_ORDERPAID, "商户订单已支付");
			put(ALIPAY_RESULTSTATUS_CODE_ORDERCLOSED, "订单已关闭");
			put(ALIPAY_RESULTSTATUS_CODE_SYSTEMERROR, "系统错误");
			put(ALIPAY_RESULTSTATUS_CODE_APPID_NOT_EXIST, "APPID不存在");
			put(ALIPAY_RESULTSTATUS_CODE_MCHID_NOT_EXIST, "MCHID不存在");
			put(ALIPAY_RESULTSTATUS_CODE_APPID_MCHID_NOT_MATCH, "appid和mch_id不匹配");
			put(ALIPAY_RESULTSTATUS_CODE_LACK_PARAMS, "缺少参数");
			put(ALIPAY_RESULTSTATUS_CODE_OUT_TRADE_NO_USED, "商户订单号重复");
			put(ALIPAY_RESULTSTATUS_CODE_SIGNERROR, "签名错误");
			put(ALIPAY_RESULTSTATUS_CODE_XML_FORMAT_ERROR, "XML格式错误");
			put(ALIPAY_RESULTSTATUS_CODE_REQUIRE_POST_METHOD, "请使用post方法");
			put(ALIPAY_RESULTSTATUS_CODE_POST_DATA_EMPTY, "post数据为空");
			put(ALIPAY_RESULTSTATUS_CODE_NOT_UTF8, "编码格式错误");
			
			/***************订单状态******************/
			put(ORDERSTATUS_WAIT, "订单待支付");
			put(ORDERSTATUS_COMPLETE, "订单支付完成");
			put(ORDERSTATUS_FAIL, "订单支付失败");
			put(ORDERSTATUS_CANCEL, "订单被取消");
			put(ORDERSTATUS_INVALID, "订单失效");

			put(ILLEGAL_ORDER, "订单非法");
			put(CREATE_FAILED, "下单失败");
			
		}
	};

	
	/**************************************************** 支付宝相关参数（hj） *********************************************************/
	/** 应用ID */
	public final static String ALIPAY_APPID = fileData.getProperty("ALIPAY_APPID");;
	/** 商户UID */
	public final static String ALIPAY_SELLER_ID = fileData.getProperty("ALIPAY_SELLER_ID");
	/** 格式类型 */
	public final static String ALIPAY_FORMAT = "json";
	/** 编码格式 */
	public final static String ALIPAY_CHARSET = "utf-8";
	/** 签名方式 */
	public final static String ALIPAY_SIGN_TYPE = "RSA";
	/** 调用的接口版本 */
	public final static String ALIPAY_VERSION = "1.0";
	/** APP销售产品码 */
	public final static String ALIPAY_PRODUCT_CODE_APP = "QUICK_MSECURITY_PAY";
	/** WAP销售产品码 */
	public final static String ALIPAY_PRODUCT_CODE_WAP = "QUICK_WAP_PAY";

	/** 支付失效时间（单位：m/分钟） */
	public final static long ALIPAY_PAY_TIMEOUTEXPRESS = Long.parseLong(fileData.getProperty("ALIPAY_PAY_TIMEOUTEXPRESS"));

	/** 支付宝请求地址 （用不上） */
	public final static String ALIPAY_REQUEST_ADDRESS = "https://openapi.alipay.com/gateway.do";// 线上
	
	/** 支付宝提供给商户的服务接入网关URL(新)*/
	public final static String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
	
	/** 异步回调地址 */
	public final static String ALIPAY_NOTIFYURL = "/rechanger/notifya";
	/** 同步回调地址 */

	/**************************************************** 订单错误状态码 *********************************************************/
	/** 订单支付成功 */
	public final static int ALIPAY_RESULTSTATUS_CODE_SUCCESS = 9000;
	/** 签名验证失败 */
	public final static int ALIPAY_RESULTSTATUS_CODE_SIGN_FAIL = 90001;
	/** 订单信息验证失败 */
	public final static int ALIPAY_RESULTSTATUS_CODE_ORDER_INFORMATION = 90002;
	/** 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态 */
	public final static int ALIPAY_RESULTSTATUS_CODE_TREATMENT = 8000;
	/** 订单支付失败 */
	public final static int ALIPAY_RESULTSTATUS_CODE_FAIL = 4000;
	/** 重复请求 */
	public final static int ALIPAY_RESULTSTATUS_CODE_REPEAT = 5000;
	/** 用户中途取消 */
	public final static int ALIPAY_RESULTSTATUS_CODE_CANCEL = 6001;
	/** 网络连接出错 */
	public final static int ALIPAY_RESULTSTATUS_CODE_NETWORK_ERROR = 6002;
	/** 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态 */
	public final static int ALIPAY_RESULTSTATUS_CODE_RESULT_UNKNOWN = 6004;
	/** 订单不存在 */
	public final static int ALIPAY_RESULTSTATUS_CODE_NOT_ORDER = 0;

	/*************************** 微信错误码（从1000开始） *********************************/
	/** 商户无此接口权限 */
	public final static int ALIPAY_RESULTSTATUS_CODE_NOAUTH = 1000;
	/** 余额不足 */
	public final static int ALIPAY_RESULTSTATUS_CODE_NOTENOUGH = 1001;
	/** 商户订单已支付 */
	public final static int ALIPAY_RESULTSTATUS_CODE_ORDERPAID = 1002;
	/** 订单已关闭 */
	public final static int ALIPAY_RESULTSTATUS_CODE_ORDERCLOSED = 1003;
	/** 系统错误 */
	public final static int ALIPAY_RESULTSTATUS_CODE_SYSTEMERROR = 1004;
	/** APPID不存在 */
	public final static int ALIPAY_RESULTSTATUS_CODE_APPID_NOT_EXIST = 1005;
	/** MCHID不存在 */
	public final static int ALIPAY_RESULTSTATUS_CODE_MCHID_NOT_EXIST = 1006;
	/** appid和mch_id不匹配 */
	public final static int ALIPAY_RESULTSTATUS_CODE_APPID_MCHID_NOT_MATCH = 1007;
	/** 缺少参数 */
	public final static int ALIPAY_RESULTSTATUS_CODE_LACK_PARAMS = 1008;
	/** 商户订单号重复 */
	public final static int ALIPAY_RESULTSTATUS_CODE_OUT_TRADE_NO_USED = 1009;
	/** 签名错误 */
	public final static int ALIPAY_RESULTSTATUS_CODE_SIGNERROR = 1010;
	/** XML格式错误 */
	public final static int ALIPAY_RESULTSTATUS_CODE_XML_FORMAT_ERROR = 1011;
	/** 请使用post方法 */
	public final static int ALIPAY_RESULTSTATUS_CODE_REQUIRE_POST_METHOD = 1012;
	/** post数据为空 */
	public final static int ALIPAY_RESULTSTATUS_CODE_POST_DATA_EMPTY = 1013;
	/** 编码格式错误 */
	public final static int ALIPAY_RESULTSTATUS_CODE_NOT_UTF8 = 1014;

	/*********************************************************************** 微信相关参数（hj） ***********************************************/
	/** 微信下单地址 */
	public final static String WEIXINPAY_REQUEST_ADDRESS = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/** 应用ID */
	public final static String WEIXINPAY_YH_APPID = fileData.getProperty("ALIPAY_PAY_TIMEOUTEXPRESS");// 炎黄
	public final static String WEIXINPAY_XLD_APPID = "";// 新兰德
	/** 公众号ID */
	public final static String WEIXINPAY_YH_OFFICIALACCOUNTS = fileData.getProperty("ALIPAY_PAY_TIMEOUTEXPRESS");// 炎黄
	public final static String WEIXINPAY_XLD_OFFICIALACCOUNTS = fileData.getProperty("WEIXINPAY_XLD_OFFICIALACCOUNTS");// 新兰德
	/** APP商户号 */
	public final static String WEIXINPAY_YH_MCH_ID_APP = fileData.getProperty("WEIXINPAY_YH_MCH_ID_APP");// 炎黄
	public final static String WEIXINPAY_XLD_MCH_ID_APP = fileData.getProperty("WEIXINPAY_XLD_MCH_ID_APP");// 新兰德
	/** 公众号商户号 */
	public final static String WEIXINPAY_YH_MCH_ID_JSAPI = fileData.getProperty("WEIXINPAY_YH_MCH_ID_JSAPI");// 炎黄
	public final static String WEIXINPAY_XLD_MCH_ID_JSAPI = fileData.getProperty("WEIXINPAY_XLD_MCH_ID_JSAPI");// 新兰德
	/** 设备号 */
	public final static String WEIXINPAY_DECICE_INFO = "001";// (自定义：终端设备号(门店号或收银设备ID))
	/** 回调地址(异步回调) */
	public final static String WEIXINPAY_NOTIFYURL = "/rechanger/notifyw";
	/** 支付失效时间（单位：m/分钟） */
	public final static long WEIXINPAY_PAY_TIMEOUTEXPRESS = 120;
	/** 微信支付密钥key */
	public final static String WEIXINPAY_PAY_YH_KEY = fileData.getProperty("WEIXINPAY_PAY_YH_KEY");// 炎黄
	public final static String WEIXINPAY_PAY_XLD_KEY = fileData.getProperty("WEIXINPAY_PAY_XLD_KEY");// 新兰德
	/** 微信AppSecret */
	public final static String WEIXINPAY_YH_PAY_APPSECRET = fileData.getProperty("WEIXINPAY_YH_PAY_APPSECRET");// 炎黄
	public final static String WEIXINPAY_XLD_PAY_APPSECRET = fileData.getProperty("WEIXINPAY_XLD_PAY_APPSECRET");// 新兰德

	/************************* 订单状态 ***************************/
	/** 订单状态——待支付 */
	public final static String ORDERSTATUS_WAIT = "wait";
	/** 订单状态——支付完成 */
	public final static String ORDERSTATUS_COMPLETE = "complete";
	/** 订单状态——支付失败 */
	public final static String ORDERSTATUS_FAIL = "fail";
	/** 订单状态——取消支付 */
	public final static String ORDERSTATUS_CANCEL = "cancel";
	/** 订单状态——订单失效 */
	public final static String ORDERSTATUS_INVALID = "invalid";
	
	/*****************************阿里订单状态*********************/
	/** 交易创建，等待买家付款 */
	public final static String WAIT_BUYER_PAY ="wait";
	/** 未付款交易超时关闭，或支付完成后全额退款 */
	public final static String TRADE_CLOSED ="invalid";
	/** 交易支付成功 */
	public final static String TRADE_SUCCESS ="complete";
	/** 交易结束，不可退款 */
	public final static String TRADE_FINISHED ="invalid";
	
	/** 订单非法 */
	public final static String ILLEGAL_ORDER = "ILLEGAL_ORDER";

	/** 订单创建失败 */
	public final static String CREATE_FAILED = "CREATE_FAILED";
	
	

}
