<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="title_menu">
	<a href="<%=request.getContextPath()%>/application/list"><fmt:message key="applications.title.menu"/></a>		
	<a href="<%=request.getContextPath()%>/application/add"><img src="<%=request.getContextPath()%>/resources/img/icons/add.png" alt="<fmt:message key="buttons.add" />" title="<fmt:message key="buttons.add"/>"/></a>
</div>

<ul class="tree">		
	<c:forEach items="${applications}" var="application" varStatus="loopStatus">			  								
		<li>												
			<span class="${application.id==id_application?'selected':''}">		
				<a href="<%=request.getContextPath()%>/application/run/${application.id}" target="_blank">
					<img src="<%=request.getContextPath()%>/resources/img/icons/application_go.png" alt="<fmt:message key="buttons.open_application"/>" title="<fmt:message key="buttons.open_application"/>">
				</a>		
				<a href="<%=request.getContextPath()%>/application/delete/${application.id}">
					<img src="<%=request.getContextPath()%>/resources/img/icons/delete.png" alt="<fmt:message key="buttons.delete"/>" title="<fmt:message key="buttons.delete"/>" onclick="return confirmDelete()">
				</a>
				<a href="<%=request.getContextPath()%>/application/edit/${application.id}">
					<img src="<%=request.getContextPath()%>/resources/img/icons/edit.png" alt="<fmt:message key="buttons.edit"/> " title="<fmt:message key="buttons.edit"/>">
				</a>
				<a href="<%=request.getContextPath()%>/category/list/${application.id}">
					${application.name}
				</a>
			</span>
		</li>															
	</c:forEach>	
</ul>

<script>
	function confirmDelete(){
		var txt = "<fmt:message key="applications.warning_on_delete"/>";
		return confirm(txt);
	}
</script>