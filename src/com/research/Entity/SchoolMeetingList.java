package com.research.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.research.org.json.JSONArray;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class SchoolMeetingList implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3626950309132269880L;
	public ArrayList<SchoolMeeting> mSchoolMeetingList;
	public PageInfo mPageInfo;
	public ResearchJiaState mState;
	
	public SchoolMeetingList(){}
	
	public SchoolMeetingList(String reString, int type) {

		try {
			JSONObject json = new JSONObject(reString);
			if (!json.isNull("state")) {
				mState = new ResearchJiaState(json.getJSONObject("state"));
			}

			if (mState != null && mState.code == 0) {
				if (!json.isNull("data")) {
					if(type == 1){
						JSONArray array = json.getJSONArray("data");
						if (array != null && array.length() != 0) {
							mSchoolMeetingList = new ArrayList<SchoolMeeting>();
							for (int i = 0; i < array.length(); i++) {
								SchoolMeeting item = new SchoolMeeting(array.getJSONObject(i));
								mSchoolMeetingList.add(item);
							}
						}
					}else if (type == 2){
						JSONObject data = json.getJSONObject("data");
						Iterator<String> keyIter= data.keys();  
			            String key;  
			            Object value ;  
			            Map<String, Object> valueMap = new HashMap<String, Object>();  
			            mSchoolMeetingList = new ArrayList<SchoolMeeting>();
			            while (keyIter.hasNext()) {  
			                key = keyIter.next();  
			                value = data.get(key);  
			                valueMap.put(key, value);  
			                JSONObject arr = (JSONObject) value;
			                SchoolMeeting item = new SchoolMeeting(arr);
			                item.id = key;
			                if (item.isJoin != null && item.isJoin) {
			                	item.sortType = 2;
			                }else{
			                	item.sortType = 3;
			                }
							mSchoolMeetingList.add(item);
			            }  
					}
				}
			}
			if (!json.isNull("pageInfo")) {
				mPageInfo = new PageInfo(json.getJSONObject("pageInfo"));
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
