package com.research.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.research.org.json.JSONArray;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class OrderMenuItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;
	public String url;
	public List<OrderMenuItem> childMenuList;
	public OrderMenuItem() {
		super();
	}
	
	public OrderMenuItem(JSONObject json) {
		super();
		if(json == null || json.equals("")){
			return;
		}
		
		try {
			id = json.getInt("id");
			name = json.getString("name");
			url = json.getString("url");
			if(!json.isNull("children")){
				String childObj = json.getString("children");
				if(childObj!=null && !childObj.equals("")){
					JSONArray array = json.getJSONArray("children");
					childMenuList = new ArrayList<OrderMenuItem>();
					for (int i = 0; i <array.length(); i++) {
						childMenuList.add(new OrderMenuItem(array.getJSONObject(i)));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
