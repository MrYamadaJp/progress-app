<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>クラス詳細</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css?v=2" />
  <script defer src="${pageContext.request.contextPath}/assets/app.js"></script>
  <style>.badge{background:#6c757d;color:#fff;padding:2px 6px;border-radius:4px;margin-right:6px;font-size:12px;}</style>
</head>
<body class="page-detail"><div class="container">
  <h1 class="page-title">クラス詳細</h1>
  <p><a class="btn" href="<c:url value='/classes'/>">一覧へ戻る</a></p>

  <div class="card section" id="class">
    <div class="card-header">
      <div>
        <c:if test="${clazz.classPinned}"><span class="badge" style="background:#ffbe0b;color:#000;">PIN</span></c:if>
        <c:if test="${not empty clazz.classType}"><span class="badge">${clazz.classType}</span></c:if>
        <strong>${clazz.className}</strong>
      </div>
    </div>
    <div class="card-body">
      <p class="muted">ID: ${clazz.id}</p>
      <p class="muted">作成者 ${clazz.classCreated}</p>
      <p class="muted">ステータス: ${clazz.status}</p>
    </div>
  </div>

  <div class="section" id="memos">
    <h2 class="memo-title">メモ一覧</h2>
    <p class="btn-group">
      <a class="btn btn-primary" href="<c:url value='/memo/new'><c:param name='classId' value='${clazz.id}'/><c:param name='back' value='/classes/${clazz.id}'/></c:url>">新規メモ</a>
    </p>
    <div class="stack">
      <c:forEach var="m" items="${memos}">
        <div class="card">
          <div class="card-header">
            <div>
              <span class="badge">${m.memoType}</span>
              <c:if test="${m.memoPinned}"><span class="badge" style="background:#ffbe0b;color:#000;">PIN</span></c:if>
              <strong>${m.memoTitle}</strong>
              <span class="muted" style="margin-left:8px">作成: ${m.memoCreated}
                <c:if test="${not empty m.memoUpdated}"> / 更新: ${m.memoUpdated}</c:if>
              </span>
            </div>
            <div class="btn-group">
              <a class="btn" href="<c:url value='/memo/edit'><c:param name='memoId' value='${m.id}'/><c:param name='classId' value='${clazz.id}'/><c:param name='back' value='/classes/${clazz.id}'/></c:url>">更新</a>
              <a class="btn btn-danger" href="<c:url value='/memo/delete'><c:param name='memoId' value='${m.id}'/><c:param name='classId' value='${clazz.id}'/><c:param name='back' value='/classes/${clazz.id}'/></c:url>">削除</a>
            </div>
          </div>
          <div class="card-body">
            <c:if test="${not empty m.memoTags}"><div class="tags muted">タグ: ${m.memoTags}</div></c:if>
            <div class="memo-detail" style="white-space: pre-wrap;"><c:out value="${m.memoDetail}" escapeXml="true"/></div>
          </div>
        </div>
      </c:forEach>
    </div>
  </div>
</div></body>
</html>
