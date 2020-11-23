package com.pretask.share.util;

import org.apache.commons.lang3.RandomStringUtils;

public class TokenUtil {

	private static TokenUtil tokenUtil;
	
	public static TokenUtil getInstance() {
		if(tokenUtil == null) tokenUtil = new TokenUtil();
		return tokenUtil;
	}
	
	String a = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	
	public String makeToken() {
		return RandomStringUtils.random(3, a);
	}
}
