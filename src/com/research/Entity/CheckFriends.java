package com.research.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.research.org.json.JSONArray;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class CheckFriends implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<CheckFriendsItem> childList;
	public ResearchJiaState status;
	public CheckFriends(List<CheckFriendsItem> childList, ResearchJiaState status) {
		super();
		this.childList = childList;
		this.status = status;
	}
	public CheckFriends() {
		super();
	}
	
	public CheckFriends(String reqString) {
		super();
		try {
			JSONObject jsonObj = new JSONObject(reqString);
			if (!jsonObj.isNull("data")) {
				JSONArray array = jsonObj.getJSONArray("data");
				if (array!=null && array.length()>0) {
					childList = new ArrayList<CheckFriendsItem>();
					for (int i = 0; i < array.length(); i++) {
						childList.add(new CheckFriendsItem(array.getJSONObject(i)));
					}
				}
			}
			if(!jsonObj.isNull("state")){
				status = new ResearchJiaState(jsonObj.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
