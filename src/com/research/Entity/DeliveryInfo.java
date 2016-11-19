package com.research.Entity;

import java.io.Serializable;

import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class DeliveryInfo implements Serializable{

	private static final long serialVersionUID = -14564545455L;
	
	public Delivery mDelivery;
	public ResearchJiaState mState;
	
	public DeliveryInfo(){
		
	}
	
	public DeliveryInfo(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				mDelivery = new Delivery(json.getJSONObject("data"));
			}
			
			if(!json.isNull("state")){
				mState = new ResearchJiaState(json.getJSONObject("state"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
