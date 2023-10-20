package com.dev.TomoAdministration.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig {
	
	private final String[] visitorsUrls = {
    		"/login",
    		"/memberRegistration",
    		"/**",
    		"/api/**"
    		};
    private final String[] adminsUrls = {
    		"/admin/**", 
    		};
    
    @Bean   
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/resources/**");   
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
			.csrf()
				.disable()
			.authorizeRequests()
				.antMatchers(adminsUrls).hasAuthority("ROLE_ADMIN")
				.antMatchers(visitorsUrls).permitAll()
			.anyRequest().authenticated()	
//			.and()
//			.authorizeRequests()
//				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()	
//	    		.antMatchers(HttpMethod.DELETE).hasRole("ADMIN")
//	    		.antMatchers(adminsUrls).hasRole("ADMIN")
//	    		.antMatchers(visitorsUrls).permitAll()
//	    		.anyRequest()
//	    		.authenticated()
    		.and()
	        .formLogin()
				.loginPage("/admin/login")
				.loginProcessingUrl("/admin/loginProcess")
				.defaultSuccessUrl("/ai/prompt")
			.and()
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/admin/login")
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

