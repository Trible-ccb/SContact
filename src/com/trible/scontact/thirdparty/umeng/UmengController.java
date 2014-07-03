package com.trible.scontact.thirdparty.umeng;

import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

public class UmengController {

	public static UMSocialService getLoginService(){
		return UMServiceFactory.getUMSocialService("com.umeng.login", RequestType.SOCIAL);
	}
	public static UMSocialService getService(){
		return UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);
	}
}
