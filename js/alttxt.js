// AUTHOR:   Brian Gosselin
//Hi Brian, nice script. thanx.-mrks- 
var mousefollow=true;  
                       
//alert('asd');                       
var dofade=false;       
var center=false;      
var centertext=false;  

var NS4=(navigator.appName.indexOf("Netscape")>=0 && !document.getElementById)? true : false;
var IE4=(document.all && !document.getElementById)? true : false;
var IE5=(document.getElementById && document.all)? true : false;
var NS6=(document.getElementById && navigator.appName.indexOf("Netscape")>=0 )? true: false;
var W3C=(document.getElementById)? true : false;
var w_y, w_x, navtxt, boxheight, boxwidth;
var ishover=false;
var isloaded=false;
var ieop=0;
var op_id=0;
var oktomove=false;

function getwindowdims(){
w_y=(NS4||NS6)? window.innerHeight : (IE5||IE4)? document.body.clientHeight : 0;
w_x=(NS4||NS6)? window.innerWidth : (IE5||IE4)? document.body.clientWidth : 0;
}

function getboxwidth(){
if(NS4)boxwidth=(navtxt.document.width)? navtxt.document.width : navtxt.clip.width;
if(IE5||IE4)boxwidth=(navtxt.style.pixelWidth)? navtxt.style.pixelWidth : navtxt.offsetWidth;
if(NS6)boxwidth=(navtxt.style.width)? parseInt(navtxt.style.width) : parseInt(navtxt.offsetWidth);
}

function getboxheight(){
if(NS4)boxheight=(navtxt.document.height)? navtxt.document.height : navtxt.clip.height;
if(IE4||IE5)boxheight=(navtxt.style.pixelHeight)? navtxt.style.pixelHeight : navtxt.offsetHeight;
if(NS6)boxheight=parseInt(navtxt.offsetHeight);
}

function movenavtxt(x,y){
if(NS4)navtxt.moveTo(x,y);
if(W3C||IE4){
navtxt.style.left=x+'px';
navtxt.style.top=y+'px';
}}

function getpagescrolly(){
if(NS4||NS6)return window.pageYOffset;
if(IE5||IE4)return document.body.scrollTop;
}

function getpagescrollx(){
if(NS4||NS6)return window.pageXOffset;
if(IE5||IE4)return document.body.scrollLeft;
}

function writeindiv(text){
if(NS4){
navtxt.document.open();
navtxt.document.write(text);
navtxt.document.close();
}
if(W3C||IE4)navtxt.innerHTML=text;
}

function writetxt(text){
if(isloaded){
if(text!=0){
oktomove=true;
ishover=true;
if(NS4)text='<div class="navtext">'+((centertext)?'<center>':'')+text+((centertext)?'</center>':'')+'</div>';
writeindiv(text);
getboxheight();
if((W3C || IE4) && dofade){
ieop=0;
incropacity();
}}else{
if(NS4)navtxt.visibility="hide";
if(IE4||W3C){
if(dofade)clearTimeout(op_id);
navtxt.style.visibility="hidden";
}
writeindiv('');
ishover=false;
}}}

function incropacity(){
if(ieop<=100){
ieop+=7;
if(IE4 || IE5)navtxt.style.filter="alpha(opacity="+ieop+")";
if(NS6)navtxt.style.MozOpacity=ieop/100;
op_id=setTimeout('incropacity()', 50);
}}

function moveobj(evt){
if(isloaded && ishover && oktomove){
margin=(IE4||IE5)?5:25;
if(NS6)if(document.height+27-window.innerHeight<0)margin=15;
if(NS4)if(document.height-window.innerHeight<0)margin=10;
mx=(NS4||NS6)? evt.pageX : (IE5||IE4)? event.clientX : 0;
my=(NS4||NS6)? evt.pageY : (IE5||IE4)? event.clientY : 0;
if(NS4||NS6)mx-=getpagescrollx();
if(NS4)my-=getpagescrolly();
xoff=(center)? mx-boxwidth/2 : mx-250;
yoff=(my+boxheight+25-((NS6)?getpagescrolly():0)+margin>=w_y)? -5-boxheight: 0;
movenavtxt( Math.min(w_x-boxwidth-margin , Math.max(2,xoff))+getpagescrollx(), my+yoff+((!NS6)?getpagescrolly():0));
if(NS4)navtxt.visibility="show";
if(W3C||IE4)navtxt.style.visibility="visible";
if(!mousefollow)oktomove=false;
}}

if(NS4)document.captureEvents(Event.MOUSEMOVE);
document.onmousemove=moveobj;
window.onload=function(){
  navtxt=(NS4)? document.layers['navtxt'] : (IE4)? document.all['navtxt'] : (W3C)? document.getElementById('navtxt') : null;
  getboxwidth();
  getboxheight();
  getwindowdims();
  isloaded=true;
  if((W3C || IE4) && centertext)navtxt.style.textAlign="center";
  if(W3C)navtxt.style.padding='5px';
  if(IE4 || IE5 && dofade)navtxt.style.filter="alpha(opacity=0)";
  }
window.onresize=getwindowdims;