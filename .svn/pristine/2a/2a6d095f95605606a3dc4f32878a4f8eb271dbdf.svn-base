package com.research.Entity;

import java.io.Serializable;

import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class LoginResult implements Serializable{

	private static final long serialVersionUID = 113454353454L;
	
	public Login mLogin;
	public ResearchJiaState mState;

	
	public LoginResult(){}
	
	public LoginResult(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			
			if(!json.isNull("data")){
				mLogin = new Login(json.getJSONObject("data"));
			}
			
			if(!json.isNull("state")){
				mState = new ResearchJiaState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
