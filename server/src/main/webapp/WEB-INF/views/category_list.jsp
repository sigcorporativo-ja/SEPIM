
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 

<!DOCTYPE html>
<html lang="es">
<head>
	<jsp:include page="template/head.jsp" />
	<script>
	
	</script>	
</head>

<body onload="">
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
						<h1><fmt:message key="categories.title.list" /></h1>	
						<p>
							<fmt:message key="categories.info" />
						</p>
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
