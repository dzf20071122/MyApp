package com.research.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.research.org.json.JSONArray;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class UserList implements Serializable{

	private static final long serialVersionUID = 1156435454541L;
	
	public List<Login> mUserList;
	public List<NewFriendItem> newFriendList;
	public ResearchJiaState mState;
	public PageInfo mPageInfo;
	
	public int code;
	public int resultCode;
	public int wallPage;

	public UserList(){}
	
	/**
	 * type == 0 普通用户
	 * type == 1 新的用户
	 * @param reString
	 * @param type
	 */
	public UserList(String reString,int type){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				
				JSONArray array = json.getJSONArray("data");
				if(array != null){
					if(type == 0){
						mUserList = new ArrayList<Login>();
						for (int i = 0; i < array.length(); i++) {
							mUserList.add(new Login(array.getJSONObject(i)));
						}
					}else if(type == 1){
						newFriendList = new ArrayList<NewFriendItem>();
						for (int i = 0; i < array.length(); i++) {
							newFriendList.add(new NewFriendItem(array.getJSONObject(i)));
						}
					}
					
				}
			}
			
			if(!json.isNull("state")){
				mState = new ResearchJiaState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("code")){
				code = json.getInt("code");
			}
			
			if(!json.isNull("pageInfo")){
				mPageInfo = new PageInfo(json.getJSONObject("pageInfo"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public UserList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			resultCode = json.getInt("code");
			
			JSONObject obj = json.getJSONObject("data");
			if(obj!=null && !obj.equals("")){
				
				JSONArray array = obj.getJSONArray("rows");
				if(array != null){
					mUserList = new ArrayList<Login>();
					for (int i = 0; i < array.length(); i++) {
						mUserList.add(new Login(array.getJSONObject(i)));
					}
					
				}
			}
			
			if(!obj.isNull("page")){
				wallPage = obj.getInt("page");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
