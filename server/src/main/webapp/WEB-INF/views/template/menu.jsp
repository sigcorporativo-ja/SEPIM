<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="well sidebar-nav">
	<ul class="nav nav-list">
		<li class="nav-header">CATEGOR�AS</li>
		
				
	</ul>
</div>



<script>

	
	

	function confirmDelete() {
		return confirm("�Seguro que desea eliminar este elemento?");
		/* bootbox.confirm("Est� seguro de eliminar este elemento", function(result) {
			return result;
		});*/
	}

	function toggle(element) {
		$(element).toggle();
		return false;
	}
</script>