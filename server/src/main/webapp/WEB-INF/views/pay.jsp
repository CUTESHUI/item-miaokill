<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="tag.jsp" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>商城高并发抢购-支付成功页</title>
    <%@include file="head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 align="center">支付成功-请您<span style="color: red"><a href="/kill/paySuccess">查看订单详情</a></span></h2>
        </div>
    </div>
</div>
</body>
<script src="${ctx}/static/plugins/jquery.js"></script>
<script src="${ctx}/static/plugins/bootstrap-3.3.0/js/bootstrap.min.js"></script>
<script src="${ctx}/static/plugins/jquery.cookie.min.js"></script>
<script src="${ctx}/static/plugins/jquery.countdown.min.js"></script>
</html>
















