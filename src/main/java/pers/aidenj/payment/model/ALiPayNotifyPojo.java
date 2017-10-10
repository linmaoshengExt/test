package pers.aidenj.payment.model;

public class ALiPayNotifyPojo
{
	/** 公用 */
	String sign;// 签名
	String out_trade_no;// 商户订单号
	/** 支付宝APP */
	String notify_time;// 通知时间
	String notify_type;// 通知类型
	String notify_id;// 通知效验ID
	String app_id;// 应用ID
	String charset;// 编码格式
	String version;// 接口版本
	String sign_type;// 签名算法
	String trade_no;// 支付宝交易号

	String buyer_id;// 买家支付宝用户号
	String seller_email;// 卖家支付宝账号
	String trade_status;// 交易状态
	String total_amount;// 订单金额
	String refund_fee;// 总退款金额
	String subject;// 订单标题
	String body;// 商品描述
	String gmt_create;// 交易创建时间
	String gmt_payment;// 交易付款时间
	String gmt_refund;// 交易退款时间
	String gmt_close;// 交易结束时间

	// 预计在2016年11月中旬即将开放
	String out_biz_no;// 商户业务ID
	String buyer_logon_id;// 买家支付宝账号
	String seller_id;// 卖家支付宝用户号
	String receipt_amount;// 实收金额
	String invoice_amount;// 开票金额
	String buyer_pay_amount;// 付款金额
	String point_amount;// 集分宝金额
	String fund_bill_list;// 支付金额信息

	/** 微信 */
	String return_code;// 返回状态码
	String return_msg;// 返回信息
	String appid;// 应用ID
	String mch_id;// 商户号
	String device_info;// 设备号
	String nonce_str;// 随机字符串
	String result_code;// 业务结果
	String err_code;// 错误代码
	String err_code_des;// 错误代码描述
	String openid;// 用户标识
	String is_subscribe;// 是否关注公众账号
	String trade_type;// 交易类型
	String bank_type;// 付款银行
	String total_fee;// 总金额
	String fee_type;// 货币种类
	String cash_fee;// 现金支付金额
	String cash_fee_type;// 现金支付货币类型
	String coupon_fee;// 代金券或立减优惠金额
	String coupon_count;// 代金券或立减优惠使用数量
	String coupon_id_$n;// 代金券或立减优惠ID
	String coupon_fee_$n;// 单个代金券或立减优惠支付金额
	String transaction_id;// 微信支付订单号
	String attach;// 商家数据包
	String time_end;// 支付完成时间
	public String getSign()
	{
		return sign;
	}
	public void setSign(String sign)
	{
		this.sign = sign;
	}
	public String getOut_trade_no()
	{
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no)
	{
		this.out_trade_no = out_trade_no;
	}
	public String getNotify_time()
	{
		return notify_time;
	}
	public void setNotify_time(String notify_time)
	{
		this.notify_time = notify_time;
	}
	public String getNotify_type()
	{
		return notify_type;
	}
	public void setNotify_type(String notify_type)
	{
		this.notify_type = notify_type;
	}
	public String getNotify_id()
	{
		return notify_id;
	}
	public void setNotify_id(String notify_id)
	{
		this.notify_id = notify_id;
	}
	public String getApp_id()
	{
		return app_id;
	}
	public void setApp_id(String app_id)
	{
		this.app_id = app_id;
	}
	public String getSign_type()
	{
		return sign_type;
	}
	public void setSign_type(String sign_type)
	{
		this.sign_type = sign_type;
	}
	public String getTrade_no()
	{
		return trade_no;
	}
	public void setTrade_no(String trade_no)
	{
		this.trade_no = trade_no;
	}
	public String getBuyer_id()
	{
		return buyer_id;
	}
	public void setBuyer_id(String buyer_id)
	{
		this.buyer_id = buyer_id;
	}
	public String getSeller_email()
	{
		return seller_email;
	}
	public void setSeller_email(String seller_email)
	{
		this.seller_email = seller_email;
	}
	public String getTrade_status()
	{
		return trade_status;
	}
	public void setTrade_status(String trade_status)
	{
		this.trade_status = trade_status;
	}
	public String getTotal_amount()
	{
		return total_amount;
	}
	public void setTotal_amount(String total_amount)
	{
		this.total_amount = total_amount;
	}
	public String getRefund_fee()
	{
		return refund_fee;
	}
	public void setRefund_fee(String refund_fee)
	{
		this.refund_fee = refund_fee;
	}
	public String getSubject()
	{
		return subject;
	}
	public void setSubject(String subject)
	{
		this.subject = subject;
	}
	public String getBody()
	{
		return body;
	}
	public void setBody(String body)
	{
		this.body = body;
	}
	public String getGmt_create()
	{
		return gmt_create;
	}
	public void setGmt_create(String gmt_create)
	{
		this.gmt_create = gmt_create;
	}
	public String getGmt_payment()
	{
		return gmt_payment;
	}
	public void setGmt_payment(String gmt_payment)
	{
		this.gmt_payment = gmt_payment;
	}
	public String getGmt_refund()
	{
		return gmt_refund;
	}
	public void setGmt_refund(String gmt_refund)
	{
		this.gmt_refund = gmt_refund;
	}
	public String getGmt_close()
	{
		return gmt_close;
	}
	public void setGmt_close(String gmt_close)
	{
		this.gmt_close = gmt_close;
	}
	public String getOut_biz_no()
	{
		return out_biz_no;
	}
	public void setOut_biz_no(String out_biz_no)
	{
		this.out_biz_no = out_biz_no;
	}
	public String getBuyer_logon_id()
	{
		return buyer_logon_id;
	}
	public void setBuyer_logon_id(String buyer_logon_id)
	{
		this.buyer_logon_id = buyer_logon_id;
	}
	public String getSeller_id()
	{
		return seller_id;
	}
	public void setSeller_id(String seller_id)
	{
		this.seller_id = seller_id;
	}
	public String getReceipt_amount()
	{
		return receipt_amount;
	}
	public void setReceipt_amount(String receipt_amount)
	{
		this.receipt_amount = receipt_amount;
	}
	public String getInvoice_amount()
	{
		return invoice_amount;
	}
	public void setInvoice_amount(String invoice_amount)
	{
		this.invoice_amount = invoice_amount;
	}
	public String getBuyer_pay_amount()
	{
		return buyer_pay_amount;
	}
	public void setBuyer_pay_amount(String buyer_pay_amount)
	{
		this.buyer_pay_amount = buyer_pay_amount;
	}
	public String getPoint_amount()
	{
		return point_amount;
	}
	public void setPoint_amount(String point_amount)
	{
		this.point_amount = point_amount;
	}
	public String getFund_bill_list()
	{
		return fund_bill_list;
	}
	public void setFund_bill_list(String fund_bill_list)
	{
		this.fund_bill_list = fund_bill_list;
	}
	public String getReturn_code()
	{
		return return_code;
	}
	public void setReturn_code(String return_code)
	{
		this.return_code = return_code;
	}
	public String getReturn_msg()
	{
		return return_msg;
	}
	public void setReturn_msg(String return_msg)
	{
		this.return_msg = return_msg;
	}
	public String getAppid()
	{
		return appid;
	}
	public void setAppid(String appid)
	{
		this.appid = appid;
	}

	public String getMch_id()
	{
		return mch_id;
	}

	public void setMch_id(String mch_id)
	{
		this.mch_id = mch_id;
	}
	public String getDevice_info()
	{
		return device_info;
	}
	public void setDevice_info(String device_info)
	{
		this.device_info = device_info;
	}
	public String getNonce_str()
	{
		return nonce_str;
	}
	public void setNonce_str(String nonce_str)
	{
		this.nonce_str = nonce_str;
	}
	public String getResult_code()
	{
		return result_code;
	}
	public void setResult_code(String result_code)
	{
		this.result_code = result_code;
	}
	public String getErr_code()
	{
		return err_code;
	}
	public void setErr_code(String err_code)
	{
		this.err_code = err_code;
	}
	public String getErr_code_des()
	{
		return err_code_des;
	}
	public void setErr_code_des(String err_code_des)
	{
		this.err_code_des = err_code_des;
	}
	public String getOpenid()
	{
		return openid;
	}
	public void setOpenid(String openid)
	{
		this.openid = openid;
	}
	public String getIs_subscribe()
	{
		return is_subscribe;
	}
	public void setIs_subscribe(String is_subscribe)
	{
		this.is_subscribe = is_subscribe;
	}
	public String getTrade_type()
	{
		return trade_type;
	}
	public void setTrade_type(String trade_type)
	{
		this.trade_type = trade_type;
	}
	public String getBank_type()
	{
		return bank_type;
	}
	public void setBank_type(String bank_type)
	{
		this.bank_type = bank_type;
	}
	public String getTotal_fee()
	{
		return total_fee;
	}
	public void setTotal_fee(String total_fee)
	{
		this.total_fee = total_fee;
	}
	public String getFee_type()
	{
		return fee_type;
	}
	public void setFee_type(String fee_type)
	{
		this.fee_type = fee_type;
	}
	public String getCash_fee()
	{
		return cash_fee;
	}
	public void setCash_fee(String cash_fee)
	{
		this.cash_fee = cash_fee;
	}
	public String getCash_fee_type()
	{
		return cash_fee_type;
	}
	public void setCash_fee_type(String cash_fee_type)
	{
		this.cash_fee_type = cash_fee_type;
	}
	public String getCoupon_fee()
	{
		return coupon_fee;
	}
	public void setCoupon_fee(String coupon_fee)
	{
		this.coupon_fee = coupon_fee;
	}
	public String getCoupon_count()
	{
		return coupon_count;
	}
	public void setCoupon_count(String coupon_count)
	{
		this.coupon_count = coupon_count;
	}
	public String getCoupon_id_$n()
	{
		return coupon_id_$n;
	}
	public void setCoupon_id_$n(String coupon_id_$n)
	{
		this.coupon_id_$n = coupon_id_$n;
	}
	public String getCoupon_fee_$n()
	{
		return coupon_fee_$n;
	}
	public void setCoupon_fee_$n(String coupon_fee_$n)
	{
		this.coupon_fee_$n = coupon_fee_$n;
	}
	public String getTransaction_id()
	{
		return transaction_id;
	}
	public void setTransaction_id(String transaction_id)
	{
		this.transaction_id = transaction_id;
	}
	public String getAttach()
	{
		return attach;
	}
	public void setAttach(String attach)
	{
		this.attach = attach;
	}
	public String getTime_end()
	{
		return time_end;
	}
	public void setTime_end(String time_end)
	{
		this.time_end = time_end;
	}

}
