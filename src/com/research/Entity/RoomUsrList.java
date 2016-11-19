package com.research.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.research.org.json.JSONArray;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class RoomUsrList implements Serializable{

	private static final long serialVersionUID = 146545456456L;
	public List<Login> mUserList;
	public ResearchJiaState mState;
	
	public RoomUsrList(){}
	
	public RoomUsrList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				JSONArray array = json.getJSONArray("data");
				if(array != null && array.length() != 0){
					mUserList = new ArrayList<Login>();
					for (int i = 0; i < array.length(); i++) {
						mUserList.add(new Login(array.getJSONObject(i)));
					}
				}
			}
			
			if(!json.isNull("state")){
				mState = new ResearchJiaState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
