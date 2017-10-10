package pers.aidenj.payment.model;

import java.util.Date;

public class ALiPay
{
	private String ordernumber;

	private String appid;

	private String method;

	private String format;

	private String charset;

	private String signtype;

	private String sign;

	private Date timestamp;

	private String version;

	private String notifyurl;

	private String appauthtoken;

	private String bizcontent;

	private String outtradeno;

	private String sellerid;

	private Double totalamount;

	private Double discountableamount;

	private Double undiscountabelamount;

	private String buyerlogonid;

	private Date timeoutexpress;

	public String getOrdernumber()
	{
		return ordernumber;
	}

	public void setOrdernumber(String ordernumber)
	{
		this.ordernumber = ordernumber;
	}

	public String getAppid()
	{
		return appid;
	}

	public void setAppid(String appid)
	{
		this.appid = appid;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method == null ? null : method.trim();
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format == null ? null : format.trim();
	}

	public String getCharset()
	{
		return charset;
	}

	public void setCharset(String charset)
	{
		this.charset = charset == null ? null : charset.trim();
	}

	public String getSigntype()
	{
		return signtype;
	}

	public void setSigntype(String signtype)
	{
		this.signtype = signtype == null ? null : signtype.trim();
	}

	public String getSign()
	{
		return sign;
	}

	public void setSign(String sign)
	{
		this.sign = sign == null ? null : sign.trim();
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version == null ? null : version.trim();
	}

	public String getNotifyurl()
	{
		return notifyurl;
	}

	public void setNotifyurl(String notifyurl)
	{
		this.notifyurl = notifyurl == null ? null : notifyurl.trim();
	}

	public String getAppauthtoken()
	{
		return appauthtoken;
	}

	public void setAppauthtoken(String appauthtoken)
	{
		this.appauthtoken = appauthtoken == null ? null : appauthtoken.trim();
	}

	public String getBizcontent()
	{
		return bizcontent;
	}

	public void setBizcontent(String bizcontent)
	{
		this.bizcontent = bizcontent == null ? null : bizcontent.trim();
	}

	public String getOuttradeno()
	{
		return outtradeno;
	}

	public void setOuttradeno(String outtradeno)
	{
		this.outtradeno = outtradeno == null ? null : outtradeno.trim();
	}

	public String getSellerid()
	{
		return sellerid;
	}

	public void setSellerid(String sellerid)
	{
		this.sellerid = sellerid == null ? null : sellerid.trim();
	}

	public Double getTotalamount()
	{
		return totalamount;
	}

	public void setTotalamount(Double totalamount)
	{
		this.totalamount = totalamount;
	}

	public Double getDiscountableamount()
	{
		return discountableamount;
	}

	public void setDiscountableamount(Double discountableamount)
	{
		this.discountableamount = discountableamount;
	}

	public Double getUndiscountabelamount()
	{
		return undiscountabelamount;
	}

	public void setUndiscountabelamount(Double undiscountabelamount)
	{
		this.undiscountabelamount = undiscountabelamount;
	}

	public String getBuyerlogonid()
	{
		return buyerlogonid;
	}

	public void setBuyerlogonid(String buyerlogonid)
	{
		this.buyerlogonid = buyerlogonid;
	}

	public Date getTimeoutexpress()
	{
		return timeoutexpress;
	}

	public void setTimeoutexpress(Date timeoutexpress)
	{
		this.timeoutexpress = timeoutexpress;
	}

}