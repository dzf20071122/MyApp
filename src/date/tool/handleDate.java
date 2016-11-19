package date.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class handleDate {

	   /** 
	    * 获取当前时间的String类型
	    */  
	   public static String getDateToInt() {  
	  
	       String time = "";  
	       try {  
	           Date date = new Date();
	           time = date.getTime() + "";  
	           
	       }  
	       catch (Exception e) {  
	           e.printStackTrace();  
	       }  
	       return time;  
	   }  

	   /** 
	    * 获取指定时间的String类型
	    */  
	   public static String getDateToInt(Date date) {  
	  
		   String time = "";  
	       try {  
	           time = date.getTime() + "";
	       }  
	       catch (Exception e) {  
	           e.printStackTrace();  
	       }  
	       return time;  
	   }

	   /** 
	    * 将String类型的时间转换成只有日期的int类型 
	    */  
	   public static int getDateToInt(String datetime) {  
	  
	       int time = 0;  
	       try {  
	           SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
	    	   Date date= getIntToDate(datetime);
	           String format=sdf.format(date);
	           time = Integer.parseInt(format);
	       }  
	       catch (Exception e) {  
	           e.printStackTrace();  
	       }  
	       return time; 
	   }
	   

	   /** 
	    * 将指定String类型转换为long类型的日期再转换为date
	    */  
	   private static Date getIntToDate(String datetime) {  
		   Date date = null;
	       try {  
	    	   date=new Date(Long.parseLong(datetime));
	       }  
	       catch (Exception e) {  
	           e.printStackTrace();  
	       }  
	       return date;  
	   }  
}
