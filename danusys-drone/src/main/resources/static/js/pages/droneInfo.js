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
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/drone', function (drone) {
            console.log(JSON.parse(drone.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function droneLog(log) {
    stompClient.send("/app/drone", {}, JSON.stringify({'droneLog': log}));
}

function showDroneInfo(message) {
    // console.log("### >>> " + message);
    // $("#showDroneInfo").append("<tr><td>" + message + "</td></tr>");
    // $("#showDroneInfo").html(message);

}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#start" ).click(function() { droneLog('start'); });
    $( "#stop" ).click(function() { droneLog('stop'); });
});

