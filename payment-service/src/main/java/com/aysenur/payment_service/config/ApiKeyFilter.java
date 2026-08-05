package com.aysenur.payment_service.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

	private final byte[] expectedApiKey;

	public ApiKeyFilter(
        	@Value("${app.api-key}") String apiKey
	) {
   	 this.expectedApiKey =
        	    apiKey.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected void doFilterInternal(
        	HttpServletRequest request,
        	HttpServletResponse response,
        	FilterChain filterChain
	) throws ServletException, IOException {

	String path = request.getRequestURI();

	if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
    filterChain.doFilter(request, response);
    return;
    }

	if (path.equals("/api/payments/callback")
        	|| path.equals("/api/payments/webhook")
		|| path.startsWith("/swagger-ui")
        	|| path.startsWith("/v3/api-docs")) {

    	filterChain.doFilter(request, response);
    	return;
	}

	String requestApiKey =
        request.getHeader("X-Api-Key");

	if (requestApiKey == null) {
    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	response.getWriter().write("API anahtarı eksik.");
    	return;
	}

	byte[] receivedApiKey =
        	requestApiKey.getBytes(StandardCharsets.UTF_8);

	boolean apiKeyValid =
        	MessageDigest.isEqual(
                	expectedApiKey,
                	receivedApiKey
        	);

	if (!apiKeyValid) {
    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	response.getWriter().write("Geçersiz API anahtarı.");
    	return;
	}

	filterChain.doFilter(request, response);



	}

}
