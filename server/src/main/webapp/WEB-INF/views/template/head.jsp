
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

    
<meta charset="utf-8">
<title><fmt:message key="app.title"/></title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Administrador CDAU">
<meta name="author" content="Héctor Javier Méndez Alfaro - hmendez@saig.es">

<!-- Estilos -->


<link href="<%=request.getContextPath()%>/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<style type="text/css">
  body {
    padding-top: 60px;
    padding-bottom: 40px;
  }
  .sidebar-nav {
    padding: 9px 0;
  }
</style>
<link href="<%=request.getContextPath()%>/resources/bootstrap/css/bootstrap-responsive.css" rel="stylesheet">
<link href="<%=request.getContextPath()%>/resources/bootstrap/css/datepicker.css" rel="stylesheet">
<link href="<%=request.getContextPath()%>/resources/colorpicker/css/colorpicker.css" rel="stylesheet">
<!-- <link href="<%=request.getContextPath()%>/resources/lib/datatables/css/jquery.dataTables.css" rel="stylesheet"> -->
<link href="<%=request.getContextPath()%>/resources/css/main.css" rel="stylesheet">


<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<!-- Fav and touch icons -->
<link type="image/x-icon" href="<%=request.getContextPath()%>/resources/img/favicon.ico" rel="shortcut icon">