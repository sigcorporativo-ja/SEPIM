
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 

<!DOCTYPE html>
<html lang="es">
<head>
	<jsp:include page="template/head.jsp" />
	<script>
		function submitForm(){		
			showProcessWindow();	
		}
		
	</script>	
</head>

<body>
	<jsp:include page="template/header.jsp" />
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3 menu">
				<jsp:include page="user_menu.jsp" />
			</div>
			<!--/span-->
			<div class="span9">
				<div class="row-fluid">
					<div class="span12">										
						<form:form method="post" action="${pageContext.request.contextPath}/user/save" modelAttribute="user"  onsubmit="submitForm()" class="form-horizontal">
							<h1>
								<c:if test="${user.username==null}"><fmt:message key="users.title.new" /></c:if>
								<c:if test="${user.username!=null}"><fmt:message key="users.title.edit"/>: ${user.name}</c:if>
							</h1>
						
							<div class="form_buttons">
    							<button type="submit" class="btn"><fmt:message key="buttons.save" /></button>
    							<button type="button" class="btn" onclick="history.back();"><fmt:message key="buttons.cancel" /></button>
   							</div> 
    						
    						<div>
								<form:label path="username">Nombre de usuario*:</form:label>											
								<form:input path="username"  readonly="${user.newUser==false}"/>					
								<form:errors path="username" class="error"></form:errors>										
							</div>		
							<div>
								<form:label path="name">Nombre*:</form:label>											
								<form:input path="name" />					
								<form:errors path="name" class="error"></form:errors>										
							</div>	
							<div>
								<form:label path="password">Password*:</form:label>											
								<form:password path="password" />					
								<form:errors path="password" class="error"></form:errors>										
							</div>		
							<div>
								<form:label path="profile">Perfil*:</form:label>											
								<form:select path="profile">
									<form:option value="ROLE_ADMIN" title="Administrador">Administrador</form:option>
									<form:option value="ROLE_USER" title="Usuario">Usuario</form:option>
								</form:select>				
								<form:errors path="profile" class="error"></form:errors>										
							</div>		
							

							
							
    						<div class="form_buttons">
    							<button type="submit" class="btn"><fmt:message key="buttons.save" /></button>
    							<button type="button" class="btn" onclick="history.back();"><fmt:message key="buttons.cancel" /></button>
   							</div> 
												
						</form:form>

					</div>
				</div>
				<!--/row-->
			</div>
			<!--/span-->
		</div>
		<!--/row-->
		<jsp:include page="template/footer.jsp" />
	</div>
	<!--/.fluid-container-->
	<jsp:include page="template/js.jsp" />
</body>
</html>
