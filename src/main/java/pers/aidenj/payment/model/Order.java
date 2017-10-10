package pers.aidenj.payment.model;

import java.util.Date;

public class Order
{
	private Integer id;

	private String identification;

	private String commodityid;

	private Double commodityprice;

	private Integer commoditynumber;

	private String commodityname;

	private String commoditydescribe;

	private String commoditytype;

	private String ordernumber;

	private Date starttime;

	private Date endtime;

	private Long userid;

	private String ip;

	private Double orderamount;

	private String paymentmethod;

	private String paymenttype;

	private String servicetype;

	private String orderstatus;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getIdentification()
	{
		return identification;
	}

	public void setIdentification(String identification)
	{
		this.identification = identification == null ? null : identification.trim();
	}

	public String getCommodityid()
	{
		return commodityid;
	}

	public void setCommodityid(String commodityid)
	{
		this.commodityid = commodityid;
	}

	public Double getCommodityprice()
	{
		return commodityprice;
	}

	public void setCommodityprice(Double commodityprice)
	{
		this.commodityprice = commodityprice;
	}

	public Integer getCommoditynumber()
	{
		return commoditynumber;
	}

	public void setCommoditynumber(Integer commoditynumber)
	{
		this.commoditynumber = commoditynumber;
	}

	public String getCommodityname()
	{
		return commodityname;
	}

	public void setCommodityname(String commodityname)
	{
		this.commodityname = commodityname == null ? null : commodityname.trim();
	}

	public String getCommoditydescribe()
	{
		return commoditydescribe;
	}

	public void setCommoditydescribe(String commoditydescribe)
	{
		this.commoditydescribe = commoditydescribe == null ? null : commoditydescribe.trim();
	}

	public String getCommoditytype()
	{
		return commoditytype;
	}

	public void setCommoditytype(String commoditytype)
	{
		this.commoditytype = commoditytype == null ? null : commoditytype.trim();
	}

	public String getOrdernumber()
	{
		return ordernumber;
	}

	public void setOrdernumber(String ordernumber)
	{
		this.ordernumber = ordernumber == null ? null : ordernumber.trim();
	}

	public Date getStarttime()
	{
		return starttime;
	}

	public void setStarttime(Date starttime)
	{
		this.starttime = starttime;
	}

	public Date getEndtime()
	{
		return endtime;
	}

	public void setEndtime(Date endtime)
	{
		this.endtime = endtime;
	}

	public Long getUserid()
	{
		return userid;
	}

	public void setUserid(Long userid)
	{
		this.userid = userid;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip == null ? null : ip.trim();
	}

	public Double getOrderamount()
	{
		return orderamount;
	}

	public void setOrderamount(Double orderamount)
	{
		this.orderamount = orderamount;
	}

	public String getPaymentmethod()
	{
		return paymentmethod;
	}

	public void setPaymentmethod(String paymentmethod)
	{
		this.paymentmethod = paymentmethod == null ? null : paymentmethod.trim();
	}

	public String getPaymenttype()
	{
		return paymenttype;
	}

	public void setPaymenttype(String paymenttype)
	{
		this.paymenttype = paymenttype == null ? null : paymenttype.trim();
	}

	public String getServicetype()
	{
		return servicetype;
	}

	public void setServicetype(String servicetype)
	{
		this.servicetype = servicetype == null ? null : servicetype.trim();
	}

	public String getOrderstatus()
	{
		return orderstatus;
	}

	public void setOrderstatus(String orderstatus)
	{
		this.orderstatus = orderstatus == null ? null : orderstatus.trim();
	}
}