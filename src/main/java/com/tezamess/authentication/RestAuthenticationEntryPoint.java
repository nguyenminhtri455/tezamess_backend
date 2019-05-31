package com.tezamess.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tezamess.model.ResultModelV2;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResultModelV2 resultModelV2 = new ResultModelV2(ResultModelV2.Status.ERROR_AUTHORICATION.getStatus()
                , null
                , ResultModelV2.Status.ERROR_AUTHORICATION.name()
                , new Date());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(mapper.writeValueAsString(resultModelV2));
    }
}
