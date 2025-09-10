package com.example.progress.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (req.getSession(false) != null) req.getSession(false).invalidate();
    // delete remember-me cookies
    String path = req.getContextPath().isEmpty()? "/" : req.getContextPath();
    Cookie cu = new Cookie("REMEMBER_USER", ""); cu.setMaxAge(0); cu.setPath(path); cu.setHttpOnly(true); resp.addCookie(cu);
    Cookie cg = new Cookie("REMEMBER_GID", "0"); cg.setMaxAge(0); cg.setPath(path); cg.setHttpOnly(true); resp.addCookie(cg);
    resp.sendRedirect(req.getContextPath() + "/");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doPost(req, resp);
  }
}
