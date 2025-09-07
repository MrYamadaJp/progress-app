package com.example.progress.web;

import com.example.progress.model.ClassEntry;
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
import java.time.LocalDate;

@WebServlet(urlPatterns = {"/classes/new", "/classes/edit"})
public class ClassEditServlet extends HttpServlet {
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
    String path = req.getServletPath();
    if ("/classes/new".equals(path)) {
      req.setAttribute("mode", "create");
      forward(req, resp);
      return;
    }
    int id = parseInt(req.getParameter("id"), -1);
    if (id <= 0) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
    ClassEntry clazz = (store instanceof JdbcStore) ? ((JdbcStore) store).findClass(id) : ((InMemoryStore) store).findClass(id);
    if (clazz == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
    req.setAttribute("mode", "edit");
    req.setAttribute("clazz", clazz);
    forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    if ("/classes/new".equals(path)) {
      ClassEntry e = bindClass(req, 0);
      if (store instanceof JdbcStore) { ((JdbcStore) store).addClass(e); } else { ((InMemoryStore) store).addClass(e); }
      resp.sendRedirect(req.getContextPath() + "/classes");
      return;
    }
    int id = parseInt(req.getParameter("id"), -1);
    if (id <= 0) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
    ClassEntry e = bindClass(req, id);
    boolean ok;
    if (store instanceof JdbcStore) { ok = ((JdbcStore) store).updateClass(e); } else { ok = ((InMemoryStore) store).updateClass(e); }
    if (!ok) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
    resp.sendRedirect(req.getContextPath() + "/classes");
  }

  private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    RequestDispatcher rd = req.getRequestDispatcher("/class_form.jsp");
    rd.forward(req, resp);
  }

  private static Integer parseIdFromEdit(String pathInfo) {
    // expecting "/{id}/edit" -> pathInfo like "/123/edit"
    if (pathInfo == null || pathInfo.length() < 2) return null;
    String s = pathInfo;
    if (s.startsWith("/")) s = s.substring(1);
    if (s.endsWith("/edit")) s = s.substring(0, s.length() - 5);
    try { return Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
  }

  private static ClassEntry bindClass(HttpServletRequest req, int id) {
    String cls = nvl(req.getParameter("className"));
    String createdBy = nvl(req.getParameter("classCreated"));
    String status = nvl(req.getParameter("status"));
    return new ClassEntry(id, cls, createdBy, status);
  }

  private JdbcStore tryCreateJdbcStore() {
    try { InitialContext ic = new InitialContext(); DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/progress"); if (ds != null) return new JdbcStore(ds); } catch (NamingException ignored) {}
    return null;
  }

  private static String nvl(String s) { return s == null ? "" : s; }
  private static String nvl(String s, String d) { return s == null ? d : s; }
  private static int parseInt(String s, int d) { try { return Integer.parseInt(s); } catch (Exception e) { return d; } }
}
