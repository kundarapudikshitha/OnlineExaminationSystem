
package com.exam.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // Get existing session without creating a new one
        HttpSession session = request.getSession(false);

        // Check admin login status
        if (session == null
                || session.getAttribute("loggedInAdmin") == null) {

            // Redirect to admin login page
            response.sendRedirect(
                    request.getContextPath() + "/admin/login");

            return false;
        }

        return true;
    }
}
