package com.example.progress.model;

public class Memo {
  private int memoId;
  private String memoType;     // TEXT
  private String memoTitle;
  private String memoCreated;  // 作成者など
  private String memoDetail;   // 本文詳細（任意）

  public Memo(int memoId, String memoType, String memoTitle, String memoCreated) {
    this(memoId, memoType, memoTitle, memoCreated, null);
  }

  public Memo(int memoId, String memoType, String memoTitle, String memoCreated, String memoDetail) {
    this.memoId = memoId;
    this.memoType = memoType;
    this.memoTitle = memoTitle;
    this.memoCreated = memoCreated;
    this.memoDetail = memoDetail;
  }

  public int getId() { return memoId; }
  public int getMemoId() { return memoId; }
  public String getMemoType() { return memoType; }
  public String getMemoTitle() { return memoTitle; }
  public String getMemoCreated() { return memoCreated; }
  public String getMemoDetail() { return memoDetail; }

  public void setId(int memoId) { this.memoId = memoId; }
  public void setMemoId(int memoId) { this.memoId = memoId; }
  public void setMemoType(String memoType) { this.memoType = memoType; }
  public void setMemoTitle(String memoTitle) { this.memoTitle = memoTitle; }
  public void setMemoCreated(String memoCreated) { this.memoCreated = memoCreated; }
  public void setMemoDetail(String memoDetail) { this.memoDetail = memoDetail; }
}

