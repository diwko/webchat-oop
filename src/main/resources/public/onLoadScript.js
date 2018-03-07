function checkCookiesExist() {
	if(!document.cookie){		
		document.getElementById("nameBox").style.display = "block";
	}
	else
		document.getElementById("chat").style.display = "block";
}