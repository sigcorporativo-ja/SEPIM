
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="es">
<head>
	<jsp:include page="template/head.jsp" />
	<script>
		function submitForm(){		
			showProcessWindow();	
		}
		
		function cargarMunicipios(){
			//console.log("cargarMunicipios");
			$("#idMunicipio").html("<option value='' title='--TODOS--'>--TODOS--</option>");
			var idProvincia = $("#idProvincia").val();
			if(idProvincia == ""){
				return;
			}
			$("#loader").show();
			$.ajax({
				 url: "<%=request.getContextPath()%>/api/entidades/" + idProvincia + "/hijos",
		         type: "GET",
		         cache: true,
		         dataType: "json",
		         success: function(municipiosList){
		        	 var i = 0;
		        	 var length = municipiosList.length;
		        	 var htmlOptions = [];
		        	 for(i;i<length;i++){
		        		 htmlOptions.push('<option value="' + municipiosList[i].id +'">' + municipiosList[i].name + '</option>');
		        	 }
		        	 $('#idMunicipio').append(htmlOptions.join(''));
		        	 $("#loader").hide();
		         },
		         error: function(){
			 		 alert("Se ha producido un error al obtener los municipios");
			 		$("#loader").hide();
			 	 }
		     });
		};

		function toggleGeographicFilter(){
			var checkbox = $("#enableGeographicFilter1")[0];
			if(checkbox.checked){
				$("#geographicFilter").show();
			}else{
				$("#geographicFilter").hide();
			}
		}
		
		function init(){
			toggleGeographicFilter();
			//cargarMunicipios();
		}
		
	</script>	
</head>

<body onload="init()">
	<jsp:include page="template/header.jsp" />
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3 menu">
				<jsp:include page="app_menu.jsp" />
			</div>
			<!--/span-->
			<div class="span9">
				<div class="row-fluid">
					<div class="span12">										
						<form:form method="post" action="${pageContext.request.contextPath}/application/save" modelAttribute="application"  onsubmit="submitForm()" class="form-horizontal">
							<h1>
								<c:if test="${application.id==null}"><fmt:message key="applications.title.new" /></c:if>
								<c:if test="${application.id!=null}"><fmt:message key="applications.title.edit"/>: ${application.name}</c:if>
							</h1>
						
							<div class="form_buttons">
    							<button type="submit" class="btn"><fmt:message key="buttons.save" /></button>
    							<button type="button" class="btn" onclick="history.back();"><fmt:message key="buttons.cancel" /></button>
   							</div> 
    						
    						<div>
								<form:label path="name">Nombre*:</form:label>											
								<form:input path="name" />					
								<form:errors path="name" class="error"></form:errors>										
							</div>	
							
							<div>
								<form:label path="wmcURL">URL de Mapea:</form:label>											
								<form:input path="wmcURL" />					
								<form:errors path="wmcURL" class="error"></form:errors>										
							</div>		

							<div>
								<form:label path="description">Descripción:</form:label>									
								<form:textarea path="description" />	
								<form:errors path="description" class="error"></form:errors>										
							</div>	
							
							 <security:authorize access="hasRole('ROLE_ADMIN')">
								<div>
									<form:label path="user.username">Usuario:</form:label>									
									<form:select path="user.username"  disabled="${application.id!=null}">
										<c:forEach items="${users}" var="user" varStatus="loopStatus" >	
											<form:option value="${user.username}" title="${user.username}">
												${user.username}
											</form:option>	
										</c:forEach>
									</form:select>								
									<form:errors path="user.username" class="error"></form:errors>										
								</div>	
							</security:authorize>
							
							<div>
								<form:label path="enableGeographicFilter">Habilitar filtro geográfico:</form:label>											
								<form:checkbox path="enableGeographicFilter" onclick="toggleGeographicFilter()"/>					
								<form:errors path="enableGeographicFilter" class="error"></form:errors>		 
							</div>
							
							<fieldset id="geographicFilter" style="display:none">
								<legend>Filtro Geográfico</legend>
								<div>
									<form:label path="idProvincia">Provincias:</form:label>									
									<form:select path="idProvincia" onchange="cargarMunicipios()">
										<form:option value=""> -- TODAS --</form:option>	
										<c:forEach items="${provincias}" var="provincia" varStatus="loopStatus" >
											<form:option value="${provincia.id}" title="${provincia.name}">
												${provincia.name}
											</form:option>	
										</c:forEach>
									</form:select>			
									<img id="loader" src="<%=request.getContextPath()%>/resources/img/loader12.gif" style="display:none"/>					
									<form:errors path="idProvincia" class="error"></form:errors>								
								</div>
								<div>
									<form:label path="idMunicipio">Municipios:</form:label>									
									<form:select path="idMunicipio">
										<form:option value=""> -- TODOS --</form:option>	
										<c:forEach items="${municipios}" var="municipio" varStatus="loopStatus" >
											<form:option value="${municipio.id}" title="${municipio.name}">
												${municipio.name}
											</form:option>	
										</c:forEach>
									</form:select>								
									<form:errors path="idMunicipio" class="error"></form:errors>								
								</div>
							</fieldset>
							
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
