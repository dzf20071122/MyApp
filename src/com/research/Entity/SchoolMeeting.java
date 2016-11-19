package com.research.Entity;

import java.io.Serializable;
import java.util.ArrayList;

import com.research.org.json.JSONArray;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class SchoolMeeting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5593560802916205805L;
	public String name;
	public String id;
	public String icon;
	public int num; //人数
	public int Sum;//校友会总人数
	public Boolean isJoin; // 是否加入
	public int sortType; //
	public String sec; //描述
	
	public SchoolMeeting(){}
	
	public SchoolMeeting(JSONObject json) {

		try {
			if (!json.isNull("id")) {
				id = json.getString("id");
			}
			if (!json.isNull("name")) {
				name = json.getString("name");
			}

			if (!json.isNull("logo")) {
				icon = json.getString("logo");
			}
			if (!json.isNull("join")) {
				isJoin = json.getBoolean("join");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public SchoolMeeting(int mType,String mName, String mId){
		name = mName;
		id = mId;
		sortType = mType;
		
	}
}
