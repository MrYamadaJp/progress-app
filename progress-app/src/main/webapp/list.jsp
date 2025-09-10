<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>クラス一覧</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css?v=2" />
  <style>
    .grid { display:grid; grid-template-columns: 180px 1fr; gap:16px; align-items:start; }
    .sidebar .btn { display:block; width:100%; margin-bottom:8px; }
    .badge { background:#6c757d; color:#fff; font-size:12px; padding:2px 6px; border-radius:4px; margin-right:6px; }
  </style>
</head>
<body class="page-list"><div class="container">
  <h1 class="page-title">クラス一覧</h1>
  <c:if test="${not empty param.warning || not empty warning}">
    <div class="warn">${warning}</div>
  </c:if>
  <p class="btn-group">
    <a class="btn" href="<c:url value='/'/>">トップへ戻る</a>
    <a class="btn btn-primary" href="<c:url value='/classes/new'/>">新規クラスの作成</a>
  </p>

  <c:set var="t" value="${empty param.type ? 'all' : param.type}" />
  <!-- 検索フォーム -->
  <form method="get" action="<c:url value='/classes'/>" class="form" style="margin:8px 0 16px;">
    <input type="text" name="q" placeholder="クラス名で検索" value="${param.q}" style="min-width:240px;" />
    <input type="hidden" name="type" value="${t}" />
    <button type="submit" class="btn">検索</button>
    <a class="btn" href="<c:url value='/classes'/>">クリア</a>
  </form>
  <div class="grid">
    <aside class="sidebar">
      <a class="btn ${t=='all' ? 'btn-primary' : ''}" href="<c:url value='/classes'><c:if test='${not empty param.q}'><c:param name='q' value='${param.q}'/></c:if></c:url>">全て</a>
      <a class="btn ${t=='JSP' ? 'btn-primary' : ''}" href="<c:url value='/classes'><c:param name='type' value='JSP'/><c:if test='${not empty param.q}'><c:param name='q' value='${param.q}'/></c:if></c:url>">JSP</a>
      <a class="btn ${t=='サーブレット' ? 'btn-primary' : ''}" href="<c:url value='/classes'><c:param name='type' value='サーブレット'/><c:if test='${not empty param.q}'><c:param name='q' value='${param.q}'/></c:if></c:url>">サーブレット</a>
      <a class="btn ${t=='Beans' ? 'btn-primary' : ''}" href="<c:url value='/classes'><c:param name='type' value='Beans'/><c:if test='${not empty param.q}'><c:param name='q' value='${param.q}'/></c:if></c:url>">Beans</a>
    </aside>
    <main>
      <div class="stack">
        <c:forEach var="c" items="${classes}">
          <div class="card">
            <div class="card-header">
              <div>
                <c:if test="${c.classPinned}"><span class="badge" style="background:#ffbe0b;color:#000;">PIN</span></c:if>
                <c:if test="${not empty c.classType}"><span class="badge">${c.classType}</span></c:if>
                <strong>${c.className}</strong>
              </div>
              <div class="btn-group">
                <a class="btn" href="<c:url value='/classes/${c.id}'/>">詳細</a>
                <a class="btn" href="<c:url value='/classes/edit'><c:param name='id' value='${c.id}'/></c:url>">更新</a>
                <a class="btn btn-danger" href="<c:url value='/class/delete'><c:param name='id' value='${c.id}'/></c:url>">削除</a>
              </div>
            </div>
            <div class="card-body">
              <p class="muted">ID: ${c.id}</p>
              <p class="muted">作成者 ${c.classCreated}</p>
              <p class="muted">ステータス: ${c.status}</p>
            </div>
          </div>
        </c:forEach>
      </div>
    </main>
  </div>
</div></body>
</html>
