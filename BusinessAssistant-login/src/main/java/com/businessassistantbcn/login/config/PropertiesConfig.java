package com.businessassistantbcn.login.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("security.datasource")
@Getter @Setter
public class PropertiesConfig {
	
	private String signUpUrl;
	private String secret;
	private String headerString;
	private String tokenPrefix;
	private long expiresIn;
	private String authorities; // lista de permisos (ROLES)
	private String err;
	
}