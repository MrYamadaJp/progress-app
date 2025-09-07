package com.example.progress.repository;

import com.example.progress.model.ClassEntry;
import com.example.progress.model.Memo;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryStore {
  private final Map<Integer, ClassEntry> classes = new LinkedHashMap<>();
  private final List<Memo> memos = new ArrayList<>(); // independent memos
  private final AtomicInteger classSeq = new AtomicInteger(1);
  private final AtomicInteger memoSeq = new AtomicInteger(1);
  private final Map<Integer, List<Integer>> classToMemoIds = new HashMap<>();

  public synchronized List<ClassEntry> listClasses() { return new ArrayList<>(classes.values()); }

  public synchronized List<ClassEntry> listClassesSorted() {
    List<ClassEntry> list = listClasses();
    list.sort(Comparator.comparing(ClassEntry::getClassName));
    return list;
  }

  public synchronized ClassEntry findClass(int id) { return classes.get(id); }

  public synchronized ClassEntry addClass(ClassEntry entry) {
    if (entry.getId() <= 0) entry.setId(classSeq.getAndIncrement());
    classes.put(entry.getId(), entry);
    return entry;
  }

  public synchronized boolean updateClass(ClassEntry entry) {
    if (!classes.containsKey(entry.getId())) return false;
    classes.put(entry.getId(), entry);
    return true;
  }

  public synchronized boolean deleteClass(int id) { return classes.remove(id) != null; }

  public synchronized List<Memo> listMemos(int classId) {
    if (classId <= 0) return new ArrayList<>(memos);
    List<Integer> ids = classToMemoIds.getOrDefault(classId, Collections.emptyList());
    List<Memo> list = new ArrayList<>();
    for (Integer id : ids) {
      for (Memo m : memos) if (m.getId() == id) { list.add(m); break; }
    }
    return list;
  }

  public synchronized Memo findMemo(int memoId) {
    for (Memo m : memos) if (m.getId() == memoId) return m;
    return null;
  }

  public synchronized Memo addMemo(int classId, Memo memo) {
    if (memo.getId() <= 0) memo.setId(memoSeq.getAndIncrement());
    memos.add(memo);
    if (classId > 0) classToMemoIds.computeIfAbsent(classId, k -> new ArrayList<>()).add(memo.getId());
    return memo;
  }

  public synchronized boolean updateMemo(int ignored, Memo memo) {
    for (int i = 0; i < memos.size(); i++) {
      if (memos.get(i).getId() == memo.getId()) { memos.set(i, memo); return true; }
    }
    return false;
  }

  public synchronized boolean deleteMemo(int classId, int memoId) {
    if (classId > 0) {
      List<Integer> ids = classToMemoIds.get(classId);
      if (ids != null) ids.removeIf(id -> id == memoId);
    } else {
      // remove from all mappings
      for (List<Integer> ids : classToMemoIds.values()) ids.removeIf(id -> id == memoId);
    }
    return memos.removeIf(m -> m.getId() == memoId);
  }

  public synchronized boolean closeMemo(int classId, int memoId) { return false; }
  public synchronized long countOpenBugs(int classId) { return 0L; }

  public void seed() {
    ClassEntry a = addClass(new ClassEntry(0, "ClassListServlet", "山田", "doing"));
    ClassEntry b = addClass(new ClassEntry(0, "Task", "佐藤", "todo"));
    addMemo(a.getId(), new Memo(0, "note", "初期メモ", "管理者", "これは初期メモの詳細です。"));
  }
}
