package com.example.progress.web;

import com.example.progress.repository.InMemoryStore;
import com.example.progress.repository.JdbcStore;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

public final class StoreProvider {
  private static final String CTX_KEY = "STORE";

  private StoreProvider() {}

  public static Object getStore(ServletContext ctx) {
    Object s = ctx.getAttribute(CTX_KEY);
    if (s != null) return s;
    JdbcStore jdbc = tryCreateJdbcStore();
    if (jdbc != null) { ctx.setAttribute(CTX_KEY, jdbc); return jdbc; }
    InMemoryStore mem = new InMemoryStore();
    mem.seed();
    ctx.setAttribute(CTX_KEY, mem);
    return mem;
  }

  private static JdbcStore tryCreateJdbcStore() {
    try {
      InitialContext ic = new InitialContext();
      DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/progress");
      if (ds != null) return new JdbcStore(ds);
    } catch (NamingException ignored) {}
    return null;
  }
}

