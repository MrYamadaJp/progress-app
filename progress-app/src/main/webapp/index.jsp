<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Progress App</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css" />
</head>
<body><div class="container">
  <h1>TaskAll</h1>

  <c:choose>
    <c:when test="${not empty sessionScope.USER}">
      <p>ようこそ、<strong>${sessionScope.USER}</strong> さん</p>
      <p class="btn-group">
        <a class="btn btn-primary" href="<c:url value='/classes'/>">クラス一覧へ</a>
        <a class="btn" href="<c:url value='/plugins.jsp'/>">プラグイン一覧へ</a>
      </p>
      <form method="post" action="<c:url value='/logout'/>" class="btn-group">
        <button type="submit" class="btn">ログアウト</button>
      </form>
    </c:when>
    <c:otherwise>
      <div class="card" style="max-width: 420px;">
        <div class="card-header"><strong>ログイン</strong></div>
        <div class="card-body">
          <c:if test="${not empty requestScope.loginError}">
            <div class="warn">${requestScope.loginError}</div>
          </c:if>
          <form method="post" action="<c:url value='/login'/>" class="form">
            <label>ユーザー名: <input type="text" name="username" required></label>
            <label>パスワード: <input type="password" name="password" required></label>
            <button type="submit" class="btn btn-primary">ログイン</button>
          </form>
        </div>
      </div>
    </c:otherwise>
  </c:choose>

</div></body>
</html>
