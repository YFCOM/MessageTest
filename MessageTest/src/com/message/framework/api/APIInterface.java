package com.message.framework.api;

import com.message.framework.bean.GetLoginResult;



public interface APIInterface {

	public void returnSigninSuccess(GetLoginResult getLoginResult);
	public void returnSigninFail(String error);
	

}
