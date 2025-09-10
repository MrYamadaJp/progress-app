package com.example.progress.model;

public class ClassEntry {
  private int classId;
  private String className;
  private String classCreated; // 作成者/created_by 相当
  private String status;       // TEXT
  private String classType;    // 追加: クラス種類 (JSP/サーブレット/Beans)
  private boolean classPinned; // 追加: ピン止め（最上段表示）

  public ClassEntry(int classId, String className, String classCreated, String status) {
    this.classId = classId;
    this.className = className;
    this.classCreated = classCreated;
    this.status = status;
    this.classType = null;
  }

  public ClassEntry(int classId, String className, String classCreated, String status, String classType) {
    this.classId = classId;
    this.className = className;
    this.classCreated = classCreated;
    this.status = status;
    this.classType = classType;
    this.classPinned = false;
  }

  public ClassEntry(int classId, String className, String classCreated, String status, String classType, boolean classPinned) {
    this.classId = classId;
    this.className = className;
    this.classCreated = classCreated;
    this.status = status;
    this.classType = classType;
    this.classPinned = classPinned;
  }

  public int getId() { return classId; }
  public int getClassId() { return classId; }
  public String getClassName() { return className; }
  public String getClassCreated() { return classCreated; }
  public String getStatus() { return status; }
  public String getClassType() { return classType; }
  public boolean isClassPinned() { return classPinned; }

  public void setId(int classId) { this.classId = classId; }
  public void setClassId(int classId) { this.classId = classId; }
  public void setClassName(String className) { this.className = className; }
  public void setClassCreated(String classCreated) { this.classCreated = classCreated; }
  public void setStatus(String status) { this.status = status; }
  public void setClassType(String classType) { this.classType = classType; }
  public void setClassPinned(boolean classPinned) { this.classPinned = classPinned; }
}
