function popup(){
		urli="/mag/popup/anh.html";
		if(navigator.appName=="Netscape"){
			if(navigator.appVersion.substring(0,1) == "4"){
				window.open(urli,"commercial","width=262,height=147,screenX=220,screenY=0,scrollbars=no,resizable=no");
			}else{
   				window.open(urli,"commercial","width=242,height=127,screenX=220,screenY=0,scrollbars=no,resizable=no");
			}
		}else{
   			window.open(urli,"commercial","width=242,height=127,screenX=220,screenY=0,scrollbars=no,resizable=no");
			}
		}