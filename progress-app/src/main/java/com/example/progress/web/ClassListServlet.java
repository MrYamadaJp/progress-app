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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/classes")
public class ClassListServlet extends HttpServlet {
  private static final String CTX_KEY = "STORE";

  private Object store; // InMemoryStore or JdbcStore

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext ctx = config.getServletContext();
    Object s = ctx.getAttribute(CTX_KEY);
    if (s != null) {
      this.store = s;
      return;
    }
    // Try JNDI DataSource first
    JdbcStore jdbc = tryCreateJdbcStore();
    if (jdbc != null) {
      this.store = jdbc;
      ctx.setAttribute(CTX_KEY, this.store);
      return;
    }
    // Fallback to in-memory store with seed
    InMemoryStore mem = new InMemoryStore();
    mem.seed();
    this.store = mem;
    ctx.setAttribute(CTX_KEY, this.store);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    List<ClassEntry> classes;
    if (store instanceof JdbcStore) {
      JdbcStore js = (JdbcStore) store;
      if (!js.isSchemaReady()) {
        req.setAttribute("warning", "データベースに必要なテーブル(classes, memos)が見つかりません。HeidiSQLでDDLを実行してください。");
        classes = java.util.Collections.emptyList();
      } else {
        try {
          classes = js.listClassesSorted();
        } catch (com.example.progress.repository.SchemaMissingException ex) {
          req.setAttribute("warning", "データベースに必要なテーブル(classes, memos)が見つかりません。HeidiSQLでDDLを実行してください。");
          classes = java.util.Collections.emptyList();
        }
      }
    } else {
      classes = ((InMemoryStore) store).listClassesSorted();
    }
    req.setAttribute("classes", classes);
    RequestDispatcher rd = req.getRequestDispatcher("/list.jsp");
    rd.forward(req, resp);
  }

  private JdbcStore tryCreateJdbcStore() {
    try {
      InitialContext ic = new InitialContext();
      DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/progress");
      if (ds != null) return new JdbcStore(ds);
    } catch (NamingException ignored) {}
    return null;
  }
}
