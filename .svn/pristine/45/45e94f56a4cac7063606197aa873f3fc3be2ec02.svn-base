package com.research.Entity;

import java.io.Serializable;

import com.research.net.ResearchInfo;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class PushPic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String path;
	public String bPath;
	public String key;
	public PushPic(int id, String path, String key) {
		super();
		this.id = id;
		this.path = path;
		this.key = key;
	}
	public PushPic() {
		super();
	}
	
	public PushPic(String reString) {
		super();
		if(reString == null || reString.equals("")){
			return;
		}
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("id")){
				this.id = json.getInt("id");
			}
			if(!json.isNull("path")){
				String imgUrl = json.getString("path");
				if(imgUrl!=null && !imgUrl.equals("")){
					this.path = ResearchInfo.HEAD_URL+imgUrl;
				}
			}
			if(!json.isNull("bpath")){
				String imgUrl = json.getString("bpath");
				if(imgUrl!=null && !imgUrl.equals("")){
					this.bPath = ResearchInfo.HEAD_URL+imgUrl;
				}
			}
			if(!json.isNull("key")){
				this.key = json.getString("key");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
