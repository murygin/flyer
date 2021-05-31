ns4 = (document.layers)? true:false
ie4 = (document.all)? true:false
ns6 = (document.getElementById&&!document.all)? true:false 



function show(id) {
//alert(show.arguments.length);
		if(id!='events'){
			hide('events')
			}
		if(id!='locations'){
			hide('locations')
			}
		if(id!='magazin'){
			hide('magazin')
			}
		if(id!='ueberuns'){
			hide('ueberuns')
			}
        if (ns4) document.layers[id].visibility = "show"
        else if (ie4) document.all[id].style.visibility = "visible"
		else if (ns6) document.getElementById(id).style.visibility = "visible"
}

function hide(id) {
        if (ns4) document.layers[id].visibility = "hide"
        else if (ie4) document.all[id].style.visibility = "hidden"
		else if (ns6) document.getElementById(id).style.visibility = "hidden"
}
function slow(id){
hide(id);
//fuc="hide(\"" + id + "\")";
	//window.setTimeout(fuc,1);
	}
