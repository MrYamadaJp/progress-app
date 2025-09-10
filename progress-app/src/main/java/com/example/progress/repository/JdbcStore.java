package com.example.progress.repository;

import com.example.progress.model.ClassEntry;
import com.example.progress.model.Memo;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcStore {
  private final DataSource dataSource;

  public JdbcStore(DataSource dataSource) { this.dataSource = dataSource; }

  public boolean isSchemaReady() {
    try (Connection c = dataSource.getConnection()) {
      try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM classes LIMIT 1")) { ps.executeQuery(); }
      try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM memos LIMIT 1")) { ps.executeQuery(); }
      return true;
    } catch (SQLException e) { return false; }
  }

  // classes
  public List<ClassEntry> listClasses() {
    String sql = "SELECT class_id, class_name, class_created, status, class_type, class_pinned FROM classes";
    List<ClassEntry> list = new ArrayList<>();
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
      while (rs.next()) list.add(mapClass(rs));
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "SELECT class_id, class_name, class_created, status FROM classes";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old); ResultSet rs = ps.executeQuery()) {
          while (rs.next()) list.add(mapClass(rs));
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      } else if (isTableMissing(e)) { throw new SchemaMissingException(e); } else { throw new RuntimeException(e); }
    }
    return list;
  }

  public List<ClassEntry> listClassesSorted() {
    String sql = "SELECT class_id, class_name, class_created, status, class_type, class_pinned FROM classes ORDER BY class_name";
    List<ClassEntry> list = new ArrayList<>();
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
      while (rs.next()) list.add(mapClass(rs));
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "SELECT class_id, class_name, class_created, status FROM classes ORDER BY class_name";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old); ResultSet rs = ps.executeQuery()) {
          while (rs.next()) list.add(mapClass(rs));
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      } else if (isTableMissing(e)) { throw new SchemaMissingException(e); } else { throw new RuntimeException(e); }
    }
    return list;
  }

  // classes filtered by group
  public List<ClassEntry> listClassesByGroupSorted(int groupId) {
    String sql = "SELECT c.class_id, c.class_name, c.class_created, c.status, c.class_type, c.class_pinned " +
                 "FROM classes c JOIN group_class gc ON gc.class_id = c.class_id " +
                 "WHERE gc.group_id = ? ORDER BY c.class_name";
    List<ClassEntry> list = new ArrayList<>();
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, groupId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapClass(rs));
      }
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "SELECT c.class_id, c.class_name, c.class_created, c.status " +
                     "FROM classes c JOIN group_class gc ON gc.class_id = c.class_id " +
                     "WHERE gc.group_id = ? ORDER BY c.class_name";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old)) {
          ps.setInt(1, groupId);
          try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapClass(rs));
          }
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      } else if (isTableMissing(e)) { throw new SchemaMissingException(e); } else { throw new RuntimeException(e); }
    }
    return list;
  }

  // search by class name within a group (case-insensitive LIKE)
  public List<ClassEntry> searchClassesByGroupSorted(int groupId, String keyword) {
    String sql = "SELECT c.class_id, c.class_name, c.class_created, c.status, c.class_type, c.class_pinned " +
                 "FROM classes c JOIN group_class gc ON gc.class_id = c.class_id " +
                 "WHERE gc.group_id = ? AND LOWER(c.class_name) LIKE ? ORDER BY c.class_name";
    List<ClassEntry> list = new ArrayList<>();
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, groupId);
      ps.setString(2, "%" + keyword.toLowerCase() + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapClass(rs));
      }
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "SELECT c.class_id, c.class_name, c.class_created, c.status " +
                     "FROM classes c JOIN group_class gc ON gc.class_id = c.class_id " +
                     "WHERE gc.group_id = ? AND LOWER(c.class_name) LIKE ? ORDER BY c.class_name";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old)) {
          ps.setInt(1, groupId);
          ps.setString(2, "%" + keyword.toLowerCase() + "%");
          try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapClass(rs));
          }
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      } else if (isTableMissing(e)) { throw new SchemaMissingException(e); } else { throw new RuntimeException(e); }
    }
    return list;
  }

  public ClassEntry findClass(int id) {
    String sql = "SELECT class_id, class_name, class_created, status, class_type, class_pinned FROM classes WHERE class_id=?";
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapClass(rs); return null; }
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "SELECT class_id, class_name, class_created, status FROM classes WHERE class_id=?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old)) {
          ps.setInt(1, id);
          try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapClass(rs); return null; }
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      }
      if (isTableMissing(e)) throw new SchemaMissingException(e); throw new RuntimeException(e);
    }
  }

  public ClassEntry addClass(ClassEntry entry) {
    String sql = "INSERT INTO classes(class_name, class_created, status, class_type, class_pinned) VALUES(?,?,?,?,?)";
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, entry.getClassName());
      ps.setString(2, entry.getClassCreated());
      ps.setString(3, entry.getStatus());
      ps.setString(4, entry.getClassType());
      ps.setBoolean(5, entry.isClassPinned());
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) entry.setId(keys.getInt(1)); }
      return entry;
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "INSERT INTO classes(class_name, class_created, status) VALUES(?,?,?)";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old, Statement.RETURN_GENERATED_KEYS)) {
          ps.setString(1, entry.getClassName());
          ps.setString(2, entry.getClassCreated());
          ps.setString(3, entry.getStatus());
          ps.executeUpdate();
          try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) entry.setId(keys.getInt(1)); }
          return entry;
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      }
      if (isTableMissing(e)) throw new SchemaMissingException(e); throw new RuntimeException(e);
    }
  }

  public ClassEntry addClassForGroup(ClassEntry entry, int groupId) {
    ClassEntry e = addClass(entry);
    String link = "INSERT INTO group_class(group_id, class_id) VALUES(?,?)";
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(link)) {
      ps.setInt(1, groupId);
      ps.setInt(2, e.getId());
      ps.executeUpdate();
      return e;
    } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
  }

  public boolean updateClass(ClassEntry entry) {
    String sql = "UPDATE classes SET class_name=?, class_created=?, status=?, class_type=?, class_pinned=? WHERE class_id=?";
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, entry.getClassName());
      ps.setString(2, entry.getClassCreated());
      ps.setString(3, entry.getStatus());
      ps.setString(4, entry.getClassType());
      ps.setBoolean(5, entry.isClassPinned());
      ps.setInt(6, entry.getId());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "UPDATE classes SET class_name=?, class_created=?, status=? WHERE class_id=?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old)) {
          ps.setString(1, entry.getClassName());
          ps.setString(2, entry.getClassCreated());
          ps.setString(3, entry.getStatus());
          ps.setInt(4, entry.getId());
          return ps.executeUpdate() > 0;
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      }
      if (isTableMissing(e)) throw new SchemaMissingException(e); throw new RuntimeException(e);
    }
  }

  public boolean deleteClass(int id) {
    String sql = "DELETE FROM classes WHERE class_id=?";
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) { if (isTableMissing(e)) throw new SchemaMissingException(e); throw new RuntimeException(e); }
  }

  // memos (independent)
  public List<Memo> listMemos(int classId) {
    String sql = "SELECT m.memo_id, m.memo_type, m.memo_title, m.memo_created, m.memo_detail, " +
                 "m.memo_updated, m.memo_tags, m.memo_pinned " +
                 "FROM memos m JOIN class_memo cm ON cm.memo_id = m.memo_id " +
                 "WHERE cm.class_id = ? " +
                 "ORDER BY m.memo_pinned DESC, COALESCE(m.memo_updated, m.memo_created) DESC, m.memo_id DESC";
    List<Memo> list = new ArrayList<>();
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, classId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapMemo(rs, true, true));
        return list;
      }
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        // fallback for older schema w/o new columns
        String old = "SELECT m.memo_id, m.memo_type, m.memo_title, m.memo_created " +
                     "FROM memos m JOIN class_memo cm ON cm.memo_id = m.memo_id WHERE cm.class_id = ? ORDER BY m.memo_id DESC";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old)) {
          ps.setInt(1, classId);
          try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapMemo(rs, false)); return list; }
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      }
      if (isTableMissing(e)) {
        // If class_memo is missing, fallback to all memos to avoid hard break
        String all = "SELECT memo_id, memo_type, memo_title, memo_created FROM memos ORDER BY memo_id";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(all); ResultSet rs = ps.executeQuery()) {
          while (rs.next()) list.add(mapMemo(rs, false));
          return list;
        } catch (SQLException ex) { throw new RuntimeException(ex); }
      }
      throw new RuntimeException(e);
    }
  }

  public Memo addMemo(int classId, Memo memo) {
    String sql = "INSERT INTO memos(memo_type, memo_title, memo_created, memo_detail, memo_updated, memo_tags, memo_pinned) VALUES(?,?,?,?,?,?,?)";
    try (Connection c = dataSource.getConnection()) {
      PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, memo.getMemoType());
      ps.setString(2, memo.getMemoTitle());
      ps.setString(3, memo.getMemoCreated());
      ps.setString(4, memo.getMemoDetail());
      ps.setString(5, memo.getMemoUpdated());
      ps.setString(6, memo.getMemoTags());
      ps.setBoolean(7, memo.isMemoPinned());
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) memo.setId(keys.getInt(1)); }
      // link to class
      if (classId > 0) {
        try (PreparedStatement link = c.prepareStatement("INSERT INTO class_memo(class_id, memo_id) VALUES(?,?)")) {
          link.setInt(1, classId);
          link.setInt(2, memo.getId());
          link.executeUpdate();
        }
      }
      ps.close();
      return memo;
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "INSERT INTO memos(memo_type, memo_title, memo_created) VALUES(?,?,?)";
        try (Connection c = dataSource.getConnection()) {
          PreparedStatement ps = c.prepareStatement(old, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, memo.getMemoType());
          ps.setString(2, memo.getMemoTitle());
          ps.setString(3, memo.getMemoCreated());
          ps.executeUpdate();
          try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) memo.setId(keys.getInt(1)); }
          if (classId > 0) {
            try (PreparedStatement link = c.prepareStatement("INSERT INTO class_memo(class_id, memo_id) VALUES(?,?)")) {
              link.setInt(1, classId);
              link.setInt(2, memo.getId());
              link.executeUpdate();
            }
          }
          return memo;
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      }
      if (isTableMissing(e)) throw new SchemaMissingException(e); throw new RuntimeException(e);
    }
  }

  public boolean updateMemo(int unused, Memo memo) {
    String sql = "UPDATE memos SET memo_type=?, memo_title=?, memo_created=?, memo_detail=?, memo_updated=?, memo_tags=?, memo_pinned=? WHERE memo_id=?";
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, memo.getMemoType());
      ps.setString(2, memo.getMemoTitle());
      ps.setString(3, memo.getMemoCreated());
      ps.setString(4, memo.getMemoDetail());
      ps.setString(5, memo.getMemoUpdated());
      ps.setString(6, memo.getMemoTags());
      ps.setBoolean(7, memo.isMemoPinned());
      ps.setInt(8, memo.getId());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "UPDATE memos SET memo_type=?, memo_title=?, memo_created=?, memo_detail=? WHERE memo_id=?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old)) {
          ps.setString(1, memo.getMemoType());
          ps.setString(2, memo.getMemoTitle());
          ps.setString(3, memo.getMemoCreated());
          ps.setString(4, memo.getMemoDetail());
          ps.setInt(5, memo.getId());
          return ps.executeUpdate() > 0;
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      }
      if (isTableMissing(e)) throw new SchemaMissingException(e); throw new RuntimeException(e);
    }
  }

  public boolean deleteMemo(int unused, int memoId) {
    String sql1 = "DELETE FROM class_memo WHERE memo_id=?";
    String sql2 = "DELETE FROM memos WHERE memo_id=?";
    try (Connection c = dataSource.getConnection()) {
      try (PreparedStatement ps1 = c.prepareStatement(sql1)) { ps1.setInt(1, memoId); ps1.executeUpdate(); }
      try (PreparedStatement ps2 = c.prepareStatement(sql2)) { ps2.setInt(1, memoId); return ps2.executeUpdate() > 0; }
    } catch (SQLException e) { if (isTableMissing(e)) throw new SchemaMissingException(e); throw new RuntimeException(e); }
  }

  public Memo findMemo(int memoId) {
    String sql = "SELECT memo_id, memo_type, memo_title, memo_created, memo_detail, memo_updated, memo_tags, memo_pinned FROM memos WHERE memo_id=?";
    try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, memoId);
      try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapMemo(rs, true, true); return null; }
    } catch (SQLException e) {
      if (isColumnMissing(e)) {
        String old = "SELECT memo_id, memo_type, memo_title, memo_created FROM memos WHERE memo_id=?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(old)) {
          ps.setInt(1, memoId);
          try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapMemo(rs, false); return null; }
        } catch (SQLException ex) { if (isTableMissing(ex)) throw new SchemaMissingException(ex); throw new RuntimeException(ex); }
      }
      if (isTableMissing(e)) throw new SchemaMissingException(e); throw new RuntimeException(e);
    }
  }

  public boolean closeMemo(int classId, int memoId) { return false; }
  public long countOpenBugs(int classId) { return 0L; }

  private static ClassEntry mapClass(ResultSet rs) throws SQLException {
    int id = rs.getInt("class_id");
    String cls = rs.getString("class_name");
    String created = rs.getString("class_created");
    String status = rs.getString("status");
    String type = null;
    try { type = rs.getString("class_type"); } catch (SQLException ignored) {}
    boolean pinned = false;
    try { pinned = rs.getBoolean("class_pinned"); } catch (SQLException ignored) {}
    return new ClassEntry(id, cls, created, status, type, pinned);
  }

  private static Memo mapMemo(ResultSet rs, boolean hasDetail) throws SQLException {
    int id = rs.getInt("memo_id");
    String type = rs.getString("memo_type");
    String title = rs.getString("memo_title");
    String created = rs.getString("memo_created");
    String detail = hasDetail ? rs.getString("memo_detail") : null;
    return new Memo(id, type, title, created, detail);
  }

  private static Memo mapMemo(ResultSet rs, boolean hasDetail, boolean hasExtras) throws SQLException {
    if (!hasExtras) return mapMemo(rs, hasDetail);
    int id = rs.getInt("memo_id");
    String type = rs.getString("memo_type");
    String title = rs.getString("memo_title");
    String created = rs.getString("memo_created");
    String detail = hasDetail ? rs.getString("memo_detail") : null;
    String updated = null;
    String tags = null;
    boolean pinned = false;
    try { updated = rs.getString("memo_updated"); } catch (SQLException ignored) {}
    try { tags = rs.getString("memo_tags"); } catch (SQLException ignored) {}
    try { pinned = rs.getBoolean("memo_pinned"); } catch (SQLException ignored) {}
    return new Memo(id, type, title, created, detail, updated, tags, pinned);
  }

  private static boolean isTableMissing(SQLException e) {
    if ("42S02".equals(e.getSQLState())) return true;
    if (e.getErrorCode() == 1146) return true;
    Throwable t = e.getCause();
    while (t instanceof SQLException) {
      SQLException se = (SQLException) t;
      if ("42S02".equals(se.getSQLState()) || se.getErrorCode() == 1146) return true;
      t = se.getCause();
    }
    return false;
  }

  private static boolean isColumnMissing(SQLException e) {
    // Unknown column: SQLSTATE 42S22, MySQL error code 1054
    if ("42S22".equals(e.getSQLState())) return true;
    if (e.getErrorCode() == 1054) return true;
    Throwable t = e.getCause();
    while (t instanceof SQLException) {
      SQLException se = (SQLException) t;
      if ("42S22".equals(se.getSQLState()) || se.getErrorCode() == 1054) return true;
      t = se.getCause();
    }
    return false;
  }
}
