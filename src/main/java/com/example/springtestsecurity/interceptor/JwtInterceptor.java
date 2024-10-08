package com.example.springtestsecurity.interceptor;

import com.example.springtestsecurity.service.PermissionService;
import com.example.springtestsecurity.service.RedisService;
import com.example.springtestsecurity.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;
    private final RedisService redisService;
    private final PermissionService permissionService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return unauthorizedResponse(response, "001", "Missing or invalid token");
        }

        token = token.substring(7);
        try{
            String username= jwtUtils.getUsernameFromToken(token);
        }catch(ExpiredJwtException e){
            return unauthorizedResponse(response, "001", "Invalid token");
        }
        String username= jwtUtils.getUsernameFromToken(token);
        String requestPath=request.getRequestURI();
        String requestMethod=request.getMethod();
        if (!redisService.isTokenExists(username)) {
            return unauthorizedResponse(response, "001", "Missing or invalid token in redis");
        }

        if (!jwtUtils.validateToken(token)) {
            return unauthorizedResponse(response, "001", "Invalid token");
        }
        boolean hasPermission= permissionService.hasPermission(requestPath,requestMethod,username);
        if(!hasPermission){
            return unauthorizedResponse(response,"002","The account does not have permission to perform the function");
        }
        return true;
    }

    private boolean unauthorizedResponse(HttpServletResponse response, String errorCode, String errorMessage) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error_cd\": \"%s\", \"error_msg\": \"%s\"}", errorCode, errorMessage));
        return false;
    }
}
