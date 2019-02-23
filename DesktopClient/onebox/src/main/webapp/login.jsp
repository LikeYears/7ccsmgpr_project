<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>登录页</title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jquery/jquery-1.5.1.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/login.js"></script>
<link
	href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css"
	rel="stylesheet">
<link
	href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-responsive.css"
	rel="stylesheet">
<script
	src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.js"></script>


<style type="text/css">
body {
	padding-top: 40px;
	padding-bottom: 40px;
	background-color: #f5f5f5;
}

body, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, form,
	fieldset, input, p, blockquote, th, td {
	margin: 0;
	padding: 0;
}

.form-signin {
	max-width: 500px;
	padding: 19px 29px 29px;
	margin: 0 auto 20px;
	background-color: #fff;
	border: 1px solid #e5e5e5;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
	-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
	box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
}

.form-signin .form-signin-heading, .form-signin .checkbox {
	margin-bottom: 15px;
}

.form-signin input[type="text"], .form-signin input[type="password"] {
	height: auto;
	margin-bottom: 15px;
	padding: 7px 9px;
}

h1 {
	color: #fff;
	background: #06b;
	padding: 10px;
	font-size: 200%;
	text-align: center;
}

.labelError {
	line-height: 9px;
	font-size: 10pt;
	color: #f40000;
	border: 1px #ffb8b8 solid;
	padding: 8px 8px 8px 35px;
	background-color: #fef2f2;
}
</style>
</head>
<body>
	<div style="margin-top:80px;">
		<form class="form-signin"
			action="${pageContext.request.contextPath}/admin" method="post"
			onSubmit="return check()">
			<h2 class="form-signin-heading">登录</h2>
			<hr>
			<table>
				<tr>
					<td></td>
					<td><font color="red">${error }</font></td>
					<td></td>
				</tr>

				<tr>
					<td align="right">用户名：</td>
					<td><input type="text" name="username" id="username"
						class="input"></td>
					<td><label class="labelError" id="usernameError"></label></td>
				</tr>

				<tr>
					<td align="right">密码：</td>
					<td><input type="password" name="password" id="password"
						class="input"></td>
					<td><label class="labelError" id="passwordError"></label></td>
				</tr>

				<tr>
					<td></td>
					<td><button type="submit" class="btn btn-primary">登陆</button>
						<input type="reset" class="btn btn-primary" value="重置"
						style="margin-left: 50px;" /></td>

				</tr>

			</table>
		</form>
	</div>
</body>
</html>