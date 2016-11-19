package com.research.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.research.org.json.JSONArray;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class ResearchProject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ResearchJiaState state;
	
	public List<ResearchProjectItem> childList;

	public ResearchProject() {
		super();
	}
	
	public ResearchProject(String reqString) {
		super();
		if(reqString == null || reqString.equals("")){
			return;
		}
		try {
			JSONObject json = new JSONObject(reqString);
			if(!json.isNull("state")){
				this.state = new ResearchJiaState(json.getJSONObject("state"));
			}
			if(!json.isNull("data")){
				String dataString = json.getString("data");
				if(dataString!=null && !dataString.equals("")){
					JSONArray jsonArray = json.getJSONArray("data");
					if(jsonArray!=null && jsonArray.length()>0){
						childList = new ArrayList<ResearchProjectItem>();
						for (int i = 0; i < jsonArray.length(); i++) {
							childList.add(new ResearchProjectItem(jsonArray.getJSONObject(i)));
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
