<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>クラス詳細</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css" />
  <script defer src="${pageContext.request.contextPath}/assets/app.js"></script>
</head>
<body><div class="container">
  <h1>クラス詳細</h1>
  <p><a class="btn" href="<c:url value='/classes'/>">一覧へ戻る</a></p>

  <div class="card section" id="class">
    <div class="card-header">
      <div><strong>${clazz.className}</strong></div>
      <div class="btn-group">
        <a class="btn" href="<c:url value='/classes/edit'><c:param name='id' value='${clazz.id}'/></c:url>">更新</a>
        <a class="btn btn-danger" href="<c:url value='/class/delete'><c:param name='id' value='${clazz.id}'/></c:url>">削除</a>
      </div>
    </div>
    <div class="card-body">
      <p class="muted">ID: ${clazz.id}</p>
      <p class="muted">作成者: ${clazz.classCreated}</p>
      <p class="muted">ステータス: ${clazz.status}</p>
    </div>
  </div>

  <div class="section" id="memos">
    <h2>メモ一覧</h2>
    <p class="btn-group">
      <a class="btn btn-primary" href="<c:url value='/memo/new'><c:param name='classId' value='${clazz.id}'/><c:param name='back' value='/classes/${clazz.id}'/></c:url>">新規メモ</a>
    </p>
    <div class="stack">
      <c:forEach var="m" items="${memos}">
        <div class="card">
          <div class="card-header">
            <div>
              <span class="badge">${m.memoType}</span>
              <strong>${m.memoTitle}</strong>
              <span class="muted" style="margin-left:8px">${m.memoCreated}</span>
            </div>
            <div class="btn-group">
              <a class="btn" href="<c:url value='/memo/edit'><c:param name='memoId' value='${m.id}'/><c:param name='classId' value='${clazz.id}'/><c:param name='back' value='/classes/${clazz.id}'/></c:url>">更新</a>
              <a class="btn btn-danger" href="<c:url value='/memo/delete'><c:param name='memoId' value='${m.id}'/><c:param name='classId' value='${clazz.id}'/><c:param name='back' value='/classes/${clazz.id}'/></c:url>">削除</a>
            </div>
          </div>
          <div class="card-body">
            <div>${m.memoDetail}</div>
          </div>
        </div>
      </c:forEach>
    </div>
  </div>
</div></body>
</html>
