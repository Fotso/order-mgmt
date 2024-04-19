package com.gaudiy.demo.clients;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import com.gaudiy.demo.models.OrderBook;

/**
 * WebSocket endpoint for managing real-time order book updates from Binance for
 * specific trading pairs.
 */
@ServerEndpoint("/websocket")
public class BinanceEndpoint {

	private Session session;
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private OrderBook btcOrderBook = new OrderBook("BTCUSDT");
	private OrderBook ethOrderBook = new OrderBook("ETHUSDT");
	private long btcLastUpdateId = -1;
	private long ethLastUpdateId = -1;

	/**
	 * Callback method when a new WebSocket session is opened. Fetches initial
	 * snapshots for BTCUSDT and ETHUSDT and starts the Binance connection.
	 *
	 * @param session the WebSocket session object
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		System.out.println("Session opened, connection established.");
		fetchInitialSnapshot("BTCUSDT", btcOrderBook);
		fetchInitialSnapshot("ETHUSDT", ethOrderBook);
		startBinanceConnection();
		executor.scheduleAtFixedRate(this::sendOrderBookUpdates, 0, 10, TimeUnit.SECONDS);
	}

	/**
	 * Fetches the initial order book snapshot from Binance API for the specified
	 * symbol.
	 *
	 * @param symbol    the trading symbol for which to fetch the snapshot
	 * @param orderBook the order book to update with the snapshot data
	 */
	private void fetchInitialSnapshot(String symbol, OrderBook orderBook) {
		try {
			URL url = new URL("https://api.binance.com/api/v3/depth?symbol=" + symbol + "&limit=50");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			processInitialSnapshot(new JSONObject(response.toString()), orderBook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the initial snapshot JSON object, updating the specified order
	 * book.
	 *
	 * @param snapshot  the JSON object containing the snapshot data
	 * @param orderBook the order book to update with snapshot data
	 */
	private void processInitialSnapshot(JSONObject snapshot, OrderBook orderBook) {
		JSONArray bids = snapshot.getJSONArray("bids");
		JSONArray asks = snapshot.getJSONArray("asks");
		long lastUpdateId = snapshot.getLong("lastUpdateId");
		orderBook.updateFromSnapshot(bids, true); // true for bids
		orderBook.updateFromSnapshot(asks, false); // false for asks
		if (orderBook.getSymbol().equals("BTCUSDT")) {
			btcLastUpdateId = lastUpdateId;
		} else if (orderBook.getSymbol().equals("ETHUSDT")) {
			ethLastUpdateId = lastUpdateId;
		}
	}

	/**
	 * Starts the WebSocket connection to Binance's stream for both BTCUSDT and
	 * ETHUSDT.
	 */
	private void startBinanceConnection() {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		String binanceStreamUrl = "wss://stream.binance.com:9443/stream?streams=btcusdt@depth/ethusdt@depth";
		try {
			container.connectToServer(new Endpoint() {
				@Override
				public void onOpen(Session session, EndpointConfig config) {
					session.addMessageHandler(String.class, message -> processMessage(message));
				}
			}, ClientEndpointConfig.Builder.create().build(), URI.create(binanceStreamUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes messages received from the Binance stream.
	 *
	 * @param message the raw JSON message string
	 */
	private void processMessage(String message) {
		JSONObject json = new JSONObject(message);
		JSONObject data = json.getJSONObject("data");
		long u = data.getLong("u");
		long U = data.getLong("U");
		String stream = json.getString("stream");

		OrderBook targetBook = stream.contains("btcusdt") ? btcOrderBook : ethOrderBook;
		long lastUpdateId = stream.contains("btcusdt") ? btcLastUpdateId : ethLastUpdateId;

		if (U > lastUpdateId && u >= lastUpdateId + 1) {
			targetBook.update(data.getJSONArray("b"), true);
			targetBook.update(data.getJSONArray("a"), false);
			if (stream.contains("btcusdt")) {
				btcLastUpdateId = u;
			} else if (stream.contains("ethusdt")) {
				ethLastUpdateId = u;
			}
		}
	}

	/**
	 * Sends the current state of both order books (BTCUSDT and ETHUSDT) to the
	 * connected client.
	 */
	private void sendOrderBookUpdates() {
		try {
			JSONObject update = new JSONObject();
			update.put("btcusdt", btcOrderBook.toJSON());
			update.put("ethusdt", ethOrderBook.toJSON());
			if (session != null && session.isOpen()) {
				session.getBasicRemote().sendText(update.toString());
				System.out.println("Order Book Updates Sent: " + update.toString());
			}
		} catch (IOException e) {
			System.err.println("Error sending order book updates: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Callback method when the WebSocket session is closed. Shuts down the executor
	 * service and clears the session.
	 *
	 * @param session the WebSocket session object that was closed
	 */
	@OnClose
	public void onClose(Session session) {
		this.session = null;
		executor.shutdown();
		System.out.println("Session closed and executor shut down.");
	}
}
