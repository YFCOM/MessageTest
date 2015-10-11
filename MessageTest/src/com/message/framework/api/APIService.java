package com.message.framework.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;





import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.message.framework.bean.GetLoginResult;
import com.message.framework.utils.EncryptUtils;
import com.message.framework.utils.LogUtils;

public class APIService {
	private APIInterface apiInterface;
	private static final int TYPE_NORMAL = 3;
	private static final int TYPE_REFRESH = 4;
	private static final int TYPE_MORE = 5;
	private static final String TAG = "APIService";
	private Context context;
	private static String baseUrl = "http://mob.inspur.com/api?";
	// private static String baseUrl = "http://10.24.11.121:3000/api?";
	// private static String baseUrl = "http://10.24.12.200:80/api?";
	private String cookie = "";

	public APIService(Context context) {
		this.context = context;
//		cookie = ((MyApplication) context.getApplicationContext()).getCookie();
	}

	public void setAPIInterface(APIInterface apiInterface) {
		this.apiInterface = apiInterface;
	}

	/**
	 * 登录
	 * 
	 * @param userName
	 * @param password
	 */
	public void signin(String userName, String password) {
		String module = "sign";
		String method = "signin";
		RequestParams params = new RequestParams();
		try {
			password = EncryptUtils.encode(password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "EncryptUtilslog=" + e.toString());
		}
		params.put("userName", userName);
		params.put("password", password);
		params.put("deviceOS", 2);
		String completeUrl = baseUrl + "module=" + module + "&method=" + method;
		AsyncHttpClient client = new AsyncHttpClient();
		AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				apiInterface.returnSigninSuccess(new GetLoginResult(new String(
						arg2)));
//				((MyApplication) context.getApplicationContext())
//						.saveCookie(arg1);

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				apiInterface.returnSigninFail(new String(arg2));
			}
		};
		asyncHttpResponseHandler.setIsDebug(LogUtils.isDebug);
		client.post(completeUrl, params, asyncHttpResponseHandler);
	}

	/**
	 * 注销登录
	 */
	public void signout() {
		String module = "sign";
		String method = "signout";
		String completeUrl = baseUrl + "module=" + module + "&method=" + method;
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Cookie", cookie);
		AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
//				apiInterface.returnSignoutSuccess(new GetSignoutResult(
//						new String(arg2)));

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
//				apiInterface.returnSignoutFail(new String(arg2));
			}
		};
		asyncHttpResponseHandler.setIsDebug(LogUtils.isDebug);
		client.post(completeUrl, asyncHttpResponseHandler);

	}



}
