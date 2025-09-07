package com.example.progress.web;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/healthz")
public class HealthServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/plain; charset=UTF-8");
    try (PrintWriter out = resp.getWriter()) {
      // try DB if available
      boolean dbOk = false;
      try {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/progress");
        if (ds != null) {
          try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT 1"); ResultSet rs = ps.executeQuery()) {
            dbOk = rs.next();
          }
        }
      } catch (NamingException | RuntimeException e) {
        dbOk = false;
      } catch (Exception e) {
        dbOk = false;
      }
      if (dbOk) {
        out.println("ok");
      } else {
        // still return 200 to allow container health checks when DB is optional
        out.println("ok (db: unavailable)");
      }
    }
  }
}

