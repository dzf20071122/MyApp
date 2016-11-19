package com.research.Entity;

import java.io.Serializable;

import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class ProductDetailInfo implements Serializable{

	private static final long serialVersionUID = -1564678166341312L;
	
	public ProductDetail mProductDetail;
	public ResearchJiaState mState;
	
	public ProductDetailInfo(){}
	
	public ProductDetailInfo(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				mProductDetail = new ProductDetail(json.getJSONObject("data"));
			}
			
			if(!json.isNull("state")){
				mState = new ResearchJiaState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
