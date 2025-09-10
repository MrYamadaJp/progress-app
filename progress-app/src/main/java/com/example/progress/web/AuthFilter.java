package com.example.progress.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
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
    // Try remember-me cookies
    String rememberedUser = null;
    Integer rememberedGroup = null;
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie c : cookies) {
        if ("REMEMBER_USER".equals(c.getName())) rememberedUser = c.getValue();
        if ("REMEMBER_GID".equals(c.getName())) {
          try { rememberedGroup = Integer.parseInt(c.getValue()); } catch (Exception ignored) {}
        }
      }
    }
    if (rememberedUser != null && !rememberedUser.isEmpty()) {
      HttpSession s = req.getSession(true);
      s.setAttribute("USER", rememberedUser);
      if (rememberedGroup != null && rememberedGroup > 0) s.setAttribute("GROUP_ID", rememberedGroup);
      chain.doFilter(request, response);
      return;
    }
    // Not logged in: redirect to index (login)
    resp.sendRedirect(req.getContextPath() + "/");
  }

  @Override public void destroy() {}
}
