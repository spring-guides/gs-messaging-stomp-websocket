var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/chat_messages', function (message) {
            addMessage(JSON.parse(message.body));
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    var message = {
      'name': $("#name").val()
    };

    stompClient.send("/app/hello", {}, JSON.stringify(message));
}

function sendMessage() {
    var message = {
      'emitter': $("#name").val(),
      'content': $('#message').val()
    };

    console.log('Sending message : ' + message);

    stompClient.send("/app/message", {}, JSON.stringify(message));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function addMessage(message) {
    var messageDate = formatDate(new Date(message.emissionDate))
    $("#messages").append("<tr><td>" + message.emitter + ' (' + messageDate + ') ' + ' : ' + message.content + "</td></tr>");
}

function formatDate(date) {
  var mm = date.getMonth() + 1; // getMonth() is zero-based
  var dd = date.getDate();

  return [(dd>9 ? '' : '0') + dd,
          '/',
          (mm>9 ? '' : '0') + mm,
          '/',
          date.getFullYear()
         ].join('');
};

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#sendMessage" ).click(function() { sendMessage(); });
});
