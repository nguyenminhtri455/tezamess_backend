package com.tezamess.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tezamess.model.ResultModelV2;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ade) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResultModelV2 resultModelV2 = new ResultModelV2(ResultModelV2.Status.ERROR_ACCESS_DENIED.getStatus()
                , null
                , ResultModelV2.Status.ERROR_ACCESS_DENIED.name()
                , new Date());
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(mapper.writeValueAsString(resultModelV2));
    }

}
