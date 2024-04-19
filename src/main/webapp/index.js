var wsServer = null;
var userInitiatedDisconnect = false;

function connect() {
    if (wsServer !== null) {
        alert('Already connected to Server.');
        return;
    }

    var host = document.location.host;
    var serverUrl = "ws://" + host + "/order-mgmt/websocket";
    wsServer = new WebSocket(serverUrl);
    wsServer.onmessage = function(event) {
        console.log('Received data from Server: ', event.data);
        var data = JSON.parse(event.data);
        updateTable('btcusdt', data.btcusdt.bids, data.btcusdt.asks);
        updateTable('ethusdt', data.ethusdt.bids, data.ethusdt.asks);
    };
    wsServer.onopen = function() {
        updateStatus("Connected to Server!");
    };
    wsServer.onclose = function() {
        updateStatus("Disconnected from Server!");
        wsServer = null;
        if (!userInitiatedDisconnect) {
            setTimeout(connect, 1000); // Attempt to reconnect automatically.
        }
        userInitiatedDisconnect = false;
    };
    wsServer.onerror = function(event) {
        console.error('WebSocket Error from Server: ', event);
        wsServer = null;
    };
}

function disconnect() {
    userInitiatedDisconnect = true;
    if (wsServer) {
        wsServer.close();
        wsServer = null;
    }
    updateStatus("Disconnected!");
}

function updateStatus(message) {
    document.getElementById("webserverStatus").innerHTML = message;
}

function updateTable(symbol, bids, asks) {
    var table = document.getElementById(symbol);
    var tbody = table.getElementsByTagName('tbody')[0];
    tbody.innerHTML = '';

    var totalBidVolume = 0;
    var totalAskVolume = 0;

    bids.forEach(function(entry) {
        var price = parseFloat(entry[0]);
        var quantity = parseFloat(entry[1]);
        var row = '<tr><td>' + price.toFixed(2) + '</td><td>' + quantity.toFixed(2) + '</td></tr>';
        tbody.innerHTML += row;
        totalBidVolume += price * quantity;
    });

    asks.forEach(function(entry) {
        var price = parseFloat(entry[0]);
        var quantity = parseFloat(entry[1]);
        var row = '<tr><td>' + price.toFixed(2) + '</td><td>' + quantity.toFixed(2) + '</td></tr>';
        tbody.innerHTML += row;
        totalAskVolume += price * quantity;
    });

    document.getElementById(symbol + '-total-bids').innerText = "Total Bids: " + totalBidVolume.toFixed(2) + " USDT";
    document.getElementById(symbol + '-total-asks').innerText = "Total Asks: " + totalAskVolume.toFixed(2) + " USDT";
}
