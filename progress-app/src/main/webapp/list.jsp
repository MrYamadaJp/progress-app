<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>クラス一覧</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css" />
</head>
<body><div class="container">
  <h1>クラス一覧</h1>
  <c:if test="${not empty param.warning || not empty warning}">
    <div class="warn">データベースに必要なテーブルが見つかりません。HeidiSQL などで DDL を実行してください。</div>
  </c:if>
  <p>
    <a class="btn" href="<c:url value='/'/>">トップへ戻る</a>
    &nbsp;|&nbsp;
    <a class="btn btn-primary" href="<c:url value='/classes/new'/>">新規クラスの作成</a>
  </p>

  <div class="stack">
    <c:forEach var="c" items="${classes}">
      <div class="card">
        <div class="card-header">
          <div><strong>${c.className}</strong></div>
          <div class="btn-group">
            <a class="btn" href="<c:url value='/classes/${c.id}'/>">詳細</a>
            <a class="btn" href="<c:url value='/classes/edit'><c:param name='id' value='${c.id}'/></c:url>">更新</a>
            <a class="btn btn-danger" href="<c:url value='/class/delete'><c:param name='id' value='${c.id}'/></c:url>">削除</a>
          </div>
        </div>
        <div class="card-body">
          <p class="muted">ID: ${c.id}</p>
          <p class="muted">作成者: ${c.classCreated}</p>
          <p class="muted">ステータス: ${c.status}</p>
        </div>
      </div>
    </c:forEach>
  </div>
</div></body>
</html>
