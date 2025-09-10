package com.example.progress.web;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  private String expectedUser;
  private String expectedPass;
  private DataSource dataSource;
  private boolean dbReady = false;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext ctx = config.getServletContext();
    String envUser = trim(System.getenv("AUTH_USER"));
    String envPass = trim(System.getenv("AUTH_PASS"));
    expectedUser = !envUser.isEmpty() ? envUser : nvl(ctx.getInitParameter("auth.username"), "admin");
    expectedPass = !envPass.isEmpty() ? envPass : nvl(ctx.getInitParameter("auth.password"), "changeit");

    // Try to enable DB auth via JNDI (users table)
    try {
      InitialContext ic = new InitialContext();
      DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/progress");
      if (ds != null) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
          this.dataSource = ds;
          this.dbReady = true;
        } catch (SQLException e) {
          this.dbReady = false;
          ctx.setAttribute("AUTH_WARNING", "SQLが読み込めなかった");
        }
      }
    } catch (NamingException e) {
      this.dbReady = false;
      ctx.setAttribute("AUTH_WARNING", "SQLが読み込めなかった");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String u = nvl(req.getParameter("username"));
    String p = nvl(req.getParameter("password"));

    // 1) DB認証（利用可能なら優先）
    if (dbReady && dataSource != null) {
      try (Connection c = dataSource.getConnection();
           PreparedStatement ps = c.prepareStatement(
               "SELECT u.user_id, u.display_name, ug.group_id " +
               "FROM users u LEFT JOIN user_group ug ON ug.user_id = u.user_id " +
               "WHERE u.username=? AND u.password=?")) {
        ps.setString(1, u);
        ps.setString(2, p);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            String display = rs.getString("display_name");
            if (display == null || display.isEmpty()) display = u;
            HttpSession session = req.getSession(true);
            session.setAttribute("USER", display);
            int groupId = rs.getInt("group_id");
            if (groupId > 0) session.setAttribute("GROUP_ID", groupId);
            // Remember Me cookies (1 year)
            addRememberCookies(resp, display, groupId, req.getContextPath());
            resp.sendRedirect(req.getContextPath() + "/classes");
            return;
          }
        }
        // ユーザー不一致
        req.setAttribute("loginError", "ユーザー名またはパスワードが正しくありません");
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
        return;
      } catch (SQLException e) {
        // DB認証が利用不能
        req.setAttribute("authWarning", "SQLが読み込めなかった");
        // この後、環境変数/初期値のフォールバックを試す
      }
    }

    // 2) フォールバック（環境変数 or web.xml 初期値）
    if (u.equals(expectedUser) && p.equals(expectedPass)) {
      HttpSession session = req.getSession(true);
      session.setAttribute("USER", u);
      addRememberCookies(resp, u, 0, req.getContextPath());
      resp.sendRedirect(req.getContextPath() + "/classes");
      return;
    }

    req.setAttribute("loginError", "ユーザー名またはパスワードが正しくありません");
    req.getRequestDispatcher("/index.jsp").forward(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.sendRedirect(req.getContextPath() + "/");
  }

  private static String nvl(String s) { return s==null? "" : s; }
  private static String nvl(String s, String d) { return s==null? d : s; }
  private static String trim(String s) { return s==null? "" : s.trim(); }

  private static void addRememberCookies(HttpServletResponse resp, String user, int groupId, String contextPath) {
    Cookie cu = new Cookie("REMEMBER_USER", user);
    cu.setHttpOnly(true);
    cu.setPath(contextPath.isEmpty()? "/" : contextPath);
    cu.setMaxAge(60*60*24*365); // 1 year
    resp.addCookie(cu);
    Cookie cg = new Cookie("REMEMBER_GID", String.valueOf(groupId));
    cg.setHttpOnly(true);
    cg.setPath(contextPath.isEmpty()? "/" : contextPath);
    cg.setMaxAge(60*60*24*365);
    resp.addCookie(cg);
  }
}
