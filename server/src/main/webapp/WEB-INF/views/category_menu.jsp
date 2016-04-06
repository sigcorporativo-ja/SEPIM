<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cdau" uri="http://www.saig.es/cdau" %>

<span>
	<!-- 
	<span id="tree_wms_control">
		<a title="<fmt:message key="buttons.collapse"/>" href="#"><img src="<%=request.getContextPath()%>/resources/img/icons/collapse_all.png"/></a>
		<a title="<fmt:message key="buttons.expand"/>" href="#"><img src="<%=request.getContextPath()%>/resources/img/icons/expand_all.png"/></a>		
	</span>
	 -->
	
	<div class="button_back">
		<a href="<%=request.getContextPath()%>/application/list" class="left_title">
			<img src="<%=request.getContextPath()%>/resources/img/icons/back.png" alt="<fmt:message key="buttons.back" />" title="<fmt:message key="buttons.back"/>"/>
		</a>
		<a href="<%=request.getContextPath()%>/application/run/${application.id}" class="left_title" target="_blank">
			<img src="<%=request.getContextPath()%>/resources/img/icons/application_go.png" alt="<fmt:message key="buttons.open_application" />" title="<fmt:message key="buttons.open_application"/>"/>
		</a>
		${application.name}
	</div>
			
	
	<a href="<%=request.getContextPath()%>/category/list/${application.id}" class="left_title"><fmt:message key="categories.title.menu"/></a>		
	<a href="<%=request.getContextPath()%>/category/add/${application.id}"><img src="<%=request.getContextPath()%>/resources/img/icons/add.png" alt="<fmt:message key="buttons.add" />" title="<fmt:message key="buttons.add"/>"/></a>	
</span>


<ul id="tree_wms" class="tree persist control">					  
	<c:forEach items="${categories}" var="category" varStatus="loopStatus">			  								
		<li>${cdau:renderTree(categoryRenderer,category)}</li>															
	</c:forEach>
</ul>

<script>
	function confirmDelete(){
		var txt = "<fmt:message key="categories.warning_on_delete"/>";
		return confirm(txt);
	}
</script>