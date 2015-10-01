$(function () {
    var stompClient = null;
    setConnected(false);

    $('#connect').click(function () {
        var socket = new SockJS('/hello');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/greetings', function (greeting) {
                showGreeting(JSON.parse(greeting.body).content);
            });
        });
    });

    $('#disconnect').click(function(){
        stompClient.disconnect();
        setConnected(false);
        console.log("Disconnected");
    });

    $('#sendName').click(function(){
        var name = $('#name').val();
        stompClient.send('/app/hello', {}, JSON.stringify({ 'name': name }));
    });

});

function setConnected(connected) {
    $('#connect').prop('disabled', connected);
    $('#disconnect').prop('disabled', !connected);
    connected ? $('conversationDiv').show() : $('conversationDiv').hide();
    $('#response').html('');
}

function showGreeting(message) {
    $('#response').append('<p>').append(message).append('</p>');
}