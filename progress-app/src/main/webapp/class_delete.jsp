<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>クラス削除</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css" />
  <script defer src="${pageContext.request.contextPath}/assets/app.js"></script>
</head>
<body><div class="container">
  <h1>クラス削除</h1>
  <p>このクラスを削除します。よろしいですか？</p>
  <form method="post" action="" class="btn-group" data-confirm="このクラスを削除します。よろしいですか？">
    <input type="hidden" name="id" value="${id}" />
    <button type="submit" class="btn btn-danger">削除する</button>
    <a class="btn" href="<c:url value='/classes'/>">キャンセル</a>
</form>
</div></body>
</html>
