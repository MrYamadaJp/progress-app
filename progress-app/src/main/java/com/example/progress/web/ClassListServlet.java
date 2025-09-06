package com.example.progress.web;

import com.example.progress.model.ClassEntry;
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
import java.util.List;

@WebServlet("/classes")
public class ClassListServlet extends HttpServlet {
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
    List<ClassEntry> classes = store.listClassesSorted();
    req.setAttribute("classes", classes);
    RequestDispatcher rd = req.getRequestDispatcher("/list.jsp");
    rd.forward(req, resp);
  }
}
