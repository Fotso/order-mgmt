package com.gaudiy.demo.models;

import java.math.BigDecimal;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents the order book for a specific trading symbol with separate
 * structures for bids and asks. This class is thread-safe and synchronizes
 * access to bids and asks.
 */
public class OrderBook {

	private final String symbol;
	private final NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Collections.reverseOrder());
	private final NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>();

	/**
	 * Constructs an OrderBook for the given trading symbol.
	 *
	 * @param symbol the trading symbol for this order book, e.g., "BTCUSDT"
	 */
	public OrderBook(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Returns the trading symbol associated with this order book.
	 *
	 * @return the trading symbol as a String
	 */
	public String getSymbol() {
		return this.symbol;
	}

	/**
	 * Updates the order book from a snapshot, either for bids or asks, replacing
	 * any existing data.
	 *
	 * @param entries an array of JSON arrays where each JSON array contains two
	 *                strings: the price and the quantity
	 * @param isBids  true if the updates are for bids, false for asks
	 */
	public synchronized void updateFromSnapshot(JSONArray entries, boolean isBids) {
		NavigableMap<BigDecimal, BigDecimal> book = isBids ? bids : asks;
		book.clear();
		for (int i = 0; i < entries.length(); i++) {
			JSONArray entry = entries.getJSONArray(i);
			BigDecimal price = new BigDecimal(entry.getString(0));
			BigDecimal quantity = new BigDecimal(entry.getString(1));
			book.put(price, quantity);
		}
	}

	/**
	 * Updates the order book with new bid or ask data. If the quantity of an update
	 * is zero, the price level is removed; otherwise, it updates or adds the price
	 * level with the new quantity.
	 *
	 * @param updates an array of JSON arrays containing price and quantity as
	 *                strings.
	 * @param isBids  true if the updates are for bids, false for asks
	 */
	public synchronized void update(JSONArray updates, boolean isBids) {
		NavigableMap<BigDecimal, BigDecimal> book = isBids ? bids : asks;

		for (int i = 0; i < updates.length(); i++) {
			JSONArray update = updates.getJSONArray(i);
			BigDecimal price = new BigDecimal(update.getString(0));
			BigDecimal quantity = new BigDecimal(update.getString(1));

			if (quantity.compareTo(BigDecimal.ZERO) == 0) {
				book.remove(price);
				continue;
			}
			book.put(price, quantity);
		}
	}

	/**
	 * Converts the current state of the order book into a JSON object containing
	 * two keys: "asks" and "bids", each associated with an array of [price,
	 * quantity] pairs.
	 *
	 * @return a JSONObject representing the current state of the order book
	 */
	public synchronized JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("asks", mapToJSON(asks));
		json.put("bids", mapToJSON(bids));
		return json;
	}

	/**
	 * Helper method to convert a NavigableMap of prices and quantities into a
	 * JSONArray of JSON arrays where each JSON array represents a price level and
	 * contains two elements: the price and the quantity, both as strings.
	 *
	 * @param book the NavigableMap to convert
	 * @return a JSONArray representing the price levels
	 */
	private JSONArray mapToJSON(NavigableMap<BigDecimal, BigDecimal> book) {
		JSONArray array = new JSONArray();
		for (Map.Entry<BigDecimal, BigDecimal> entry : book.entrySet()) {
			JSONArray item = new JSONArray();
			item.put(entry.getKey().toString());
			item.put(entry.getValue().toString());
			array.put(item);
		}
		return array;
	}
}
