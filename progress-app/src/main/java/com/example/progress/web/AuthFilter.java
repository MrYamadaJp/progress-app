package com.example.progress.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {
  @Override public void init(FilterConfig filterConfig) {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    String uri = req.getRequestURI();
    // Allow static and auth endpoints
    if (uri.startsWith(req.getContextPath() + "/assets/") ||
        uri.equals(req.getContextPath() + "/") ||
        uri.equals(req.getContextPath() + "/index.jsp") ||
        uri.equals(req.getContextPath() + "/login") ||
        uri.equals(req.getContextPath() + "/logout") ||
        uri.equals(req.getContextPath() + "/healthz")) {
      chain.doFilter(request, response);
      return;
    }

    HttpSession session = req.getSession(false);
    if (session != null && session.getAttribute("USER") != null) {
      chain.doFilter(request, response);
      return;
    }
    // Not logged in: redirect to index (login)
    resp.sendRedirect(req.getContextPath() + "/");
  }

  @Override public void destroy() {}
}
