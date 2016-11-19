package com.research.Entity;

import java.io.Serializable;

public class SchoolMeetingMembers implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7815136588530213727L;
	
	public String uid; //UID 用户的唯一ID
	public String name; //UID 用户的唯一ID
	/**
	 * {"data":{"visit":"3","count":93,"interactive":0},"state":{"code":0,"msg":
	 * "","debugMsg":""}}
	 */
	public SchoolMeetingMembers(String uid,String name){
		this.uid = uid;
		this.name = name;
	}
}
