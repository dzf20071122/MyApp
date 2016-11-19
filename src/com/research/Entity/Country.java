package com.research.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.research.org.json.JSONArray;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class Country implements Serializable{

	private static final long serialVersionUID = -146445435454L;
	
	public String countryID;//id
	public String country;//text
	public List<ChildCity> childList;
	
	public Country(){
		
	}

	public Country(JSONObject json){
		try {
			countryID = json.getString("id");
			country = json.getString("State");
		
			if(!json.isNull("Cities")){
				JSONArray jsonArray = json.getJSONArray("Cities");
				childList = new ArrayList<ChildCity>();
				for (int i = 0; i < jsonArray.length(); i++) {
					childList.add(new ChildCity(jsonArray.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
