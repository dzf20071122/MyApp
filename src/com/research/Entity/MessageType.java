package com.research.Entity;


public final class MessageType {
	
	/**
	 * 文字
	 */
	public static final int TEXT = 1;

	/**
	 * 图片
	 */
	public static final int PICTURE = 2;
	
	/**
	 * 声音
	 */
	public static final int VOICE = 3;

	
	/**
	 * 位置
	 */
	public static final int MAP = 4;
	
	/**
	 * 通讯录名片
	 */
	public static final int CARD = 5;
	
	
	
	/**
	 * //投票
	 */
	public static final int VOTE=6;
	
	/**
	 * //图文
	 */
	public static final int PHOTOSEE=7;

	
	/**
	 * // 订阅号名片
	 */
	public static final int SUBINFOCARD = 8; 
	
	/**
	 *  // 思八达新闻分享
	 *  
	 */
	public static final int APPNEWS = 9;    
	

	
	
	
	
	
	public static String timeUid(){
		return System.currentTimeMillis() + "";
	}
}
