
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="es">
<head>
	<jsp:include page="template/head.jsp" />
	<script>
		function submitForm(){		
			showProcessWindow();	
		}
		
		function showDataSource(){
			var dataSource = $("#dataSourceType").val();
			if(dataSource=="dataBaseDS"){
				$("#fuente-datos").show();
			}else{
				$("#fuente-datos").hide();
			}
		}
		
		function checkAll(){
			$(".inputVisible").each(function(index,checkbox){
				checkbox.checked = $("#check-control")[0].checked;
			});
		}
		
		function onlyOneSelected(checkbox){
			if(checkbox.checked){
				$(".inputPrimary").each(function(index,checkbox){
					checkbox.checked = false;
				});
			}
			checkbox.checked = true;
		}
		
		function togglePassword(){
			if ($("#dataSource\\.password")[0].type=="password"){
				$("#dataSource\\.password")[0].type="text";	
			}else{
				$("#dataSource\\.password")[0].type="password";
			}
		}
		
		function init(){
			showDataSource();
			
			$('#dataSource\\.symbols0\\.strokeColor').colorpicker({format:'hex'});
			$('#dataSource\\.symbols0\\.strokeColor').colorpicker().on('changeColor', function(event){
				//console.log(event.color.toHex());
				$('#dataSource\\.symbols0\\.strokeColor').css({borderRightColor:event.color.toHex()});
				//$('#geolocalizacionColor'). = ev.color.toHex();
			});
			if ($('#dataSource\\.symbols0\\.strokeColor').val()!=""){
				$('#dataSource\\.symbols0\\.strokeColor').css({borderRightColor:$('#dataSource\\.symbols0\\.strokeColor').val()});
			}
			
			$('#dataSource\\.symbols0\\.fillColor').colorpicker({format:'hex'});
			$('#dataSource\\.symbols0\\.fillColor').colorpicker().on('changeColor', function(event){
				//console.log(event.color.toHex());
				$('#dataSource\\.symbols0\\.fillColor').css({borderRightColor:event.color.toHex()});
				//$('#geolocalizacionColor'). = ev.color.toHex();
			});
			if ($('#dataSource\\.symbols0\\.fillColor').val()!=""){
				$('#dataSource\\.symbols0\\.fillColor').css({borderRightColor:$('#dataSource\\.symbols0\\.fillColor').val()});
			}
			
			$("#dataSource\\.password")[0].type="password";
				
		}
		
		function showGraphic(){
			var urlGraphic = $("#dataSource\\.symbols0\\.graphicURL").val();
			if(urlGraphic == ""){
				alert("el campo URL del gráfico está vacio");
				return;
			}
			$("#previewGraphic").attr("src",urlGraphic);
			$("#previewGraphicWindow").modal({});
		}
		
	</script>	
</head>

<body onload="init()">
	<jsp:include page="template/header.jsp" />
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3 menu">
				<jsp:include page="category_menu.jsp" />
			</div>
			<!--/span-->
			<div class="span9">
				<div class="row-fluid">
					<div class="span12">										
						<form:form method="post" action="${pageContext.request.contextPath}/category/save/${application.id}" modelAttribute="category"  enctype="multipart/form-data"  onsubmit="submitForm()"  class="form-horizontal">
							<h1>
								<c:if test="${category.id==null}"><fmt:message key="categories.title.new" /></c:if>
								<c:if test="${category.id!=null}"><fmt:message key="categories.title.edit"/>: ${category.name}</c:if>
							</h1>
						
							<div class="form_buttons">
    							<button type="submit" class="btn"><fmt:message key="buttons.save" /></button>
    							<button type="button" class="btn" onclick="history.back();"><fmt:message key="buttons.cancel" /></button>
   							</div> 	
   							
    						
    						<h2>Categoría</h2>	
    						
    						<div>
								<form:label path="name">Nombre*:</form:label>											
								<form:input path="name" />					
								<form:errors path="name" class="error"></form:errors>										
							</div>		
							
							<div>
								<form:label path="dataSourceType">Asignar fuente de datos*:</form:label>											
								<form:select path="dataSourceType" onchange="showDataSource()">
									<form:option value="--">No</form:option>
									<form:option value="dataBaseDS">Si</form:option>
								</form:select>
								<form:errors path="dataSourceType" class="error"></form:errors>										
							</div>		
							
							
							<div>
    							<form:label path="logo">Imagen:</form:label>
    							<form:input path="logo" type="file"/>   
    							<c:if test="${category.logo!= null}">
									<a href="<%=request.getContextPath()%>/category/download_logo/${category.id}" target="_blank">
										<img src="<%=request.getContextPath()%>/resources/img/icons/download.png" />
										Descargar
									</a>	
								</c:if>	
    							<form:errors path="logo" class="error" /> 	    					
    						</div>
    						
    						<div>
    							<form:label path="resourceDS">Fichero de recurso (KML,SHP,etc):</form:label>
    							<form:input path="resourceDS" type="file"/>   
    							<c:if test="${category.resourceDS!= null}">
									<a href="<%=request.getContextPath()%>/category/download_resource/${category.id}" target="_blank">
										<img src="<%=request.getContextPath()%>/resources/img/icons/download.png" />
										Descargar
									</a>	
								</c:if>	
    							<form:errors path="resourceDS" class="error" /> 	    					
    						</div>
    						
							
							<div id="fuente-datos" style="display:none">
    							<h2>Fuente de datos</h2>
    							<form:errors class="error"/>	
    						
	    						<div>
									<form:label path="dataSource.urlDataBase">URL*:</form:label>											
									<form:input path="dataSource.urlDataBase" readonly="${category.dataSource.id!=null}"  />					
									<form:errors path="dataSource.urlDataBase" class="error"></form:errors>										
								</div>	
	    						
	    						<div>
									<form:label path="dataSource.port">Puerto*:</form:label>											
									<form:input path="dataSource.port" readonly="${category.dataSource.id!=null}" />					
									<form:errors path="dataSource.port" class="error"></form:errors>										
								</div>
								
								<div>
									<form:label path="dataSource.dataBase">Base de datos*:</form:label>											
									<form:input path="dataSource.dataBase" readonly="${category.dataSource.id!=null}" />					
									<form:errors path="dataSource.dataBase" class="error"></form:errors>										
								</div>
								
								<div>
									<form:label path="dataSource.schema">Esquema*:</form:label>											
									<form:input path="dataSource.schema" readonly="${category.dataSource.id!=null}" />					
									<form:errors path="dataSource.schema" class="error"></form:errors>										
								</div>
								
								<div>
									<form:label path="dataSource.table">Tabla*:</form:label>											
									<form:input path="dataSource.table" readonly="${category.dataSource.id!=null}" />					
									<form:errors path="dataSource.table" class="error"></form:errors>										
								</div>
								
								<div>
									<form:label path="dataSource.filter">Filtro:</form:label>									
									<form:textarea path="dataSource.filter" readonly="${category.dataSource.id!=null}" />	
									<form:errors path="dataSource.filter" class="error"></form:errors>										
								</div>	
								
								<div>
									<form:label path="dataSource.user">Usuario*:</form:label>											
									<form:input path="dataSource.user" readonly="${category.dataSource.id!=null}" />					
									<form:errors path="dataSource.user" class="error"></form:errors>										
								</div>
								
								<div>
									<form:label path="dataSource.password">Contraseña*:</form:label>											
									<form:input path="dataSource.password" readonly="${category.dataSource.id!=null}"  />	
									<span onclick="togglePassword()" style="cursor:pointer">(mostrar/ocultar)</span>				
									<form:errors path="dataSource.password" class="error"></form:errors>										
								</div>
	    						
	    						
	    						<c:if test="${fn:length(category.dataSource.fields) > 0}">
	    							<h2>Campos</h2>
		    						<table class="table table-striped">
		    							<thead>
		    								<tr>
		    									<th>Nombre</th>
		    									<th style="text-align:center">Nombre público</th>
		    									<th style="text-align:center">Campo principal</th>
		    									<th>
		    									<input id="check-control" type="checkbox" onclick="checkAll()" title="Marcar/Desmarcar todos los campos"/>
		    									¿Visible?
		    									</th>
		    								</tr>
		    							</thead>
		    							<tbody>
		    								<c:forEach items="${category.dataSource.fields}" var="field" varStatus="loopStatus">
			   									<tr>
			   										<td>${field.name}</td>
			   										<td><form:input path="dataSource.fields[${loopStatus.index}].publicName"/></td>
			   										<td style="text-align:center"><form:checkbox class="inputPrimary" path="dataSource.fields[${loopStatus.index}].primaryField" onclick="onlyOneSelected(this)"/></td>
			   										<td style="text-align:center"><form:checkbox class="inputVisible" path="dataSource.fields[${loopStatus.index}].visible"/></td>
			   									</tr>
		    								</c:forEach>
		    							</tbody>
		    						</table>
	    						</c:if>
	    						
	    						<c:if test="${category.dataSource.id != null}">
	    							<h2>Simbología</h2>
									<c:choose>
										<c:when test="${category.dataSource.symbols[0].class.name=='es.juntadeandalucia.sepim.model.PolygonSymbol'}">
											<div>
												<form:label path="dataSource.symbols[0].strokeWidth">Grosor de línea*:</form:label>											
												<form:input path="dataSource.symbols[0].strokeWidth" />					
												<form:errors path="dataSource.symbols[0].strokeWidth" class="error"></form:errors>										
											</div>
											<div>
												<form:label path="dataSource.symbols[0].strokeColor">Color de línea*:</form:label>											
												<form:input path="dataSource.symbols[0].strokeColor" style='border-right: 40px solid #ccc;width:410px'/>					
												<form:errors path="dataSource.symbols[0].strokeColor" class="error"></form:errors>										
											</div>		
											<div>
												<form:label path="dataSource.symbols[0].fillColor">Color de relleno*:</form:label>											
												<form:input path="dataSource.symbols[0].fillColor" style='border-right: 40px solid #ccc;width:410px'/>					
												<form:errors path="dataSource.symbols[0].fillColor" class="error"></form:errors>										
											</div>		
											<div>
												<form:label path="dataSource.symbols[0].fillOpacity">Opacidad de relleno*:</form:label>											
												<form:select path="dataSource.symbols[0].fillOpacity">
													<form:option value="0" title="0% (Transaparente)">0% (Transparente)</form:option>
													<form:option value="0.1" title="10%">10% </form:option>
													<form:option value="0.2" title="20%">20% </form:option>
													<form:option value="0.3" title="30%">30% </form:option>
													<form:option value="0.4" title="40%">40% </form:option>
													<form:option value="0.5" title="50%">50% </form:option>
													<form:option value="0.6" title="60%">60% </form:option>
													<form:option value="0.7" title="70%">70% </form:option>
													<form:option value="0.8" title="80%">80% </form:option>
													<form:option value="0.9" title="90%">90% </form:option>
													<form:option value="1" title="100% (Opaco)">100% (Opaco) </form:option>
												</form:select>				
												<form:errors path="dataSource.symbols[0].fillOpacity" class="error"></form:errors>										
											</div>					
										</c:when>
										
										<c:when test="${category.dataSource.symbols[0].class.name=='es.juntadeandalucia.sepim.model.LineSymbol'}">
											<div>
												<form:label path="dataSource.symbols[0].strokeWidth">Grosor de línea*:</form:label>											
												<form:input path="dataSource.symbols[0].strokeWidth" />					
												<form:errors path="dataSource.symbols[0].strokeWidth" class="error"></form:errors>										
											</div>
											<div>
												<form:label path="dataSource.symbols[0].strokeColor">Color de línea*:</form:label>											
												<form:input path="dataSource.symbols[0].strokeColor"  style='border-right: 40px solid #ccc;width:410px'/>					
												<form:errors path="dataSource.symbols[0].strokeColor" class="error"></form:errors>										
											</div>		
										</c:when>
										
										<c:when test="${category.dataSource.symbols[0].class.name=='es.juntadeandalucia.sepim.model.PointSymbol'}">
											<div>
												<form:label path="dataSource.symbols[0].graphicURL">URL del gráfico*:</form:label>											
												<form:input path="dataSource.symbols[0].graphicURL" />	
												<a href="javascript:showGraphic()">Ver gráfico</a>
												<form:errors path="dataSource.symbols[0].graphicURL" class="error"></form:errors>										
											</div>		
										</c:when>
									</c:choose>
	    						</c:if>
    						</div>
    						
    						<div class="form_buttons">
    							<button type="submit" class="btn"><fmt:message key="buttons.save" /></button>
    							<button type="button" class="btn" onclick="history.back();"><fmt:message key="buttons.cancel" /></button>
   							</div> 
						</form:form>
						
						<div id="previewGraphicWindow" class="modal hide fade">
							<div class="modal-header">		
								URL del gráfico
							</div>
							<div class="modal-body">
								<img id="previewGraphic" />
							</div>
							<div class="modal-footer">
								<button aria-hidden="true" data-dismiss="modal" class="btn">Cerrar</button>
							</div>
						</div>

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
