<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="tag.jsp" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>商城高并发抢购-商品详情页面</title>
    <%@include file="head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-default">
        <input id="killId" value="${detail.id}" type="hidden"/>
        <div class="panel-heading">
            <h2>商品名称：${detail.itemName}</h2>
        </div>
        <div class="panel-body">
            <h3 class="text-danger">剩余数量：${detail.total}</h3>
        </div>
        <div class="panel-body">
            <h4 class="text-danger">开始时间：<fmt:formatDate value="${detail.startTime}" pattern='yyyy-MM-dd HH:mm:ss'/></h4>
        </div>
        <div class="panel-body">
            <h4 class="text-danger">结束时间：<fmt:formatDate value="${detail.endTime}" pattern='yyyy-MM-dd HH:mm:ss'/></h4>
        </div>
    </div>

    <td>
        <c:choose>
            <c:when test="${detail.canKill==1}">
                <a class="btn btn-info" style="font-size: 20px" onclick="executeKill()">抢购</a>
            </c:when>
            <c:otherwise>
                <a class="btn btn-info" style="font-size: 20px">商品已抢购完毕！！或者不在抢购时间段！!</a>
            </c:otherwise>
        </c:choose>
    </td>
</div>

</body>
<script src="${ctx}/static/plugins/jquery.js"></script>
<script src="${ctx}/static/plugins/bootstrap-3.3.0/js/bootstrap.min.js"></script>
<script src="${ctx}/static/plugins/jquery.cookie.min.js"></script>
<script src="${ctx}/static/plugins/jquery.countdown.min.js"></script>

<%--<script src="${ctx}/static/script/kill.js"></script>--%>
<link rel="stylesheet" href="${ctx}/static/css/detail.css" type="text/css">
<script type="text/javascript">
    function executeKill() {
        $.ajax({
            type: "POST",
            url: "${ctx}/kill/execute",
            contentType: "application/json;charset=utf-8",
            data: JSON.stringify(getJsonData()),
            dataType: "json",

            success: function(res){
                if (res.code == 0) {
                    //alert(res.msg);
                    window.location.href="${ctx}/kill/execute/success"
                }else{
                    //alert(res.msg);
                    window.location.href="${ctx}/kill/execute/fail"
                }
            },
            error: function (message) {
                alert("提交数据失败！");
                return;
            }
        });
    }

    function getJsonData() {
        var killId=$("#killId").val();
        /*var data = {
            "killId":killId,
            "userId":1
        };*/
        var data = {
            "killId":killId
        };
        return data;
    }
</script>

</html>
















