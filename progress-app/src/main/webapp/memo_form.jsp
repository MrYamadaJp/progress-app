<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>メモ管理</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css" />
  <script defer src="${pageContext.request.contextPath}/assets/app.js"></script>
</head>
<body><div class="container">
  <h1><c:choose><c:when test="${mode=='create'}">メモ新規投稿</c:when><c:otherwise>メモ更新</c:otherwise></c:choose></h1>
  <p><a class="btn" href="javascript:history.back()">戻る</a></p>
  <c:choose>
    <c:when test="${mode=='create'}"><c:url var="actionUrl" value='/memo/new' /></c:when>
    <c:otherwise><c:url var="actionUrl" value='/memo/edit' /></c:otherwise>
  </c:choose>
  <form method="post" action="${actionUrl}" class="form">
    <c:if test="${not empty param.memoId}"><input type="hidden" name="memoId" value="${param.memoId}" /></c:if>
    <c:if test="${not empty param.classId}"><input type="hidden" name="classId" value="${param.classId}" /></c:if>
    <c:if test="${not empty param.back}"><input type="hidden" name="back" value="${param.back}" /></c:if>
    <label>種別: <input type="text" name="memoType" value="${memo.memoType}" required /></label>
    <label>タイトル: <input type="text" name="memoTitle" value="${memo.memoTitle}" required /></label>
    <label>作成者: <input type="text" name="memoCreated" value="${memo.memoCreated}" /></label>
    <label>詳細: <textarea name="memoDetail" rows="6" style="width:100%">${memo.memoDetail}</textarea></label>
    <button type="submit"><c:choose><c:when test="${mode=='create'}">投稿</c:when><c:otherwise>更新</c:otherwise></c:choose></button>
  </form>
</div></body>
</html>
