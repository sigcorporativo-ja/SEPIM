
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>	
	<title>Error 404</title>
	<jsp:include page="template/head.jsp"></jsp:include>
	
	<style>
		body{
			background: #888;
		}
		.container_login{
			margin: 4% auto 1em;
			padding: 0;
			width: 464px;
		}
		.login{
			background: #f5f5f5;
			border-radius:9px;
			box-shadow: 0 1px 30px #333;
			color:#333;
			font-weight:bold;
			text-align:left;
		}		
		.form_login{
			border-top: 1px solid #1A6FEF;
			margin:0;
			padding:30px;
		}
		.field{
			margin-bottom:20px;
		}
		input[type="text"], input[type="password"]{			
			border: 1px solid #ccc;			
			padding: 6px 4px 6px 35px;
			width: 365px;
			height: 30px;
		}
		.title_login{
			font-size: 20px;
			padding:15px 10px 10px 30px;
			border-bottom: 2px solid #1A6FEF;
		}
		h2 a{
			color: #eee;
			font-size: 11px;
			margin:0;
			text-decoration: none;
			text-shadow: 0 -1px 0 #000;
			text-align: center;
		}
		.user_field{
			
			background: red;
		}
		.password_field{			
			background: url("<%=request.getContextPath()%>/resources/img/bg_form_password.png") no-repeat 10px 8px #fff;			
		}
		
		.error{			
			margin-bottom: 10px;
		}
		
	</style>
</head>
<body>
	<div class="container_login">
			<div class="login">								
				<div class="title_login">
					<img src="<%=request.getContextPath()%>/resources/img/logo_ja_negro.png"/>
					Administración 
				</div>
				<div class="form_login">
					<div style="text-align:center">
						Página no encontrada (Error: 404)
					</div>
					<div style="margin-top:20px;text-align:center">
						<a href="<%=request.getContextPath()%>/index">Volver al inicio</a>
					</div>
				</div>
			</div>
	</div>	
</body>
</html>