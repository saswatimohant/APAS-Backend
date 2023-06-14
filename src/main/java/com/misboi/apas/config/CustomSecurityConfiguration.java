/*package com.misboi.apas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration 
	extends WebSecurityConfigurerAdapter {

	@Value("${application.security.disabled:false}")
	private boolean securityDisabled;

	@Override
	public void configure(WebSecurity web) throws Exception {
	     
	   if (securityDisabled){ 
		   web.ignoring().antMatchers("/**");
	    }
	    else{
	       
	    }  
	}
	
	@Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll();
    }
}*/