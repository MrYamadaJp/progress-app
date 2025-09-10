<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css?v=2" />
  <title><c:choose><c:when test="${mode=='create'}">クラス作成</c:when><c:otherwise>クラス編集</c:otherwise></c:choose></title>
  <style>
    .layout { display: grid; gap: 16px; }
    .form label { display:block; margin: .5rem 0; }
    select { padding: .375rem .5rem; }
  </style>
  </head>
<body><div class="container">
  <h1><c:choose><c:when test="${mode=='create'}">クラス作成</c:when><c:otherwise>クラス編集</c:otherwise></c:choose></h1>
  <p><a class="btn" href="<c:url value='/classes'/>">一覧へ戻る</a></p>

  <form method="post" action="" class="form">
    <c:if test="${mode=='edit'}">
      <input type="hidden" name="id" value="${clazz.id}" />
    </c:if>
    <!-- ピン止め（上部に配置） -->
    <label><input type="checkbox" name="classPinned" <c:if test="${clazz.classPinned}">checked</c:if> /> ピン止め（一覧で上部に表示）</label>
    <label>クラス名(例：Test.java) <input type="text" name="className" value="${clazz.className}" required></label>
    <label>作成者(例：山田) <input type="text" name="classCreated" value="${clazz.classCreated}" required></label>
    <label>ステータス(例：作成中) <input type="text" name="status" value="${clazz.status}" /></label>
    <label>クラス種類
      <select name="classType" required>
        <option value="JSP" <c:if test="${clazz.classType=='JSP'}">selected</c:if>>JSP</option>
        <option value="サーブレット" <c:if test="${clazz.classType=='サーブレット'}">selected</c:if>>サーブレット</option>
        <option value="Beans" <c:if test="${empty clazz.classType || clazz.classType=='Beans'}">selected</c:if>>Beans</option>
      </select>
    </label>
    <button type="submit" class="btn btn-primary"><c:choose><c:when test="${mode=='create'}">作成</c:when><c:otherwise>更新</c:otherwise></c:choose></button>
  </form>
</div></body>
</html>
