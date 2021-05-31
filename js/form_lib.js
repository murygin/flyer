var n,ie;

function checkBrowser() {
	n = (document.layers) ? 1:0
	ie = (document.all) ? 1:0	
}

function getForm( winCurr, sFormName ) {
	if( n==null || ie==null )
		checkBrowser();
	if( winCurr.document.forms[ sFormName ] ) {
		return winCurr.document.forms[ sFormName ];
	}
	if( n ) {
		for( i=0; i<winCurr.document.layers.length; i++ ) { 
			if( winCurr.document.layers[i].document.forms[ sFormName ] ) {
 				return winCurr.document.layers[i].document.forms[ sFormName ];
 			}	 
 		} //end for
	} //end if( n )
	return null;
}

function getFormElement( winCurr, sFormName, sElementName ) {
	if( getForm( winCurr, sFormName )==null )
		return null;
	if( eval( "getForm( winCurr, sFormName )." + sElementName ) )
		return ( eval( "getForm( winCurr, sFormName )." + sElementName ) );
	else
		return null; 	
}

function getSelectedCmbValue( winCurr, sFormName, sCmbName ) {
	if( getFormElement( winCurr, sFormName, sCmbName ) )
		return( getFormElement( winCurr, sFormName, sCmbName ).options[getFormElement( winCurr, sFormName, sCmbName ).options.selectedIndex].value );
	else
		return null;
}

function getSelectedCmbText( winCurr, sFormName, sCmbName ) {
	if( getFormElement( winCurr, sFormName, sCmbName ) )
		return( getFormElement( winCurr, sFormName, sCmbName ).options[getFormElement( winCurr, sFormName, sCmbName ).options.selectedIndex].text );
	else
		return null;
}

function selectCmbByValue( winCurr, sFormName, sCmbName, sValue ) {
	var cmb=getFormElement( this, sFormName, sCmbName );
	if( cmb!=null ) {
		for( i=0; i<cmb.options.length; i++ ) {
			if( cmb.options[i].value==sValue )
				cmb.options[i].selected=true;	 
		}
	}
}

function selectCmbByText( winCurr, sFormName, sCmbName, sText ) {
	var cmb=getFormElement( this, sFormName, sCmbName );
	if( cmb!=null ) {
		for( i=0; i<cmb.options.length; i++ ) {
			if( cmb.options[i].text==sText )
				cmb.options[i].selected=true;	
		}
	}
}

function changeCmbEntry( winCurr, sFormName, sCmbName, sText, sNewText ) {
	var cmb=getFormElement( this, sFormName, sCmbName );
	if( cmb!=null ) {
		for( i=0; i<cmb.options.length; i++ ) {
			if( cmb.options[i].text==sText )
				cmb.options[i].text=sNewText;	
		}
	}
}

function getSelectedCbValue( winCurr, sFormName, sCbName ) {
	var cb=getFormElement( this, sFormName, sCbName );
	if( cb!=null ) {
		var aValue = new Array();
		if( cb.value!=null ) {
			//checkbox mit einem eintrag
			if( cb.checked )
				aValue[aValue.length]=cb.value;
		}
		else {
			//checkbox mit mehreren eintraegen	
			for( i=0; i<cb.length; i++ ) {
		 		if( cb[i].checked ) {
		 			aValue[aValue.length]=cb[i].value;
		 		}
			}
		}
		return aValue;
	}
	else
		return null;
}

function getSelectedRbValue( winCurr, sFormName, sRbName ) {
 	var aValue=getSelectedCbValue( winCurr, sFormName, sRbName );
 	if( aValue!=null ) {
 		if( aValue.length>0 ) {
 			return aValue[0];
 		}
 		else
 			return null;
 	}
 	else
 		return null;
}