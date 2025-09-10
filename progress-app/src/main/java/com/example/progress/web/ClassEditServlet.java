package com.example.progress.web;

import com.example.progress.model.ClassEntry;
import com.example.progress.repository.InMemoryStore;
import com.example.progress.repository.JdbcStore;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/classes/new", "/classes/edit"})
public class ClassEditServlet extends HttpServlet {
  private Object store; // InMemoryStore or JdbcStore

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext ctx = config.getServletContext();
    this.store = StoreProvider.getStore(ctx);
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
      String warn = null;
      try {
        if (store instanceof JdbcStore) {
          Object gidObj = req.getSession(false) == null ? null : req.getSession(false).getAttribute("GROUP_ID");
          if (gidObj instanceof Integer && ((Integer) gidObj) > 0) {
            ((JdbcStore) store).addClassForGroup(e, (Integer) gidObj);
          } else {
            warn = "このユーザーに紐づくグループが設定されていません";
          }
        } else {
          ((InMemoryStore) store).addClass(e);
        }
      } catch (Exception ex) {
        warn = "クラス作成中にエラーが発生しました";
      }
      String url = req.getContextPath() + "/classes" + (warn != null ? ("?warning=" + java.net.URLEncoder.encode(warn, java.nio.charset.StandardCharsets.UTF_8)) : "");
      resp.sendRedirect(url);
      return;
    }
    int id = parseInt(req.getParameter("id"), -1);
    if (id <= 0) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
    ClassEntry e = bindClass(req, id);
    boolean ok;
    try {
      if (store instanceof JdbcStore) { ok = ((JdbcStore) store).updateClass(e); } else { ok = ((InMemoryStore) store).updateClass(e); }
    } catch (Exception ex) {
      ok = false;
    }
    if (!ok) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
    resp.sendRedirect(req.getContextPath() + "/classes");
  }

  private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    RequestDispatcher rd = req.getRequestDispatcher("/class_form.jsp");
    rd.forward(req, resp);
  }

  private static ClassEntry bindClass(HttpServletRequest req, int id) {
    String cls = nvl(req.getParameter("className"));
    String createdBy = nvl(req.getParameter("classCreated"));
    String status = nvl(req.getParameter("status"));
    String classType = nvl(req.getParameter("classType"));
    if (classType.isEmpty()) classType = "Beans"; // デフォルト
    boolean pinned = "on".equalsIgnoreCase(nvl(req.getParameter("classPinned"))) ||
                     "true".equalsIgnoreCase(nvl(req.getParameter("classPinned")));
    return new ClassEntry(id, cls, createdBy, status, classType, pinned);
  }

  private static String nvl(String s) { return s == null ? "" : s; }
  private static int parseInt(String s, int d) { try { return Integer.parseInt(s); } catch (Exception e) { return d; } }
}
