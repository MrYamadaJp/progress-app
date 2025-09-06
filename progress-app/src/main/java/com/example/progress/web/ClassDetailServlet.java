package com.example.progress.web;

import com.example.progress.model.ClassEntry;
import com.example.progress.model.Memo;
import com.example.progress.repository.InMemoryStore;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/classes/*")
public class ClassDetailServlet extends HttpServlet {
  private static final String CTX_KEY = "STORE";
  private InMemoryStore store;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext ctx = config.getServletContext();
    Object s = ctx.getAttribute(CTX_KEY);
    if (s instanceof InMemoryStore) {
      this.store = (InMemoryStore) s;
    } else {
      this.store = new InMemoryStore();
      this.store.seed();
      ctx.setAttribute(CTX_KEY, this.store);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer id = parseId(req.getPathInfo());
    if (id == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
    ClassEntry clazz = store.findClass(id);
    if (clazz == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
    List<Memo> memos = store.listMemos(id);
    req.setAttribute("clazz", clazz);
    req.setAttribute("memos", memos);
    RequestDispatcher rd = req.getRequestDispatcher("/detail.jsp");
    rd.forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer id = parseId(req.getPathInfo());
    if (id == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
    String action = req.getParameter("action");
    if ("add_memo".equals(action)) {
      String type = param(req, "type", "note");
      String title = param(req, "title", "");
      String body = param(req, "body", "");
      String severity = param(req, "severity", null);
      String createdBy = param(req, "createdBy", "");
      if (!title.isEmpty()) {
        Memo m = new Memo(0, id, type, title, body, severity, createdBy, LocalDateTime.now(), "open");
        store.addMemo(id, m);
      }
      resp.sendRedirect(req.getRequestURI());
      return;
    }
    if ("close_memo".equals(action)) {
      try {
        int memoId = Integer.parseInt(req.getParameter("memoId"));
        store.closeMemo(id, memoId);
      } catch (Exception ignored) {}
      resp.sendRedirect(req.getRequestURI());
      return;
    }
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
  }

  private static Integer parseId(String pathInfo) {
    if (pathInfo == null || pathInfo.length() <= 1) return null;
    try { return Integer.parseInt(pathInfo.substring(1)); } catch (NumberFormatException e) { return null; }
  }

  private static String param(HttpServletRequest req, String name, String def) {
    String v = req.getParameter(name);
    return v != null ? v : def;
  }
}
