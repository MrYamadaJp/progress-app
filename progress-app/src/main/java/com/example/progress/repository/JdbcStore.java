package com.example.progress.repository;

import com.example.progress.model.ClassEntry;
import com.example.progress.model.Memo;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JdbcStore {
  private final DataSource dataSource;

  public JdbcStore(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public boolean isSchemaReady() {
    try (Connection c = dataSource.getConnection()) {
      try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM classes LIMIT 1")) { ps.executeQuery(); }
      try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM memos LIMIT 1")) { ps.executeQuery(); }
      return true;
    } catch (SQLException e) {
      if (isTableMissing(e)) return false;
      return false;
    }
  }

  public List<ClassEntry> listClasses() {
    String sql = "SELECT id, package_name, class_name, author, status, progress, last_updated FROM classes";
    List<ClassEntry> list = new ArrayList<>();
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(mapClass(rs));
      }
    } catch (SQLException e) {
      if (isTableMissing(e)) throw new SchemaMissingException(e);
      throw new RuntimeException(e);
    }
    return list;
  }

  public List<ClassEntry> listClassesSorted() {
    String sql = "SELECT id, package_name, class_name, author, status, progress, last_updated " +
                 "FROM classes ORDER BY package_name, class_name";
    List<ClassEntry> list = new ArrayList<>();
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(mapClass(rs));
      }
    } catch (SQLException e) {
      if (isTableMissing(e)) throw new SchemaMissingException(e);
      throw new RuntimeException(e);
    }
    return list;
  }

  public ClassEntry findClass(int id) {
    String sql = "SELECT id, package_name, class_name, author, status, progress, last_updated FROM classes WHERE id=?";
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return mapClass(rs);
        return null;
      }
    } catch (SQLException e) {
      if (isTableMissing(e)) throw new SchemaMissingException(e);
      throw new RuntimeException(e);
    }
  }

  public ClassEntry addClass(ClassEntry entry) {
    String sql = "INSERT INTO classes(package_name, class_name, author, status, progress, last_updated) " +
                 "VALUES(?,?,?,?,?,?)";
    LocalDate last = entry.getLastUpdated() != null ? entry.getLastUpdated() : LocalDate.now();
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, entry.getPackageName());
      ps.setString(2, entry.getClassName());
      ps.setString(3, entry.getAuthor());
      ps.setString(4, entry.getStatus());
      ps.setInt(5, entry.getProgress());
      ps.setDate(6, Date.valueOf(last));
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) entry.setId(keys.getInt(1));
      }
      entry.setLastUpdated(last);
      return entry;
    } catch (SQLException e) {
      if (isTableMissing(e)) throw new SchemaMissingException(e);
      throw new RuntimeException(e);
    }
  }

  public List<Memo> listMemos(int classId) {
    String sql = "SELECT id, class_id, type, title, body, severity, created_by, created_at, status " +
                 "FROM memos WHERE class_id=? ORDER BY id";
    List<Memo> list = new ArrayList<>();
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, classId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapMemo(rs));
      }
    } catch (SQLException e) {
      if (isTableMissing(e)) throw new SchemaMissingException(e);
      throw new RuntimeException(e);
    }
    return list;
  }

  public Memo addMemo(int classId, Memo memo) {
    String sql = "INSERT INTO memos(class_id, type, title, body, severity, created_by, created_at, status) " +
                 "VALUES(?,?,?,?,?,?,?,?)";
    LocalDateTime ts = memo.getCreatedAt() != null ? memo.getCreatedAt() : LocalDateTime.now();
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, classId);
      ps.setString(2, memo.getType());
      ps.setString(3, memo.getTitle());
      ps.setString(4, memo.getBody());
      String sev = memo.getSeverity();
      if (sev == null || sev.trim().isEmpty()) {
        ps.setNull(5, Types.VARCHAR);
      } else {
        ps.setString(5, sev);
      }
      ps.setString(6, memo.getCreatedBy());
      ps.setTimestamp(7, Timestamp.valueOf(ts));
      ps.setString(8, memo.getStatus());
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) memo.setId(keys.getInt(1));
      }
      memo.setClassId(classId);
      memo.setCreatedAt(ts);
      return memo;
    } catch (SQLException e) {
      if (isTableMissing(e)) throw new SchemaMissingException(e);
      throw new RuntimeException(e);
    }
  }

  public boolean closeMemo(int classId, int memoId) {
    String sql = "UPDATE memos SET status='closed' WHERE class_id=? AND id=?";
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, classId);
      ps.setInt(2, memoId);
      int updated = ps.executeUpdate();
      return updated > 0;
    } catch (SQLException e) {
      if (isTableMissing(e)) throw new SchemaMissingException(e);
      throw new RuntimeException(e);
    }
  }

  public long countOpenBugs(int classId) {
    String sql = "SELECT COUNT(*) FROM memos WHERE class_id=? AND type='bug' AND status='open'";
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, classId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getLong(1);
        return 0L;
      }
    } catch (SQLException e) { throw new RuntimeException(e); }
  }

  private static ClassEntry mapClass(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String pkg = rs.getString("package_name");
    String cls = rs.getString("class_name");
    String author = rs.getString("author");
    String status = rs.getString("status");
    int progress = rs.getInt("progress");
    Date d = rs.getDate("last_updated");
    LocalDate last = d != null ? d.toLocalDate() : LocalDate.now();
    return new ClassEntry(id, pkg, cls, author, status, progress, last);
  }

  private static Memo mapMemo(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    int classId = rs.getInt("class_id");
    String type = rs.getString("type");
    String title = rs.getString("title");
    String body = rs.getString("body");
    String severity = rs.getString("severity");
    String createdBy = rs.getString("created_by");
    Timestamp ts = rs.getTimestamp("created_at");
    LocalDateTime at = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
    String status = rs.getString("status");
    return new Memo(id, classId, type, title, body, severity, createdBy, at, status);
  }

  private static boolean isTableMissing(SQLException e) {
    // MySQL/MariaDB: errorCode 1146, SQLState 42S02
    if ("42S02".equals(e.getSQLState())) return true;
    if (e.getErrorCode() == 1146) return true;
    // Unwrap causes if any
    Throwable t = e.getCause();
    while (t instanceof SQLException) {
      SQLException se = (SQLException) t;
      if ("42S02".equals(se.getSQLState()) || se.getErrorCode() == 1146) return true;
      t = se.getCause();
    }
    return false;
  }
}
