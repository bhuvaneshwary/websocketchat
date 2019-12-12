
var stompClient = null;
$(document).ready(function(){
	
	//Disable default behavior of chat form submission
	$("form").on("submit",function(e){
		e.preventDefault();
	})
	
	//Toggle the list of online users
	$("#onlineusers").hide();
	
	$("button").on("click",function(){
		$("#onlineusers").toggle();
	})
		
	// Function clears the input typed in the chat window
	$("#clear").on("click",function(){
		$("#chatmessage").val("");
	})
	
	//Handle logout
	$("#logout").on("submit", onLogout)
	
	// Open a WebSocket connection
	var socket = new SockJS("/ws-chat");
	stompClient= Stomp.over(socket);
	stompClient.connect({},onConnected);
	
	$("#chatform").on("submit",onSend);
	
	
	//Function is executed when the /chat route loads and a WebSocket connection is created.
	function onConnected(){
		stompClient.subscribe("/topic/public",onMessageReceived);
		stompClient.send('/app/chat.register',{},JSON.stringify({sender:$("#username").text(),type:'JOIN'}));
	}
	
	//Function sends the chat message to the server
	function onSend(){
		var chatMessage = {
				sender:$("#username").text(),
				content: $("#chatmessage").val(),
				type:'CHAT'
		};
		$("#chatmessage").val('');
		stompClient.send('/app/chat.send',{},JSON.stringify(chatMessage));	
	}
	
	
	
	// Function handles the messages received from the broker
	function onMessageReceived(message){
		 var message = JSON.parse(message.body);
		 var userList="";
		 for(i=0;i<message.onlineUsers.length;i++){
			 userList += "<p>" + message.onlineUsers[i] + "</p>";
		 }
		 console.log(message);
		 if(message.type == 'JOIN'){
			 message.content = message.sender + ' joined';
			 $("#usercount").text(message.onlineUsers.length);
			 $("#onlineusers").html(userList);
			console.log(userList);
			 $("#chatwall").append("<p class='text-warning rounded mt-1'>" + message.content + "</p>");
		 } else if(message.type == 'CHAT'){
			 $("#chatwall").append("<p class='bg-info rounded mt-1 p-2'>[" + message.sender +"]: " + message.content + "</p>");
		 } else if (message.type == 'LEAVE'){
			 $("#usercount").text(message.onlineUsers.length);
			 $("#onlineusers").html(userList);
			 $("#chatwall").append("<p class='text-light rounded mt-1'>" + message.content + "</p>");
		 }
		
	}
	
	
	//Function handles the logout function
	function onLogout(){
		stompClient.send('/app/chat.leave',{},JSON.stringify({sender:$("#username").text(),type:'LEAVE'}));
		stompClient.disconnect()
		
		$.ajax({
			type:'POST',
			url:$("#logout").attr('action'),
			success:function(){
				window.location.href="/"
			}
		})
		
	}
	
	
	
	
	
})