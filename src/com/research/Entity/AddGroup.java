package com.research.Entity;

import java.io.Serializable;

import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class AddGroup implements Serializable{

	private static final long serialVersionUID = -14641564545645L;
	public Group mGroup;
	public ResearchJiaState mState;
	
	public AddGroup(){}
	
	public AddGroup(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("state")){
				mState = new ResearchJiaState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("data")){
				mGroup = new Group(json.getJSONObject("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
