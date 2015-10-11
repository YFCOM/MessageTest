package com.message.framework.application;

import android.app.Application;
import android.util.Log;

import com.easemob.chat.EMChat;

public class MyApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		EMChat.getInstance().init(getApplicationContext());
		 
		/**
		 * debugMode == true 时为打开，sdk 会在log里输入调试信息
		 * @param debugMode
		 * 在做代码混淆的时候需要设置成false
		 */
		Log.d("yfcLog", "执行了application");
		EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
		super.onCreate();
	}
}
