
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>


<div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="<%=request.getContextPath()%>/index" style="width:240px">
          	<img src="<%=request.getContextPath()%>/resources/img/logo.png"/>
          	<fmt:message key="app.title"/>
          </a>
          <div class="nav-collapse collapse">
            <p class="navbar-text pull-right">
              <!-- <i class="icon-user icon-white"></i> -->
              <img src="<%=request.getContextPath()%>/resources/img/icons/user.png" />
              <security:authentication property="principal.username" /> : 
              <a href="<%=request.getContextPath()%>/auth/logout" class="navbar-link">
              	 [ <fmt:message key="app.logout"/> ]
              </a>              
            </p>
            <ul class="nav">
           	  <li><a href="<%=request.getContextPath()%>/application/list">Aplicaciones</a></li>
           	  <security:authorize access="hasRole('ROLE_ADMIN')">
           	  	<li><a href="<%=request.getContextPath()%>/user/list">Usuarios</a></li>
           	  </security:authorize>
              
              <li><a href="<%=request.getContextPath()%>/resources/docs/manual.pdf" target="_blank">Ayuda</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
 </div>

<div id="processWindow" class="modal hide fade">
	<div class="modal-header">		
		<h3 id="processWindowTitle" ><fmt:message key="app.title"/></h3>
	</div>
	<div class="modal-body">
		<p id="processWindowText" ><fmt:message key="app.processing"/></p>
	</div>
	<div class="modal-footer">
		
	</div>
</div>








