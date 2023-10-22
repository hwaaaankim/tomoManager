package com.dev.TomoAdministration.config;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig {
	
	private final String[] visitorsUrls = {
    		
			"/signin/**",
			"/signin",
    		"/signup/**",
    		"/api/v1/buyerRegistration",
    		"/api/**",
    		"/api/v1/**",
    		"/all/**",
    		"/error",
    		"/resources/**",
    		"/signupProcess",
    		"/emailTest",
    		"/administration/js/**",
    		"/administration/fonts/**",
    		"/administration/images/**",
    		"/administration/json/**",
    		"/administration/lang/**",
    		"/administration/libs/**",
    		"/**"
    		};
	private final String[] memberUrls = {
			"/index",
			"/",
			"/member/**"
	};
    private final String[] adminsUrls = {
    		"/admin/**", 
    		};
    
	@Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }
    
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
       
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
			.csrf()
				.disable()
			.authorizeRequests()
			.antMatchers(adminsUrls).hasAuthority("ROLE_ADMIN")
			.antMatchers(memberUrls).hasAnyAuthority("ROLE_ADMIN", "ROLE_MEMBER")
			.antMatchers(visitorsUrls).permitAll()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()	
			.anyRequest().authenticated()	
    		.and()
	        .formLogin()
				.loginPage("/signin")
				.loginProcessingUrl("/signinProcess")
				.defaultSuccessUrl("/index")
			.and()
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/signin")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
				.permitAll()
			.and()
			.rememberMe()
				.rememberMeParameter("remember")
				.tokenValiditySeconds(86400)
				.alwaysRemember(false);
//				.userDetailsService(userDetailsService);
                
        return http.build();
    }

}

