package com.research.Entity;

import java.io.Serializable;

import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class SchoolMeetingMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String uid;
	private String nickname;

	public SchoolMeetingMember() {
	}

	public SchoolMeetingMember(JSONObject jsonObject) {
		// TODO Auto-generated constructor stub

		try {
			if (jsonObject.isNull("uid")) {
				uid = jsonObject.getString("uid");
			}
			if (jsonObject.isNull("nickname")) {
				nickname = jsonObject.getString("nickname");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
