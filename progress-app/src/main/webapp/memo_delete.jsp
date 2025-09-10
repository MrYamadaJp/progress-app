<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>メモ削除</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css" />
  <script defer src="${pageContext.request.contextPath}/assets/app.js"></script>
</head>
<body><div class="container">
  <h1>メモ削除</h1>
  <p>このメモを削除します。よろしいですか？</p>
  <c:url var="actionUrl" value='/memo/delete' />
  <form method="post" action="${actionUrl}" class="btn-group" data-confirm="このメモを削除します。よろしいですか？">
    <input type="hidden" name="memoId" value="${param.memoId}" />
    <c:if test="${not empty param.classId}"><input type="hidden" name="classId" value="${param.classId}" /></c:if>
    <c:if test="${not empty param.back}"><input type="hidden" name="back" value="${param.back}" /></c:if>
    <button type="submit" class="btn btn-danger">削除する</button>
    <a class="btn" href="javascript:history.back()">キャンセル</a>
  </form>
</div></body>
</html>
