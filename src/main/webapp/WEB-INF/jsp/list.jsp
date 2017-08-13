<%--
  Created by IntelliJ IDEA.
  User: chen
  Date: 2017/6/9
  Time: 10:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<!-- 引入JSTL -->
<%@include file="common/tag.jsp"%>
<head>
	<title>秒杀列表页</title>
	<%@ include file="common/head.jsp"%>
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<style>
		th, td{
			text-align: center;
		}
		a:hover {
			color: red;
		}
	</style>
</head>
<body>
<!-- 页面显示部分 -->
<div class="container">
	<div class="panel panel-default" >
		<div class="panel-heading text-center">
			<h1>秒杀列表</h1>
		</div>
		<div class="panel-body text-center">
			<!-- align="center"：table在div中居中显示-->
			<table class="tabel table-striped table-bordered table-hover" align="center">
				<thead>
				<tr>
					<th>&nbsp;&nbsp;商品名称&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;剩余数量&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;秒杀开始时间&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;秒杀结束时间&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;秒杀创建时间&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;秒杀详情页&nbsp;&nbsp;</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach var="sk" items="${list}">
					<tr class = "text-cnter">
						<td>&nbsp;&nbsp;${sk.name}&nbsp;&nbsp;</td>
						<td>&nbsp;&nbsp;${sk.number}&nbsp;&nbsp;</td>
						<td>
							&nbsp;&nbsp;<fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>&nbsp;&nbsp;
						</td>
						<td>
							&nbsp;&nbsp;<fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>&nbsp;&nbsp;
						</td>
						<td>
							&nbsp;&nbsp;<fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>&nbsp;&nbsp;
						</td>
						<td>
							<a class="btn btn-info" href="/seckill/${sk.seckillId}/detail" title="秒杀入口地址" target="_blank">&nbsp;&nbsp;秒杀入口&nbsp;&nbsp;</a>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
</body>
</html>
