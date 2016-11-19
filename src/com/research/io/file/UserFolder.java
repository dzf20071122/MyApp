package com.research.io.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

/**
 * 用户文件夹操作
 * @author Runt02
 *
 */
public class UserFolder {
	
	//声明数组用来存储文件列表
	public File[] list;
	//声明文件，用来设置用户所属的文件夹
	File userFolder;
	File rootfile=Environment.getRootDirectory();
	public static String[] folders=new String[]{"/todayLogin","/todayChat","/todayAdd","/updateAdd"};
	String uFolder;
	public static String datapath="/data/data/com.research/files/";
	/*
	 * 指定用户文件夹名称
	 */
	public UserFolder(String uName){
		//判断文件夹是否存在
		uFolder=AndroidFolder.getSdCardPath()+datapath+uName;
		userFolder=new File(uFolder);
		if(userFolder.exists()){
			
		}else{
			//如果不存在则创建该目录及父目录和子目录
			userFolder.mkdirs();
			for(int i=0;i<folders.length;i++){
				File temp=new File(uFolder+folders[i]);
				if(!temp.exists())
					temp.mkdir();
			}
		}
	}
	
	/*
	 * 用户文件夹内的文件数量
	 */
	public File[] getFiles(String path){
		File file = new File(uFolder+"/"+path);
		if(!file.exists()){
			file.mkdir();
		}
		list=file.listFiles();
		return list;
	}

	/**
	 * 创建文件
	 * @param path
	 */
	public File EditFile(String fileName){
		File file=new File(uFolder+fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 写文件
	 * @param path  文件名
	 * @param content 要写的内容
	 * @return
	 */
	public static boolean write(String filename,String content){
		try {
			File file=new File(datapath+filename);
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}

	/**
	 * 追加一行
	 * @param path  文件路径
	 * @param content 要写的内容
	 * @return
	 */
	public static boolean writeLine(String filename,String content){
		
		return true;
	}

	/**
	 * 追加一行
	 * @param path  文件路径
	 * @param content 要写的内容
	 * @return
	 */
	public static boolean overWrite(String filename,String content){
		try {
			File file=new File(datapath+filename);
			if(!file.exists()){
				file.createNewFile();
			}else{
				file.delete();
			}
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
	/**
	 * 删除文件夹
	 * @param folderPath文件夹完整绝对路径
	 */
	public static void delFolder(String folderPath) {
	     try {
	        delAllFile(folderPath); //删除完里面所有内容
	        String filePath = folderPath;
	        filePath = filePath.toString();
	        java.io.File myFilePath = new java.io.File(filePath);
	        myFilePath.delete(); //删除空文件夹
	     } catch (Exception e) {
	       e.printStackTrace(); 
	     }
	}
	/**
	 * 删除指定文件夹下所有文件
	 *
	 *param path 文件夹完整绝对路径
	 */
   public static boolean delAllFile(String path) {
       boolean flag = false;
       File file = new File(path);
       if (!file.exists()) {
         return flag;
       }
       if (!file.isDirectory()) {
         return flag;
       }
       String[] tempList = file.list();
       File temp = null;
       for (int i = 0; i < tempList.length; i++) {
          if (path.endsWith(File.separator)) {
             temp = new File(path + tempList[i]);
          } else {
              temp = new File(path + File.separator + tempList[i]);
          }
          if (temp.isFile()) {
             temp.delete();
          }
          if (temp.isDirectory()) {
             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
             delFolder(path + "/" + tempList[i]);//再删除空文件夹
             flag = true;
          }
       }
       return flag;
     }
   /** * A方法追加文件：使用RandomAccessFile 
    *  @param fileName 文件名 
    *   @param content 追加的内容 */ 
   public static void appendMethodA(String fileName, String content){ 
	   try { // 打开一个随机访问文件流，按读写方式 
		   RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw"); // 文件长度，字节数 
		   long fileLength = randomFile.length(); //将写文件指针移到文件尾。 
		   randomFile.seek(fileLength); 
		   randomFile.writeBytes(content); 
		   randomFile.close(); 
	   } catch (IOException e){ 
		   e.printStackTrace(); 
	   }
	} 
   /** * B方法追加文件：使用FileWriter * @param fileName * @param content */ 
   public static void appendMethodB(String fileName, String content){ 
	   try { //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件 
		   FileWriter writer = new FileWriter(fileName, true); 
		   writer.write(content); writer.close(); 
	   } catch (IOException e) { 
		   e.printStackTrace(); 
	   } 
   } 
   

   public static void main(String[] args) { String fileName = "C:/temp/newTemp.txt"; String content = "new append!"; 
	   //按方法A追加文件 
	   appendMethodA(fileName, content); 
	   appendMethodA(fileName, "append end. \n"); 
	   //按方法B追加文件 
	   appendMethodB(fileName, content); 
	   appendMethodB(fileName, "append end. \n"); 
	
   }
}
