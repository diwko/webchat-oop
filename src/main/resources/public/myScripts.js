var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function(message) { messageParser(message); };
webSocket.onopen = function () { 
	if(document.cookie)
		sendUserName(getCookie("userName"));
};
webSocket.onclose = function () { alert("WebSocket connection closed") };


document.getElementById("nameInput").addEventListener("keypress", function (e) {
    if (e.keyCode === 13 && e.target.value != "") { addUserName(e.target.value); }
});

function addUserName(name) {			
		document.getElementById("nameBox").style.display = "inline";		
		if (name === "")
			return;

		sendUserName(name);
		var expires = new Date();
		expires.setDate(expires.getDate() + 365);
		document.cookie = "userName=" + name + ";" + "expires=" + expires + ";path=/";
		document.getElementById("nameBox").style.display = "none";		
		document.getElementById("chat").style.display = "block";		
}

function sendUserName(name) {
	webSocket.send(createJsonUserName(name));
	setUserData(name);
} 

function setUserData(userName) {
	document.getElementById("userData").innerHTML = userName;
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function createJsonUserName (userName) {	 
	return json = JSON.stringify({
		type: "user",
		name: userName
	});
}


document.getElementById("messageInput").addEventListener("keypress", function (e) {
    if (e.keyCode === 13 && e.target.value != "") {
     	sendMessage(e.target.value, new Date());
   		e.target.value = "";
    }
});

function sendMessage (messageText, messageDate) {
	webSocket.send(createJsonMessage(messageText, messageDate));
}

function sendKeepAliveMessage () {
	webSocket.send(JSON.stringify({
		type: "keepAlive"		
	}));
}

setInterval(sendKeepAliveMessage, 1000);

function createJsonMessage(messageText, messageDate) {
	return json = JSON.stringify({
		type: "message",
		date: messageDate.toString(),
		text: messageText
	});
}

document.getElementById("newChannel").onclick = function(e){
	var newChannel = e.target;
	newChannel.style.display = "none";
	document.getElementById("rightPanel").insertAdjacentHTML("beforeend",
		'<input id="newChannelInput" placeholder="Nazwa kanału" autofocus>');
	var newChannelInput = document.getElementById("newChannelInput");
	newChannelInput.style.display = "block";
	
	newChannelInput.addEventListener("keypress", function (e) {
    	if (e.keyCode === 13 && e.target.value != "") {
     		sendChannel(e.target.value, "new");
	     	e.target.value = "";
	     	e.target.style.display = "none";
	     	newChannel.style.display = "block";
    	}
	});
}

function messageParser(message) {
	var data = JSON.parse(message.data);

	switch(data.type){
		case "message":
			insertMessage(data.user, data.date, data.text);
			break;
		case "messages":
			document.getElementById("messages").innerHTML = "";
			console.log(data.messages);
			for (i in data.messages){
				var mes = JSON.parse(data.messages[i]);
				insertMessage(mes.user, mes.date, mes.text);	
			}
			break;
		case "channel":
			switch(data.action){
				case "new":
					insertNewChannel(data.name, "beforeend");
					break;
				case "follow":
					setFollowedChannel(data.name);
					break;
				case "unfollow":
					setUnfollowedChannel(data.name);
					break;
				default:
			}	
			break;
		case "channels":
			for (i in data.names) 
				insertNewChannel(data.names[i], "beforeend");			
		default:			
	}
}

function insertNewChannel(channelName, place){
	document.getElementById("channels").insertAdjacentHTML(place, createChannelHTML(channelName));
	var channel = document.getElementById("channel_" + channelName);	
	channel.onclick = function (e) {		
		sendChannel(channelName, "follow");
	};
}

function sendChannel(channelName, channelAction){
	webSocket.send(createJsonChannel(channelName, channelAction));
}

function createJsonChannel(channelName, channelAction){
	return json = JSON.stringify({
		type: "channel",
		action: channelAction,
		name: channelName
	});
}

function setFollowedChannel (channelName) {
	var channel = document.getElementById("channel_" + channelName);	
	channel.className = 'followedChannel';	
}

function setUnfollowedChannel (channelName) {
	var channel = document.getElementById("channel_" + channelName);	
	channel.className = 'channel';	
	document.getElementById("messages").innerHTML = '';	
}

function insertMessage(userName, date, text){
	document.getElementById("messages").insertAdjacentHTML("afterbegin", createMessageHTML (userName, date, text));
}

function createMessageHTML (userName, date, text){
	if(getCookie("userName") === userName){
		return '<div class="myMessage">' +
        	   '<p class="userName">' + userName + '</p>' +
        	    '<span class="timestamp">' + date +'</span>' +
        	   '<p class="textMessage">' + text + '</p>' +
           '</div>';
	}
	return '<div class="message">' +
        	   '<p class="userName">' + userName + '</p>' +
        	   '<span class="timestamp">' + date +'</span>' +
        	   '<p class="textMessage">' + text + '</p>' +
           '</div>';	
}

function createChannelHTML (channelName) {
	return '<div class="channel" id="channel_' + channelName + '">' +
				channelName +
			'</div>';
}