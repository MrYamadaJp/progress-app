<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css" />
  <title>クラス編集</title>
  <style>
    body{font-family:sans-serif;margin:24px}
    label{display:block;margin:8px 0}
  </style>
</head>
<body><div class="container">
  <h1><c:choose><c:when test="${mode=='create'}">クラス作成</c:when><c:otherwise>クラス編集</c:otherwise></c:choose></h1>
  <p><a class="btn" href="<c:url value='/classes'/>">一覧へ戻る</a></p>

  <form method="post" action="" class="form">
    <c:if test="${mode=='edit'}">
      <input type="hidden" name="id" value="${clazz.id}" />
    </c:if>
    <label>クラス名: <input type="text" name="className" value="${clazz.className}" required></label>
    <label>作成者: <input type="text" name="classCreated" value="${clazz.classCreated}" required></label>
    <label>ステータス: <input type="text" name="status" value="${clazz.status}" /></label>
    <button type="submit"><c:choose><c:when test="${mode=='create'}">作成</c:when><c:otherwise>更新</c:otherwise></c:choose></button>
  </form>
</div></body>
</html>
