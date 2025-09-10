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
// JNDI import不要（StoreProvider使用）
import java.io.IOException;
import java.util.List;

@WebServlet("/classes")
public class ClassListServlet extends HttpServlet {
  private Object store; // InMemoryStore or JdbcStore

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext ctx = config.getServletContext();
    this.store = StoreProvider.getStore(ctx);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String type = req.getParameter("type");
    if (type != null && type.trim().isEmpty()) type = null;
    String q = req.getParameter("q");
    if (q != null) q = q.trim();
    if (q != null && q.isEmpty()) q = null;
    List<ClassEntry> classes;
    if (store instanceof JdbcStore) {
      JdbcStore js = (JdbcStore) store;
      if (!js.isSchemaReady()) {
        req.setAttribute("warning", "データベースに必要なテーブル(classes, memos)が見つかりません。HeidiSQLでDDLを実行してください。");
        classes = java.util.Collections.emptyList();
      } else {
        try {
          Object gidObj = req.getSession(false) == null ? null : req.getSession(false).getAttribute("GROUP_ID");
          if (gidObj instanceof Integer && ((Integer) gidObj) > 0) {
            if (q != null) {
              classes = js.searchClassesByGroupSorted((Integer) gidObj, q);
            } else {
              classes = js.listClassesByGroupSorted((Integer) gidObj);
            }
          } else {
            classes = java.util.Collections.emptyList();
            req.setAttribute("warning", "このユーザーに紐づくグループが設定されていません");
          }
        } catch (com.example.progress.repository.SchemaMissingException ex) {
          req.setAttribute("warning", "データベースに必要なテーブル(classes, memos)が見つかりません。HeidiSQLでDDLを実行してください。");
          classes = java.util.Collections.emptyList();
        }
      }
    } else {
      classes = ((InMemoryStore) store).listClassesSorted();
    }
    // search by class name (in-memory for InMemoryStore or as post-filter)
    if (q != null) {
      String qlow = q.toLowerCase();
      java.util.List<ClassEntry> filtered = new java.util.ArrayList<>();
      for (ClassEntry c : classes) {
        String name = c.getClassName();
        if (name != null && name.toLowerCase().contains(qlow)) filtered.add(c);
      }
      classes = filtered;
    }
    // filter by classType if requested
    if (type != null && !"all".equals(type)) {
      java.util.List<ClassEntry> filtered = new java.util.ArrayList<>();
      for (ClassEntry c : classes) {
        if (type.equals(c.getClassType())) filtered.add(c);
      }
      classes = filtered;
    }
    // 並び順: ピン止めを最上位、その後は名前昇順
    classes.sort((a,b) -> {
      int p = Boolean.compare(b.isClassPinned(), a.isClassPinned());
      if (p != 0) return p;
      String an = a.getClassName()==null? "" : a.getClassName();
      String bn = b.getClassName()==null? "" : b.getClassName();
      return an.compareToIgnoreCase(bn);
    });

    req.setAttribute("classes", classes);
    RequestDispatcher rd = req.getRequestDispatcher("/list.jsp");
    rd.forward(req, resp);
  }

  // JNDIヘルパーはStoreProviderに集約
}
