package com.pretask.share.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.pretask.share.exception.ApiHeaderException;

public class ApiInterceptor implements HandlerInterceptor {
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(request.getHeader("X-USER-ID")==null) throw new ApiHeaderException("header정보가 상이합니다.[X-USER-ID 필수값]");
		if(request.getHeader("X-ROOM-ID")==null) throw new ApiHeaderException("header정보가 상이합니다.[X-ROOM-ID 필수값]");
        return true;
    }
}
