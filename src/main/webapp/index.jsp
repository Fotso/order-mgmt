<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Real-Time Order Book Viewer</title>
    <link rel="stylesheet" type="text/css" href="index.css">
    <script src="index.js" defer></script>
</head>
<body>
    <h2>Binance WebSocket Updates</h2>
    <p id="webserverStatus">Not Connected</p>
    <button onclick="connect();">Connect</button>
    <button onclick="disconnect();">Disconnect</button>
    <div class="table-container">
        <div class="order-book">
            <caption>BTC/USDT Order Book</caption>
            <table id="btcusdt">
                <thead>
                    <tr>
                        <th>Price</th>
                        <th>Quantity</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
            <p id="btcusdt-total-bids"></p>
            <p id="btcusdt-total-asks"></p>
        </div>
        <div class="order-book">
            <caption>ETH/USDT Order Book</caption>
            <table id="ethusdt">
                <thead>
                    <tr>
                        <th>Price</th>
                        <th>Quantity</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
            <p id="ethusdt-total-bids"></p>
            <p id="ethusdt-total-asks"></p>
        </div>
    </div>
</body>
</html>
