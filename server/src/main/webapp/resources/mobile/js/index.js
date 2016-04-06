//parámetros de configuración de la aplicación

//===========================================================================
aplicacion = null;
pilaCategorias = [];
coor_x = null;
coor_y = null;
idEntidad = null;
datos = {
	limit: -1,
	offset:0
};
showDistance = false; //JGL - indica si mostrar la distancia o no en la búsqueda por categorías
//JGL - config para GB - no usado
urlCore = "http://geobusquedas-sigc.juntadeandalucia.es/geobusquedas/sepim_sanlucarlamayor/search"; 
urlSuggest = "http://geobusquedas-sigc.juntadeandalucia.es/geobusquedas/sepim_sanlucarlamayor/suggest";
//

proj4.defs([
  [
    'EPSG:25830',
    '+proj=utm +zone=30 +ellps=GRS80 +units=m +datum=ETRS89 +no_defs'],
  [
    'EPSG:23030',
    '+proj=utm +zone=30 +ellps=intl +towgs84=-131,-100.3,-163.4,-1.244,-0.020,-1.144,9.39 +units=m +no_defs'
  ]
]);

//===========================================================================
function obtenerUrlComoObjeto(url){
	var urlObjeto = {};
	separadorUrlParametrosIndex = url.indexOf("?");
	
	urlObjeto.url = url.substr(0, separadorUrlParametrosIndex);
	urlObjeto.parametros = {};
	var parametrosUrl = url.substr(separadorUrlParametrosIndex+1).split("&");
	var i = 0;
	for(i=0;i<parametrosUrl.length;i++){
		var parametro = parametrosUrl[i].split("=");
		if(parametro[0].toLowerCase()=="projection"){
			var projection = parametro[1].split("*");
			if(projection[0].toLowerCase().indexOf("EPSG")){
				urlObjeto.projection = projection[0];
			}else{
				urlObjeto.projection = projection[1];
			}
		}
		urlObjeto.parametros[parametro[0]]= parametro[1];
	}
	ORIGINAL_LAYERS = urlObjeto.parametros.layers;
	
	return urlObjeto;
}


//codigo para corregir un bug que tiene Jquery => https://github.com/jquery/jquery/pull/370/files
//TODO => ¿poner en otro sitio?
//====================================================================================
$(document).ready(function(){
	jQuery.support = (function() {
		marginDiv.style.marginRight = "0";
		div.appendChild( marginDiv );
		support.reliableMarginRight =
		 ( parseInt( ( document.defaultView.getComputedStyle( marginDiv, null ) || { marginRight: 0 } ).marginRight, 10 ) || 0 ) === 0;
	});
	fixHeight();
});
//====================================================================================

// controladores

function loading(showLoading){
	if(showLoading){
		$.mobile.loading( "show",{});
	}else{
		$.mobile.loading( "hide",{});
	}
}

function geolocalizar(){
	//console.log("geolocalizar");
	if (navigator.geolocation) {
		  var successFunction = function(position){
			  coor_x = position.coords.longitude;
			  coor_y = position.coords.latitude;
			  idEntidad = null;
			  loading(false);
			  cargarCategoria();
		  };
		  var errorFunction = function(){
			  alert("Se ha producido un error al geolocalizar");
			  loading(false);
		  };
		  loading(true);
		  navigator.geolocation.getCurrentPosition(successFunction, errorFunction);
		} else {
		  alert("El navegador utilizado no soporta la geolocalización");
		}
}

function localizar(){
	//console.log("buscar");
	$.mobile.changePage("#localizador");
	cargarProvincias();
}

function establecerLocalizacion(){
	var entidad = $("#municipios").val();
	if(entidad==""){
		alert("Ha de seleccionar las provincias y los municipios");
		return;
	}
	idEntidad = entidad;
	coor_x = null;
	coor_y = null;
	cargarCategoria();
}


function cargarProvincias(){
	 //console.log("cargarProvincias");
	 $("#provincias").html("");
	 loading(true);
	 $.ajax({
		 url: url + "/entidades/categorias",
         type: "GET",
         cache: true,
         dataType: "json",
         success: function(provinciasList){
        	 var i = 0;
        	 var length = provinciasList.length;
        	 var htmlOptions = [];
        	 for(i;i<length;i++){
        		 htmlOptions.push('<option value="' + provinciasList[i].id +'">' + provinciasList[i].name + '</option>');
        	 }
        	 $('#provincias').append(htmlOptions.join('')).selectmenu('refresh');
        	 loading(false);
        	 //cargamos el municipio
        	 cargarMunicipios();

         },
	 	 error: function(){
	 		 alert("Se ha producido un error al obtener las provincias");
	 	 }
       });
}

function cargarMunicipios(){
	//console.log("cargarMunicipios");
	$("#municipios").html("");
	var idProvincia = $("#provincias").val();
	loading(true);
	 $.ajax({
		 url: url + "/entidades/" + idProvincia,
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
        	 $('#municipios').append(htmlOptions.join('')).selectmenu('refresh');
        	 loading(false);
         },
         error: function(){
	 		 alert("Se ha producido un error al obtener los municipios");
	 		 loading(false);
	 	 }
       });
}


function cargarCategoria(cat){
	//console.log("cargarCategoria");
	
	var requestParam = "";
	if(cat != null){
		requestParam = "?id_categoria=" + cat.id;
		pilaCategorias.push(cat);
	
	}else{
		requestParam = "?id_aplicacion=" + aplicacion.id;
	}
	$("#contenidoCategorias").html("");
	$.mobile.changePage("#categorias");
	loading(true);
	$.ajax({
		 url: url + "/categorias" + requestParam ,
	     type: "GET",
	     cache: true,
	     dataType: "json",
	     success: function(categoriasList){
	    	 var htmlElements = [];
	    	 var i = 0;
	    	 var length = categoriasList.length;
    		 for(i;i<length;i++){
        		 if(!categoriasList[i].last){
		    		 htmlElements.push("<li><a href='javascript:cargarCategoria(" + JSON.stringify(categoriasList[i]) + ")'>" +
		    				 "<img width='80px' height:'80px' src='" + url+ "/categorias/" + categoriasList[i].id + "/logo/" + "'/>" +  
		    				 categoriasList[i].name + 
		    		 "</a></li>");
		    	 }else{
		    		 htmlElements.push("<li><a href='javascript:cargarDatos(" + JSON.stringify(categoriasList[i]) + ")'>" + 
		    				 "<img width='80px' height:'80px' src='" + url+ "/categorias/" + categoriasList[i].id + "/logo/" + "'/>" +  
		    				 categoriasList[i].name + 
		    		 "</a></li>");
		    	 }
    		 }
	    	 htmlElements = "<ul id='listaCategorias' data-role='listview'>" + htmlElements.join(" ") + "</ul>";
	    	 $("#contenidoCategorias").append(htmlElements);
	    	 $("#listaCategorias").listview();
	    	 loading(false);
	     },
	 	 error: function(){
	 		 alert("Se ha producido un error al obtener las categorias");
	 		 loading(false);
	 	 }
	   });
}


function cargarDatos(cat){
	//console.log("cargarDatos");
	pilaCategorias.push(cat);
	datos.offset = 0;
	$("#listaDatos").html("");
	$.mobile.changePage("#datos");
	paginarDatos(cat);
}

function paginarDatos(cat){
//JGL12022015 - cambio para poder no usar paginación
	var requestParam = "";
	if(idEntidad!=null){
		showDistance = false; //JGL - en búsquedas por categoría no mostramos la distancia
		requestParam += "?id_entidad=" + idEntidad;
	}else{
		showDistance = true;
		requestParam += "?x=" + coor_x + "&y=" + coor_y;
	}	
	if (datos.limit!=-1){
		requestParam += "&limit=" + datos.limit + "&offset=" + datos.offset;
	}
//JGL12022015	
	loading(true);
	$.ajax({
		 url: url + "/datos/"+ cat.id + requestParam ,
	     type: "GET",
	     cache: true,
	     dataType: "json",
	     success: function(datosList){
	    	 var i = 0;
	    	 var length = datosList.length;
	    	 if(length==0){
	    		 if(datos.offset==0){
	    			 alert("No hay datos para el filtro aplicado");
	    			 loading(false);
	    			 return;
	    		 }else{
	    			 alert("Ya no hay mas datos para este filtro");
	    		 }
	    	 }
	    	 
	    	 //eliminamos el botón de cargando
	    	 if(datos.offset!=0){
	    		 $("#listaDatos li").last().remove();
    			 //$("#listaDatos li").last().css("height");
    			 //$("#listaDatos li").last().css("height","20px");
	    	 }
	    	 
			 var htmlElements = [];
			 for(i;i<length;i++){
				 var liHtml = "<li><a href='javascript:verDato("+ cat.id + "," + JSON.stringify(datosList[i]) + ")'>";
				 liHtml += datosList[i].name;
				 if(showDistance && datosList[i].distance){
					 var distance = datosList[i].distance;
					 if(distance < 1000){
						 distance += " m";
					 }else{
						 //pasamos a km
						 distance = distance/1000;
						 //redondeamos con 1 decimal
						 distance = Math.round(distance * 10) / 10;
						 distance += " km"; 
					 }
					 liHtml += "<span class='ui-li-count'>" + distance + "</span>";
				 }
				 liHtml += "</a></li>";
	    		 htmlElements.push(liHtml);
	    	 }
			if (datos.limit!=-1){//JGL
			 htmlElements.push("<li><a href='javascript:paginarDatos(" +  JSON.stringify(cat) + ")'>" + "Obtener mas datos" + "</a></li>");
			}
		 $("#listaDatos").append(htmlElements);
	    	 $("#listaDatos").listview("refresh");
	    	 
		if (datos.limit!=-1){//JGL
		    	 //estilos para el obtener mas datos
		    	 var botonObtenerMasDatos =  $($("#listaDatos li a").last()[0]);
		    	 botonObtenerMasDatos.removeClass("ui-btn-icon-right");
		    	 botonObtenerMasDatos.removeClass("ui-icon-carat-r");
		    	 botonObtenerMasDatos.css("color","#aaa");
		    	 botonObtenerMasDatos.css("text-align","center");
		}
	    	 
	    	 
	    	 datos.offset += datos.limit;
	    	 loading(false);
	     },
	 	 error: function(){
	 		 alert("Se ha producido un error al obtener los datos");
	 		 loading(false);
	 	 }
	  });
}


//function getTablaDatos(fields){
//	var html = "";
//	var i=0;
//	var length = fields.length;
//	for(i=0;i<length;i++){
//		html += "<li><b>" + fields[i].field +"</b>: " + fields[i].value + "<li>";
//	}
//	if(html!=""){
//		html = "<ul>" + html + "</ul>";
//	}else{
//		html = "No hay ningún elemento a mostrar";
//	}
//	
//	return html;
//}

//OJO: todo esto necesita tener mapea en el mismo servidor => CROSS DOMAIN
popup = null;
var bounds = [];
function verDato(idCategoria,dato){
	loading(true);
	
	//obtenemos la url de mapea con el kml para el dato seleccionado
	var urlKML = getUrlKML(idCategoria,dato.pkValue);
	
	//vamos a la página del mapa y la ocultamos hasta que se cargue el kml
	//var bounds = [];
	if(srcMapeaObjeto.projection != "EPSG:4326"){
		
		var coord1 = proj4(srcMapeaObjeto.projection,[dato.minX,dato.minY]);
		var coord2 = proj4(srcMapeaObjeto.projection,[dato.maxX,dato.maxY]);
		bounds = coord1 + "," + coord2; //[coord1[0],coord1[1],coord2[0],coord2[1]];
		
		
	}else{
		bounds= [dato.minX,dato.minY,dato.maxX,dato.maxY];
	}
	$('#mapea').attr('src', urlKML + "&bbox="+bounds);
	$.mobile.changePage("#mapa");
	fixHeight();
	loading(false);
}



//obtiene la url de mapea con la capa KML anadida
function getUrlKML(idCategoria,idDato){
	var urlKML = null;
	srcMapeaObjeto.parametros.layers = ORIGINAL_LAYERS;
	if(srcMapeaObjeto.parametros.layers == null){
		srcMapeaObjeto.parametros.layers = generarCapaKML(idCategoria,idDato);
	}else{
		if(srcMapeaObjeto.parametros.layers.trim().length==0){
			srcMapeaObjeto.parametros.layers = generarCapaKML(idCategoria,idDato);
		}else{
			srcMapeaObjeto.parametros.layers += "," + generarCapaKML(idCategoria,idDato);
		}
	}
	urlKML = srcMapeaObjeto.url + "?" + decodeURIComponent($.param(srcMapeaObjeto.parametros));
	
	//console.log(urlKML);
	return urlKML;
}


//genera sintaxis para crear una capa KML en mapea
function generarCapaKML(idCategoria,idDato){
	var capaKML = "KML*capaKML*" + url + "/datos/kml/" + idCategoria + "/item/" + idDato + "*true";
	return capaKML;
}

//funcion de entrada
function init(){	
	cargarAplicacion();	
}

function cargarAplicacion(){
	//console.log("cargarAplicacion");
	loading(true);
	$.ajax({
		 url: url + "/application/" + idAplicacion,
	     type: "GET",
	     cache: true,
	     dataType: "json",
	     success: function(app){
	    	 aplicacion = app;
	    	 if(aplicacion.name != null){
	    		 $("#app-name").html(aplicacion.name);
	    	 }
	    	 if(aplicacion.idEntidad == null){
	    		 $("#btn-buscar").hide();
	    	 }else{
	    		 idEntidad = aplicacion.idEntidad;
	    	 }
	    	 if(aplicacion.wmcURL && aplicacion.wmcURL.trim().length>0){
	    		srcMapeaObjeto = obtenerUrlComoObjeto(aplicacion.wmcURL); //JGL - eliminación srcMapea
	    	 }
	    	 $.mobile.changePage("#inicio");
	     },
	     error: function(){
	 		 alert("Se ha producido un error al obtener la aplicación con el id: " + idAplicacion);
	 	 }
	 });
}

function inicio(){
	//console.log("inicio");
	pilaCategorias = [];
	coor_x = null;
	coor_y = null;
	idEntidad = aplicacion.idEntidad;
	datos.offset = 0;
//JGL
	$("#listaDatos").empty(); 
	clearSuggest();
//
	$.mobile.changePage("#inicio");
}

function atras(){
	//console.log("atras");
	if(pilaCategorias.length==0){
		if ($("#txtBusqueda").val().length>0){
			$.mobile.changePage("#busqueda");
		}else{
			inicio();
		}
	}else{
		pilaCategorias.pop();
		var categoria = pilaCategorias.pop();
		cargarCategoria(categoria);
	}
}
function atrasMapa(){ //JGL - cambiado (hay 2 puntos de entrada a mapa)
	if ($("#listaDatos li").length>0){
		$.mobile.changePage("#datos");
	}else if ($("#listSuggest li").length>0){
		$.mobile.changePage("#busqueda");
	}else{
		inicio();
	}
}

//JGL ==================================== Integración de GB y modificaciones ===============================================
$(document).on("pageinit", "#busqueda", function() {
	    $("#txtBusqueda").on("keyup", function () {
	        var $ul = $("#listSuggest"),
	            $input = $(this),
	            value = $input.val(),
	            html = "";
	        $ul.html( "" );
	        if ( value && value.length > 2 ) {
	            $.ajax({
	                url: urlSuggest,
			        dataType: "jsonp",
			        jsonp: 'json.wrf',
			        data: {
			            'spellcheck.q': $input.val(),
			            wt: 'json',
			        },
	            })
	            .then(function (response) {
	            	//console.log(response);
	            	if(response.spellcheck.suggestions.length>0){
		                $.each(response.spellcheck.suggestions[1].suggestion, function ( i, val ) {
		                    html += "<li><a href='javascript:buscarGeobusquedas(\""+val+"\",directResultGB)'>"+val+"</a></li>";
		                });
		                $ul.html(html);
		                $ul.listview("refresh");
		                $ul.trigger("updatelayout");
	            	}else{
				$ul.empty();
			}
	            });
	        }
	    });
});

function buscarGeobusquedas(query, callback){
	loading(true);
	$.ajax({
        url: urlCore,
        dataType: "jsonp",
        jsonp: 'json.wrf',
        data: {
            q: query,
            wt: 'json',
        },
    	success: callback,
	    error: function(){
	 		 alert("Se ha producido un error al realizar la búsqueda");
	 	},
	 	final: function(){loading(false)}
    });    
}

function listarResultadosGB(result){
	datosList = result.response.docs;
	if (datosList!=null){
		var htmlElements = [];
		for(i=0;i<datosList.length;i++){
			 var liHtml = "<li><a href='javascript:verDatoGB(" + JSON.stringify(datosList[i]) + ")'>";
			 liHtml += datosList[i].nombre;
			 liHtml += "</a></li>";
			 htmlElements.push(liHtml);
		 }
		
		 $.mobile.changePage("#datos");
		 $("#listaDatos").html(htmlElements).listview("refresh");
		 
	}
}
	
function directResultGB(result){
	datosList = result.response.docs;
	if (datosList!=null && datosList.length>0){
		$("#listaDatos").empty(); 
		verDatoGB(datosList[0]);
		
	}
}

function clearSuggest(){
	$('#listSuggest').empty(); 
	$('#txtBusqueda').val('');
}

function verDatoGB(dato){
	alert('no implementado en web');
	/*loading(true);
	("#mapea").css("visibility","hidden");
	wkt = OpenLayers.Format.WKT();
	geom = wkt.read(dato.geom).geometry;
	point = null;
	if (geom.components && geom.components.length>0) {
		point = geom.getCentroid();
	}else{
		point = geom;
	}
				
	if(srcMapeaObjeto.projection != "EPSG:25830"){
		point.transform("EPSG:25830", srcMapeaObjeto.projection);
		//console.log(point);
	}
	
	notShow = [ "tHe_geom", "geom", "_version_", "solrid", "keywords" ];
	htmlTable = "<table class=\"mapea-table\"><tbody>";
	$.each(dato, function(k, v) {
		if ($.inArray(k,notShow)==-1){
	        	htmlTable += "<tr><td><b>";
	        	htmlTable += k;
	        	htmlTable += "</b></td><td>";
	        	htmlTable += v;
	        	htmlTable += "</td></tr>";
		}

	});
	htmlTable += "</tbody></table>";
	
	
	strCenter = "&center=" + point.x +","+point.y+"*true&zoom=15&label="+htmltable;
	$('#mapea').attr('src', urlKML + strCenter);
	$.mobile.changePage("#mapa");
	fixHeight();
	loading(false);	*/

}
function fixHeight(){
	$('#mapea').height($(document).height() - $("#mapa-header").height());
}
$(window).resize(function () { fixHeight(); });
