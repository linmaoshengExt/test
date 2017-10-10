package pers.aidenj.payment.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class FileUtil {
	
	public final static String CONFIG_FILE_NAME = "file.properties";
	
	public static void main(String[] args) {
		System.out.println(initConfigFile("file.properties").getProperty("tempPath"));
	}
	
	/**
	 * 上传单个临时文件
	 *
	 * @author 周化益
	 * @param is 文件数据
	 * @param fileName 文件名
	 * @return boolean 文件是否保存成功
	 */
	public static String upLoadFile(InputStream is, String fileName) {
		boolean back = false;
		String filePath = "";
		String path = "";
		
		File folder = new File(initConfigFile().getProperty("realPath"));
		if (null != folder) {
	        String folderPath = folder.getPath();
			if (!folder.exists()) {
				folder.mkdirs();//新建文件存放的文件夹
			}
			String postPath = new Date().getTime() + "." + (fileName.split("\\.")[1]);
			
			filePath = folderPath + "/" + postPath;
			
			path = initConfigFile("file.properties").getProperty("postPath") + postPath;
			
			try {
				OutputStream os = new FileOutputStream(filePath);
				back = copyFile(is,os);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(back == false) {
			path = "";
		}
		return path;
	}
	
	/**
	 * 将临时文件拷贝到真是存放路径中
	 * 
	 * @author zhy
	 * @param pathList
	 * @return
	 */
	public static String uploadRealFile(List<String> pathList) {
		String path = "";
		File realPath = new File(initConfigFile("file.properties").getProperty("realPath"));
		if (null != realPath) {
			if (!realPath.exists()) {
				realPath.mkdirs();//新建文件存放的文件夹
			}
		}
		
		for (String string : pathList) {
			File file = new File(string);
			try {
				InputStream is = new FileInputStream(file);
				String postPath = new Date().getTime() + "." + (string.split("\\.")[1]);
				String outPath = realPath.getPath()  + "/" + postPath;
				
				postPath = initConfigFile("file.properties").getProperty("postPath") + postPath;
				path += "," + postPath;
				
				OutputStream os = new FileOutputStream(outPath);
				boolean bool = copyFile(is,os);
				if(bool) {
					file.delete();
				} else {
					bool = copyFile(is,os);
					if(bool == false) {
						return "";
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return path.substring(1);
	}
	
	/**
	 * 上传单个临时文件
	 *
	 * @author 周化益
	 * @param is 文件数据
	 * @return boolean 文件是否保存成功
	 */
	public static <T> String upLoadTempFile(InputStream is,long userId, String fileName) {
		boolean back = false;
		String filePath = "";
		File folder = new File(initConfigFile("file.properties").getProperty("tempPath") + "/" + userId);
		if (null != folder) {
	        String folderPath = folder.getPath();
			if (!folder.exists()) {
				folder.mkdirs();//新建文件存放的文件夹
			}
			filePath = folderPath + "/" + fileName;
			try {
				OutputStream os = new FileOutputStream(filePath);
				back = copyFile(is,os);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(back == false) {
			filePath = "";
		}
		return filePath;
	}
	
	/**
	 * 上传单个文件
	 *
	 * @author 周化益
	 * @param entityName 实体Class
	 * @param record_Id 文件ID
	 * @param is 文件数据
	 * @param fileName 文件名
	 * @param isVideo 是否是视频,如果是视频传入true，图片则为false
	 * @return boolean 文件是否保存成功
	 */
	public static <T> Boolean upLoadPath(Class<T> entityName,long record_Id,InputStream is,
			String fileName, boolean isVideo) {
		boolean back = false;
		File folder = new File(initConfigFile("file.properties").getProperty("tmpPath"));
		if (null != folder) {
	        String folderPath = folder.getPath();
			if (!folder.exists()){
				folder.mkdirs();//新建文件存放的文件夹
			}
			String filePath = folderPath+"/" + fileName;
			try {
				OutputStream os = new FileOutputStream(filePath);
				back = copyFile(is,os);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return back;
	}
	
	/**
	 * 获取Properties
	 *
	 * @author 周化益
	 * @return Properties
	 */
	public static Properties initConfigFile(String fileName){
		Properties props = new Properties();
        try {
			props.load(FileUtil.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return props;
	}
	
	
	/**
	 * 获取Properties
	 *
	 * @author 周化益
	 * @return Properties
	 */
	public static Properties initConfigFile(){
		Properties props = new Properties();
        try {
			props.load(FileUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return props;
	}
	
	/**
	 * 拷贝文件
	 *
	 * @author 周化益
	 * @param is 输入流
	 * @param os 输出流
	 * @return boolean 是否拷贝成功
	 */
	public synchronized static boolean copyFile(InputStream is,OutputStream os){
		boolean isCopySuccess = false;
		byte buffer[] = new byte[1024];
        int len = 0;
        try {
        	while ((len=is.read(buffer)) != -1) {
            	os.write(buffer, 0, len);
    		}
            os.flush();
            isCopySuccess = true;
		} catch (Exception e) {
		} finally {
			try {
				if (null!=is){
					is.close();
				}
				if (null!=os){
					os.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
        return isCopySuccess;
	}

}
