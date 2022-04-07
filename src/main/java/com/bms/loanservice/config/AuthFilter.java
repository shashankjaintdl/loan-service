package com.bms.loanservice.config;

import com.bms.loanservice.entity.CustomerAuthResponse;
import com.bms.loanservice.service.impl.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private RestTemplateService restTemplateService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authKey = request.getHeader("Auth-Key");
        String authUser = request.getHeader("Auth-User");


        System.out.println(request.getRequestURI());
        if(request.getRequestURI().contains("swagger") || request.getRequestURI().contains("v3")){
            filterChain.doFilter(request,response);
        }


        else if(authKey==null ){
            throw new IllegalArgumentException("Provide a valid token");
        }

        else {
            CustomerAuthResponse customerAuthResponse = restTemplateService.checkUserAuthorization(authKey, "http://localhost:8080//check/authorize/" + authKey + "/key");

            if (customerAuthResponse == null) {
                throw new IllegalArgumentException("Illegal Access");
            }
            if (customerAuthResponse.getCustomerId()!=Long.parseLong(authUser)){
                throw new IllegalArgumentException("either key expired or wrong");
            }
            filterChain.doFilter(request,response);

        }
    }
}
