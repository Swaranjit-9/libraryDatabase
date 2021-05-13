function verify() {
	
	document.getElementById("taken").style.display="none";
	var password = document.forms['form']['password'].value;
	var userName = document.forms['form']['userName'].value;
	if (password == null || password == "" || 
		userName == null || userName == "") {
		
		document.getElementById("error").innerHTML 
			= "User name and password are required";
		return false;
	}

	return okay;
}