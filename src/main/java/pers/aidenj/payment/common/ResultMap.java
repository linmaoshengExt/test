package pers.aidenj.payment.common;


import java.util.HashMap;
import java.util.Map;

/**
 * 响应信息格式化统一返回工具类
 * @author DAIYIHUI
 */
public class ResultMap {
	/**
	* @author DAIYIHUI
	* @date 2016年7月1日 上午10:57:51
	* @param type success 成功  fail:失败
	* @param code 响应码
	* @param message 响应信息
	* @param data 返回的数据
	* @return
	 */
	public static Map<String, Object> getResultMap(String resultType,int resultCode,Object resultData){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("resultType", resultType);
		resultMap.put("resultCode", resultCode);
		resultMap.put("resultData", resultData);
		resultMap.put("resultMessage", DataConstant.codeMessage.get(resultCode));
		return resultMap;
	}
	
	/**
	* 成功调用的方法
	* 
	* @author zhy
	* @date 2016年7月1日 上午10:57:51
	* @param code 响应码
	* @param message 响应信息
	* @param data 返回的数据
	* @return
	 */
	public static Map<String, Object> getResultMap(Map<String, Object> resultData,Object resultCode){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("resultType", "success");
		resultMap.put("resultCode", DataConstant.SUCCESS);
		resultMap.put("resultData", resultData);
		resultMap.put("resultMessage", DataConstant.codeMessage.get(resultCode));
		return resultMap;
	}
	
	/**
	* 成功调用的方法
	* 
	* @author zhy
	* @date 2016年7月1日 上午10:57:51
	* @param code 响应码
	* @param message 响应信息
	* @param data 返回的数据
	* @return
	 */
	public static Map<String, Object> getResultMap(Object resultCode){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("resultType", "success");
		resultMap.put("resultCode", DataConstant.SUCCESS);
		resultMap.put("resultData", "");
		resultMap.put("resultMessage", DataConstant.codeMessage.get(resultCode));
		return resultMap;
	}
	
	/**
	* 失败调用的方法
	*
	* @author zhy
	* @date 2016年7月1日 上午10:57:51
	* @param code 响应码
	* @param message 响应信息
	* @param data 返回的数据
	* @return
	 */
	public static Map<String, Object> getResultMap(int resultCode) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("resultType", "fail");
		resultMap.put("resultCode", resultCode);
		resultMap.put("resultData", null);
		resultMap.put("resultMessage", DataConstant.codeMessage.get(resultCode));
		return resultMap;
	}
	public static void main(String[] args) {
		System.out.println(getResultMap(0));
	}
}
