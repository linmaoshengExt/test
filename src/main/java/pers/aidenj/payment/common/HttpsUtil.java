package pers.aidenj.payment.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class HttpsUtil
{
	protected final static Log log = LogFactory.getLog(HttpsUtil.class);

	/**
	 * 提交http请求，获取响应数据字符串（ 参考地址：https://my.oschina.net/langxSpirit/blog/488435）
	 * 
	 * @param url
	 *            请求URL
	 * @headerInfo 请求头信息
	 * @param xml
	 *            请求数据字符串
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com modify by
	 *         2015年8月2日
	 */
	public static String postXml(String url, String xml) throws Exception
	{
		String result = null;
		HttpEntity postEntity = new StringEntity(xml, "utf-8");
		HttpPost httpPost = new HttpPost(url);

		Map<String, String> headerInfo = new HashMap<String, String>();
		headerInfo.put("Content-Type", "text/xml");
		headerInfo.put("Connection", "keep-alive");
		headerInfo.put("Accept", "*/*");
		headerInfo.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		headerInfo.put("Host", "api.mch.weixin.qq.com");
		headerInfo.put("X-Requested-With", "XMLHttpRequest");
		headerInfo.put("Cache-Control", "max-age=0");
		headerInfo.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");

		if (headerInfo != null)
		{
			Iterator<String> it = headerInfo.keySet().iterator();
			while (it.hasNext())
			{
				String name = it.next();
				String value = headerInfo.get(name);
				httpPost.addHeader(name, value);
			}
		}
		httpPost.setEntity(postEntity);

		CloseableHttpClient httpclient = HttpClients.custom().build();
		try
		{
			HttpResponse response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK)
			{
				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					result = EntityUtils.toString(entity, "utf-8");
				}
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}
		finally
		{
			httpclient.close();
		}
		return result;
	}

	/**
	 * 发送xml数据,获取返回结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式
	 * @param xmlStr
	 *            请求参数
	 * @return
	 */
	public static Map<String, Object> httpXmlRequest(String requestUrl, String requestMethod, String xmlStr)
	{
		
		
		// 将解析结果存储在HashMap中
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			HttpsURLConnection urlCon = (HttpsURLConnection) (new URL(requestUrl)).openConnection();
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);
			// 设置请求方式（GET/POST）
			urlCon.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
			{
				urlCon.connect();
			}

			urlCon.setRequestProperty("Content-Length", String.valueOf(xmlStr.getBytes().length));
			urlCon.setUseCaches(false);
			// 设置为gbk可以解决服务器接收时读取的数据中文乱码问题
			if (null != xmlStr)
			{
				OutputStream outputStream = urlCon.getOutputStream();
				outputStream.write(xmlStr.getBytes("UTF-8"));
				outputStream.flush();
				outputStream.close();
			}
			InputStream inputStream = urlCon.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			// 读取输入流
			SAXReader reader = new SAXReader();
			Document document = reader.read(inputStreamReader);
			// 得到xml根元素
			Element root = document.getRootElement();
			// 得到根元素的所有子节点
			@SuppressWarnings("unchecked")
			List<Element> elementList = root.elements();
			// 遍历所有子节点
			for (Element e : elementList)
			{
				map.put(e.getName(), e.getText());
			}
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			urlCon.disconnect();
		}
		catch (MalformedURLException e)
		{
			log.error(e.getMessage());
		}
		catch (IOException e)
		{
			log.error(e.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return map;
	}

	/**
	 * https请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String doPost(String url, Map<String, String> map)
	{
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try
		{
			httpClient = new SSLClient();
			httpPost = new HttpPost(url);
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
			while (iterator.hasNext())
			{
				Entry<String, String> elem = iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0)
			{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
				httpPost.setEntity(entity);
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null)
			{
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null)
				{
					result = EntityUtils.toString(resEntity, "utf-8");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return result;
	}

	public static void main(String[] args)
	{
		Map<String, String> createMap = new HashMap<String, String>();

		createMap.put("appid", "wx21f8b0aad5b63eea");
		createMap.put("secret", "6b1d319d80114c10ed4debf49e5437ad");
		createMap.put("code", "041acQox1puB1e0S0Ypx1B4Kox1acQoM");
		createMap.put("grant_type", "authorization_code");

		String httpOrgCreateTestRtn = HttpsUtil.doPost("https://api.weixin.qq.com/sns/oauth2/access_token", createMap);
		System.out.println("result:" + httpOrgCreateTestRtn);

		/*传递过来的地址：https://api.weixin.qq.com/sns/oauth2/access_token
			传递过来的参数：{code=041acQox1puB1e0S0Ypx1B4Kox1acQoM,  grant_type =authorizati
			on_code, appid=wx21f8b0aad5b63eea, secret=6b1d319d80114c10ed4debf49e5437ad}
			通过code获取到的参数值：{"errcode":40002,"errmsg":"invalid grant_type, hints: [
			req_id: 3r6Wka0268s102 ]"}*/
	}
}
