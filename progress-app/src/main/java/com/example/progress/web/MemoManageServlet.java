package com.example.progress.web;

import com.example.progress.model.Memo;
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
import java.time.LocalDateTime;

@WebServlet(urlPatterns = {"/memo/new", "/memo/edit", "/memo/delete"})
public class MemoManageServlet extends HttpServlet {
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
    String servletPath = req.getServletPath();
    Integer memoId = parseInt(req.getParameter("memoId"));
    // keep back link
    if (req.getParameter("back") != null) req.setAttribute("back", req.getParameter("back"));
    if ("/memo/new".equals(servletPath)) {
      req.setAttribute("mode", "create");
      forward(req, resp, "/memo_form.jsp");
      return;
    }
    if ("/memo/edit".equals(servletPath)) {
      if (memoId == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
      req.setAttribute("mode", "edit");
      req.setAttribute("memoId", memoId);
      if (store instanceof JdbcStore) {
        req.setAttribute("memo", ((JdbcStore) store).findMemo(memoId));
      } else {
        req.setAttribute("memo", ((InMemoryStore) store).findMemo(memoId));
      }
      forward(req, resp, "/memo_form.jsp");
      return;
    }
    if ("/memo/delete".equals(servletPath)) {
      if (memoId == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
      req.setAttribute("memoId", memoId);
      forward(req, resp, "/memo_delete.jsp");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String servletPath = req.getServletPath();
    Integer memoId = parseInt(req.getParameter("memoId"));
    String backParam = req.getParameter("back");
    String referer = req.getHeader("Referer");
    String fallback = req.getContextPath() + "/classes";
    String back = (backParam != null && !backParam.isEmpty()) ? backParam : (referer != null && !referer.isEmpty() ? referer : fallback);
    if (back.startsWith("/")) { back = req.getContextPath() + back; }
    if ("/memo/new".equals(servletPath)) {
      int classId = parseInt(req.getParameter("classId"));
      String type = nvl(req.getParameter("memoType"));
      String title = nvl(req.getParameter("memoTitle"));
      String created = nvl(req.getParameter("memoCreated"));
      String detail = nvl(req.getParameter("memoDetail"));
      if (!title.isEmpty()) {
        Memo m = new Memo(0, type, title, created, detail);
        if (store instanceof JdbcStore) { ((JdbcStore) store).addMemo(classId, m); } else { ((InMemoryStore) store).addMemo(classId, m); }
      }
      resp.sendRedirect(back);
      return;
    }
    if ("/memo/edit".equals(servletPath)) {
      if (memoId == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
      String type = nvl(req.getParameter("memoType"));
      String title = nvl(req.getParameter("memoTitle"));
      String created = nvl(req.getParameter("memoCreated"));
      String detail = nvl(req.getParameter("memoDetail"));
      Memo m = new Memo(memoId, type, title, created, detail);
      if (store instanceof JdbcStore) { ((JdbcStore) store).updateMemo(0, m); } else { ((InMemoryStore) store).updateMemo(0, m); }
      resp.sendRedirect(back);
      return;
    }
    if ("/memo/delete".equals(servletPath)) {
      int classId = parseInt(req.getParameter("classId"));
      if (memoId == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
      if (store instanceof JdbcStore) { ((JdbcStore) store).deleteMemo(classId, memoId); } else { ((InMemoryStore) store).deleteMemo(classId, memoId); }
      resp.sendRedirect(back);
    }
  }

  private void forward(HttpServletRequest req, HttpServletResponse resp, String jsp) throws ServletException, IOException {
    RequestDispatcher rd = req.getRequestDispatcher(jsp);
    rd.forward(req, resp);
  }

  private JdbcStore tryCreateJdbcStore() {
    try { InitialContext ic = new InitialContext(); DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/progress"); if (ds != null) return new JdbcStore(ds); } catch (NamingException ignored) {}
    return null;
  }

  private static Integer parseInt(String v) { try { return v==null? null : Integer.parseInt(v); } catch(Exception e){ return null; } }
  private static String nvl(String s) { return s==null? "" : s; }
  private static String nvl(String s, String d) { return s==null? d : s; }
  private static String emptyToNull(String s){ return (s==null||s.trim().isEmpty())? null : s; }
}
