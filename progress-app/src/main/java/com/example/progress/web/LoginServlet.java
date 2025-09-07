package com.example.progress.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  private String expectedUser;
  private String expectedPass;

  @Override public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext ctx = config.getServletContext();
    String envUser = trim(System.getenv("AUTH_USER"));
    String envPass = trim(System.getenv("AUTH_PASS"));
    expectedUser = !envUser.isEmpty() ? envUser : nvl(ctx.getInitParameter("auth.username"), "admin");
    expectedPass = !envPass.isEmpty() ? envPass : nvl(ctx.getInitParameter("auth.password"), "changeit");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String u = nvl(req.getParameter("username"));
    String p = nvl(req.getParameter("password"));
    if (u.equals(expectedUser) && p.equals(expectedPass)) {
      HttpSession session = req.getSession(true);
      session.setAttribute("USER", u);
      resp.sendRedirect(req.getContextPath() + "/classes");
      return;
    }
    req.setAttribute("loginError", "ユーザー名またはパスワードが正しくありません。");
    req.getRequestDispatcher("/index.jsp").forward(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.sendRedirect(req.getContextPath() + "/");
  }

  private static String nvl(String s) { return s==null? "" : s; }
  private static String nvl(String s, String d) { return s==null? d : s; }
  private static String trim(String s) { return s==null? "" : s.trim(); }
}
