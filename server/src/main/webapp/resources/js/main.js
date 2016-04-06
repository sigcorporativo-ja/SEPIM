function showProcessWindow(text){
	//$("processWindowTitle"). 
	if (text){
		processWindowText.innerHTML = text;
	}else{
		processWindowText.innerHTML = "Procesando...";
	}
	$("#processWindow").modal({});
}

function hideProcessWindow(){
	$('#processWindow').modal('hide');
}