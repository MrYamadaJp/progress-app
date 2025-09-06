<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>クラス詳細</title>
  <style>
    body{font-family:sans-serif;margin:24px}
    table{border-collapse:collapse;width:100%}
    th,td{border:1px solid #ccc;padding:8px;text-align:left}
    th{background:#f7f7f7}
    .muted{color:#666}
    .pill{padding:2px 8px;border-radius:12px;font-size:12px;background:#eee}
    .bug{background:#ffd1d1}
    .note{background:#e1f0ff}
    .open{border:1px solid #999}
    .closed{opacity:0.6}
  </style>
</head>
<body>
  <h1>クラス詳細</h1>
  <p><a href="<c:url value='/classes'/>">一覧へ戻る</a></p>

  <h2>${clazz.packageName}.${clazz.className}</h2>
  <p class="muted">作成者: ${clazz.author} / ステータス: ${clazz.status} / 進捗: ${clazz.progress}%</p>

  <h3>メモ一覧</h3>
  <table>
    <thead>
      <tr>
        <th>ID</th>
        <th>種別</th>
        <th>タイトル</th>
        <th>作成者</th>
        <th>作成日時</th>
        <th>状態</th>
        <th>操作</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="m" items="${memos}">
        <tr class="${m.status}">
          <td>${m.id}</td>
          <td><span class="pill ${m.type}">${m.type}</span></td>
          <td>
            <div><strong>${m.title}</strong></div>
            <div class="muted">${m.body}</div>
            <c:if test="${not empty m.severity}"><div class="muted">severity: ${m.severity}</div></c:if>
          </td>
          <td>${m.createdBy}</td>
          <td>${m.createdAt}</td>
          <td>${m.status}</td>
          <td>
            <c:if test="${m.status == 'open'}">
              <form method="post" action="">
                <input type="hidden" name="action" value="close_memo" />
                <input type="hidden" name="memoId" value="${m.id}" />
                <button type="submit">クローズ</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <h3>メモ追加</h3>
  <form method="post" action="" accept-charset="UTF-8">
    <input type="hidden" name="action" value="add_memo" />
    <label>種別:
      <select name="type">
        <option value="note">note</option>
        <option value="bug">bug</option>
      </select>
    </label>
    <br/>
    <label>タイトル: <input type="text" name="title" required /></label>
    <br/>
    <label>本文: <br/><textarea name="body" rows="4" cols="60"></textarea></label>
    <br/>
    <label>severity: <input type="text" name="severity" placeholder="low/mid/high (任意)" /></label>
    <br/>
    <label>作成者: <input type="text" name="createdBy" /></label>
    <br/>
    <button type="submit">追加</button>
  </form>
</body>
</html>

