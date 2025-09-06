<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>クラス一覧</title>
  <style>
    body{font-family:sans-serif;margin:24px}
    table{border-collapse:collapse;width:100%}
    th,td{border:1px solid #ccc;padding:8px;text-align:left}
    th{background:#f7f7f7}
  </style>
  <script>
    // 進捗セレクトは枠組みのみ。値や送信は後で実装。
  </script>
  </head>
<body>
  <h1>クラス一覧</h1>
  <p><a href="<c:url value='/'/>">トップへ戻る</a></p>

  <table>
    <thead>
      <tr>
        <th>ID</th>
        <th>パッケージ</th>
        <th>クラス名</th>
        <th>作成者</th>
        <th>ステータス</th>
        <th>進捗</th>
        <th>詳細</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="c" items="${classes}">
        <tr>
          <td>${c.id}</td>
          <td>${c.packageName}</td>
          <td>${c.className}</td>
          <td>${c.author}</td>
          <td>${c.status}</td>
          <td>
            <select name="progress" class="progress-select" data-class-id="${c.id}">
              <!-- TODO: options to be provided by owner -->
            </select>
            ${c.progress}%
          </td>
          <td><a href="<c:url value='/classes/${c.id}'/>">詳細</a></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</body>
</html>

