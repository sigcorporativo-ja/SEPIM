<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta name="format-detection" content="telephone=no" />
        <meta name="msapplication-tap-highlight" content="no" />
        <!-- WARNING: for iOS 7, remove the width=device-width and height=device-height attributes. See https://issues.apache.org/jira/browse/CB-4323 -->
        <meta name="viewport" content="user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1, width=device-width, height=device-height, target-densitydpi=device-dpi" />
        
        <!-- 
        <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css" />
		<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
		<script src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
		-->
		
		<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/mobile/js/lib/jquery-mobile/jquery.mobile-1.4.5.min.css" />
		<script src="<%=request.getContextPath()%>/resources/mobile/js/lib/jquery-mobile/jquery-1.11.1.min.js"></script>
		<script src="<%=request.getContextPath()%>/resources/mobile/js/lib/jquery-mobile/jquery.mobile-1.4.5.min.js"></script>
		<script src="<%=request.getContextPath()%>/resources/mobile/js/lib/fastclick.js"></script>
		<script src="<%=request.getContextPath()%>/resources/mobile/js/lib/proj4.js"></script>
		
        <title>CDAU M�vil</title>
                
    </head>
    <body>
    	<!-- P�gina splash -->
		<div data-role="page" id="splash">
			<div style='text-align:center;'>
				<div style='margin-top:150px;'>
					<h1>Junta de Andalucia</h1>
					<img src="<%=request.getContextPath()%>/resources/mobile/js/lib/jquery-mobile/images/ajax-loader.gif"/>
				</div>
			</div>
		</div>
		<!-- P�gina splash -->
    	
    
   		<!-- P�gina principal -->
		<div data-role="page" id="inicio">
			<div data-role="header">
				<h1 id="app-name">Inicio</h1>
			</div>
	
			<div role="main" class="ui-content">
				<a class="ui-btn ui-shadow ui-corner-all ui-btn-b" data-direction="reverse" href="javascript:geolocalizar()">Cerca de mi</a>
				<a id="btn-buscar" class="ui-btn ui-shadow ui-corner-all ui-btn-b" data-direction="reverse" href="javascript:cargarCategoria()">B�squeda por categor�a</a>
				<!--<a class="ui-btn ui-shadow ui-corner-all ui-btn-b" data-direction="reverse" href="#busqueda">B�squeda por texto libre</a>-->
				
			</div>
	
			<!-- 
			<div data-role="footer" data-theme="a">
				<h4>footer</h4>
			</div>
			 -->
		</div>
		<!-- P�gina principal -->
		
		<!-- P�gina localizador -->
		<div data-role="page" id="busqueda">
			<div data-role="header">
				<a href="javascript:inicio()" data-icon="back">Atr�s</a>
				<h1>B�squeda</h1>
				<a href="javascript:inicio()" data-icon="home">Inicio</a>
			</div>
			
			<div role="main" class="ui-content">
				<input id="txtBusqueda" type="search">
				<a class="ui-btn ui-shadow ui-corner-all ui-btn-b" href="javascript:buscarGeobusquedas($('#txtBusqueda').val(),listarResultadosGB)">Buscar</a>			    
				<ul id="listSuggest" data-role="listview" data-inset="true"></ul>
			</div>
			
		</div>
		<!-- P�gina localizador -->

		<!-- P�gina localizador -->
		<div data-role="page" id="localizador">
			<div data-role="header">
				<a href="javascript:inicio()" data-icon="back">Atr�s</a>
				<h1>Localizador</h1>
				<a href="javascript:inicio()" data-icon="home">Inicio</a>
			</div>
			
			<div role="main" class="ui-content">
				<div class="ui-field-contain">
				    <select name="provincias" id="provincias" onchange="cargarMunicipios()">
				    </select>
				    <select name="municipios" id="municipios">
				    </select>
					<a href="javascript:establecerLocalizacion()" class="ui-btn">Aceptar</a>
				</div>
			</div>
			
			<!-- 
			<div data-role="footer" data-theme="a">
				<h4>footer</h4>
			</div>
			 -->
		</div>
		<!-- P�gina localizador -->
		
		
		<!-- P�gina Categor�as -->
		<div data-role="page" id="categorias">
			<div data-role="header">
				<a href="javascript:atras()" data-icon="back">Atr�s</a>
				<h1>Categor�as</h1>
				<a href="javascript:inicio()" data-icon="home">Inicio</a>
			</div>
	
			<div role="main" class="ui-content" id="contenidoCategorias">
			
			</div>
	
			<!-- 
			<div data-role="footer" data-theme="a" data-position="fixed">
				<div style="text-align:center">
					 <a href="#" class="ui-btn">Ver mapa</a>
				</div>
			</div>
			 -->
		</div>
		<!-- P�gina Categor�as -->
		
		
		<!-- P�gina datos -->
		<div data-role="page" id="datos">
			<div data-role="header" data-position="fixed">
				<a href="javascript:atras()" data-icon="back">Atr�s</a>
				<h1>Datos</h1>
				<a href="javascript:inicio()" data-icon="home">Inicio</a>
			</div>
			<div role="main" class="ui-content">
				
				<ul id="listaDatos" data-role="listview" data-filter="true" data-filter-placeholder="Filtra resultados"></ul>

				
			</div>
		</div>
		<!-- P�gina datos -->
		
		
		<!-- P�gina mapa -->
		<div data-role="page" id="mapa">
			<div id="mapa-header" data-role="header" data-position="fixed" data-fullscreen="true">
				<a href="javascript:atrasMapa()" data-icon="back">Atr�s</a>
				<h1>Mapa</h1>
				<a href="javascript:inicio()" data-icon="home">Inicio</a>
			</div>
			<div id="map" role="main" class="ui-content" style="padding:0px;">
				<iframe id="mapea" style="position:absolute; width:100%; height:100%;border:none;">
				</iframe>
			</div>
		</div>
		<!-- P�gina mapa -->

     	
        <!-- <script type="text/javascript" src="cordova.js"></script>  -->
        <script type="text/javascript" src="<%=request.getContextPath()%>/resources/mobile/js/index.js"></script>
        <script type="text/javascript">
              	// <parametros de configuracion>
              	url = location.protocol + "//" + location.host + "<%=request.getContextPath()%>/api";
		//url = "http://www.juntadeandalucia.es/sandetel/publicacion/sepim_server/api";
             	idAplicacion = ${application.id};
              	// </parametros de configuracion>	
               	init();
        </script>
    </body>
</html>
