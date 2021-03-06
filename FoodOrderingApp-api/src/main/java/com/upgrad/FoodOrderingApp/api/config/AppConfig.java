package com.upgrad.FoodOrderingApp.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.upgrad.FoodOrderingApp.api.interceptor.AuthenticationInterceptor;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	AuthenticationInterceptor authenticationInterceptor;

	/*Add all paths that require authentication checks here*/
	String[] pathPatternToIntercept = { "/order/coupon/{coupon_name}", "/order", "/customer/logout", "/customer",
			"/customer/password" };
	List<String> pathsToIntercept = new ArrayList<String>(Arrays.asList(pathPatternToIntercept));

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor).addPathPatterns(pathsToIntercept);
	}
}
