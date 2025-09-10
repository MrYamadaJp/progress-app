package com.example.progress.web;

import com.example.progress.repository.InMemoryStore;
import com.example.progress.repository.JdbcStore;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet(urlPatterns = "/class/delete")
public class ClassDeleteServlet extends HttpServlet {
  private static final String CTX_KEY = "STORE";
  private Object store; // InMemoryStore or JdbcStore

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext ctx = config.getServletContext();
    Object s = ctx.getAttribute(CTX_KEY);
    if (s != null) { this.store = s; return; }
    JdbcStore jdbc = tryCreateJdbcStore();
    if (jdbc != null) { this.store = jdbc; ctx.setAttribute(CTX_KEY, this.store); return; }
    InMemoryStore mem = new InMemoryStore(); mem.seed(); this.store = mem; ctx.setAttribute(CTX_KEY, this.store);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer id = parseInt(req.getParameter("id"));
    if (id == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
    req.setAttribute("id", id);
    RequestDispatcher rd = req.getRequestDispatcher("/class_delete.jsp");
    rd.forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer id = parseInt(req.getParameter("id"));
    if (id == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
    if (store instanceof JdbcStore) { ((JdbcStore) store).deleteClass(id); } else { ((InMemoryStore) store).deleteClass(id); }
    resp.sendRedirect(req.getContextPath() + "/classes");
  }

  private static Integer parseInt(String v) { try { return v==null? null : Integer.parseInt(v); } catch(Exception e){ return null; } }

  private JdbcStore tryCreateJdbcStore() {
    try { InitialContext ic = new InitialContext(); DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/progress"); if (ds != null) return new JdbcStore(ds); } catch (NamingException ignored) {}
    return null;
  }
}

