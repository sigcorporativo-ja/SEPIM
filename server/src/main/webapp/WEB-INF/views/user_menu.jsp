<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="title_menu">
	<a href="<%=request.getContextPath()%>/user/list"><fmt:message key="users.title.menu"/></a>		
	<a href="<%=request.getContextPath()%>/user/add"><img src="<%=request.getContextPath()%>/resources/img/icons/add.png" alt="<fmt:message key="buttons.add" />" title="<fmt:message key="buttons.add"/>"/></a>
</div>

<ul class="tree">		
	<c:forEach items="${users}" var="user" varStatus="loopStatus">	
		<li>												
			<span class="">				
				<a href="<%=request.getContextPath()%>/user/delete/${user.username}/">
					<img src="<%=request.getContextPath()%>/resources/img/icons/delete.png" alt="<fmt:message key="buttons.delete"/>" title="<fmt:message key="buttons.delete"/>" onclick="return confirmDelete()">
				</a>
				<a href="<%=request.getContextPath()%>/user/edit/${user.username}/">
					<img src="<%=request.getContextPath()%>/resources/img/icons/edit.png" alt="<fmt:message key="buttons.edit"/> " title="<fmt:message key="buttons.edit"/>">
				</a>
				${user.username}
			</span>
		</li>		
	</c:forEach>	
</ul>

<script>
	function confirmDelete(){
		var txt = "<fmt:message key="users.warning_on_delete"/>";
		return confirm(txt);
	}
</script>