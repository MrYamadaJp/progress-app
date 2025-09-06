package com.example.progress.repository;

import com.example.progress.model.ClassEntry;
import com.example.progress.model.Memo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryStore {
  private final Map<Integer, ClassEntry> classes = new LinkedHashMap<>();
  private final Map<Integer, List<Memo>> memos = new HashMap<>();
  private final AtomicInteger classSeq = new AtomicInteger(1);
  private final AtomicInteger memoSeq = new AtomicInteger(1);

  public synchronized List<ClassEntry> listClasses() {
    return new ArrayList<>(classes.values());
  }

  public synchronized List<ClassEntry> listClassesSorted() {
    List<ClassEntry> list = listClasses();
    list.sort(Comparator.comparing(ClassEntry::getPackageName)
        .thenComparing(ClassEntry::getClassName));
    return list;
  }

  public synchronized ClassEntry findClass(int id) {
    return classes.get(id);
  }

  public synchronized ClassEntry addClass(ClassEntry entry) {
    if (entry.getId() <= 0) {
      entry.setId(classSeq.getAndIncrement());
    }
    classes.put(entry.getId(), entry);
    return entry;
  }

  public synchronized List<Memo> listMemos(int classId) {
    return new ArrayList<>(memos.getOrDefault(classId, Collections.emptyList()));
  }

  public synchronized Memo addMemo(int classId, Memo memo) {
    if (memo.getId() <= 0) {
      memo.setId(memoSeq.getAndIncrement());
    }
    memo.setClassId(classId);
    memos.computeIfAbsent(classId, k -> new ArrayList<>()).add(memo);
    return memo;
  }

  public synchronized boolean closeMemo(int classId, int memoId) {
    List<Memo> list = memos.get(classId);
    if (list == null) return false;
    Optional<Memo> m = list.stream().filter(x -> x.getId() == memoId).findFirst();
    if (m.isPresent()) {
      m.get().setStatus("closed");
      return true;
    }
    return false;
  }

  public synchronized long countOpenBugs(int classId) {
    return listMemos(classId).stream()
        .filter(m -> "bug".equalsIgnoreCase(m.getType()))
        .filter(m -> "open".equalsIgnoreCase(m.getStatus()))
        .count();
  }

  public void seed() {
    // Simple seed data
    ClassEntry a = new ClassEntry(0, "com.example.progress.task", "TaskListServlet", "山田", "doing", 40, LocalDate.now());
    ClassEntry b = new ClassEntry(0, "com.example.progress.task", "Task", "佐藤", "todo", 10, LocalDate.now().minusDays(2));
    ClassEntry c = new ClassEntry(0, "com.example.progress.web", "ClassListServlet", "中村", "done", 100, LocalDate.now().minusDays(1));

    addClass(a);
    addClass(b);
    addClass(c);

    addMemo(a.getId(), new Memo(0, a.getId(), "bug", "一覧のソート不正", "期限順が逆になることがある", "mid", "高橋", LocalDateTime.now().minusHours(3), "open"));
    addMemo(a.getId(), new Memo(0, a.getId(), "note", "進捗セレクト導入", "プログレスバーを<select>へ", null, "山田", LocalDateTime.now().minusHours(1), "open"));
    addMemo(b.getId(), new Memo(0, b.getId(), "bug", "NPEの可能性", "dueDateがnullの扱い確認", "low", "佐藤", LocalDateTime.now().minusDays(1), "closed"));
  }
}
