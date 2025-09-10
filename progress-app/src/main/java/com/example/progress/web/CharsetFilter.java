package com.example.progress.web;

import javax.servlet.*;
import java.io.IOException;

public class CharsetFilter implements Filter {
  private String encoding = "UTF-8";

  @Override
  public void init(FilterConfig filterConfig) {
    String enc = filterConfig.getInitParameter("encoding");
    if (enc != null && !enc.isEmpty()) {
      encoding = enc;
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (request.getCharacterEncoding() == null) {
      request.setCharacterEncoding(encoding);
    }
    response.setCharacterEncoding(encoding);
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {}
}

