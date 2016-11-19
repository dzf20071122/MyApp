package com.research.io.file;

import java.io.File;

import android.os.Environment;

public class AndroidFolder {
	/** 
	 * 获取SD卡根目录路径 
	 *  
	 * @return 
	 */  
	public static String getSdCardPath() {  
	    boolean exist = isSdCardExist();  
	    String sdpath = "";  
	    if (exist) {  
	        sdpath = Environment.getExternalStorageDirectory()  
	                .getAbsolutePath();  
	    } else {  
	        sdpath = "不适用";  
	    }  
	    return sdpath;  
	  
	}  
	/** 
	 * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡] 
	 *  
	 * @return 
	 */  
	public static boolean isSdCardExist() {  
	    return Environment.getExternalStorageState().equals(  
	            Environment.MEDIA_MOUNTED);  
	} 
	/** 
	 * 获取默认的文件路径 
	 *  
	 * @return 
	 */  
	public static String getDefaultFilePath() {  
	    String filepath = "";  
	    File file = new File(Environment.getExternalStorageDirectory(),  
	            "abc.txt");  
	    if (file.exists()) {  
	        filepath = file.getAbsolutePath();  
	    } else {  
	        filepath = "不适用";  
	    }  
	    return filepath;  
	}  
}
