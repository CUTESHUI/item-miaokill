<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="tag.jsp" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>商城高并发抢购-抢购成功商品详情页面</title>
    <%@include file="head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-body">
            <h3>当前用户名：</h3><h3 class="text-danger">${info.userName}</h3>
        </div>
        <div class="panel-body">
            <h3>抢购的商品名称：</h3><h3 class="text-danger">${info.itemName}</h3>
        </div>
        <div class="panel-body">
            <h3>订单编号：</h3><h3 class="text-danger">${info.code}</h3>
        </div>
        <div class="panel-body">
            <h3>成功抢购的时间：</h3><h3 class="text-danger"><fmt:formatDate value="${info.createTime}" pattern='yyyy-MM-dd HH:mm:ss'/></h3>
        </div>
        <div class="panel-body">
            <h3>当前支付的状态：</h3>
            <h3 class="text-danger">
                <c:choose>
                    <c:when test="${info.status==1}">
                        已成功付款
                    </c:when>
                    <c:otherwise>
                        未支付或已取消
                    </c:otherwise>
                </c:choose>
            </h3>
        </div>
    </div>
    <table>
        <tr>
            <td><strong><input type="button" value="点击生成二维码进行支付" style="font-size: 30px;width: 1100px;height: auto;background-color: #28a4c9;"
                               onclick="alert('扫码')"/></strong></td>
        </tr>
    </table>
</div>

</body>
<script src="${ctx}/static/plugins/jquery.js"></script>
<script src="${ctx}/static/plugins/bootstrap-3.3.0/js/bootstrap.min.js"></script>
<script src="${ctx}/static/plugins/jquery.cookie.min.js"></script>
<script src="${ctx}/static/plugins/jquery.countdown.min.js"></script>
</html>
















