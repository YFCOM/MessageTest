package com.message.framework.bean;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * 
 * 登录信息返回解析类
 * 
 */
public class GetLoginResult {

	private static final String TAG = "GetLoginResult";
	private String userName = "";
	private String userID = "";
	private String cookie = "";

	public GetLoginResult(String response) {
		try {
			JSONObject jObject = new JSONObject(response);
				if (jObject.has("userName")) {
					this.userName = jObject.getString("userName");
				}
				if (jObject.has("userID")) {
					this.userID = jObject.getString("userID");
				}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
	}

	
	
	public String getUserName() {
		return userName;
	}
	
	public String getUserID() {
		return userID;
	}
	

}
