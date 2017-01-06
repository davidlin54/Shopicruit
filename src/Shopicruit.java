import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import sun.misc.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Shopicruit {
	static Map<String, BigDecimal> mRevenueMap;
	final private static String ORDER_URL = "https://shopicruit.myshopify.com/admin/orders";
	final private static String API_KEY = "";
	final private static String PASSWORD = "";
	final private static String AUTH = API_KEY + ':' + PASSWORD;
	final private static String ACCESS_TOKEN = "c32313df0d0ef512ca64d5b336a0d7c6";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mRevenueMap = new HashMap<>();
		try {
			int page = 1;
			while (true) {
		        URL url = new URL(ORDER_URL + ".json?page=" + page + "&access_token=" + ACCESS_TOKEN);
		        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
		        //httpUrlConnection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(AUTH.getBytes()));
		        //httpUrlConnection.setRequestMethod("GET");
		        BufferedReader in = new BufferedReader(
		        		new InputStreamReader(httpUrlConnection.getInputStream()));
		
		        String inputLine;
		        String input = "";
		        while ((inputLine = in.readLine()) != null) {
		            input += inputLine;
		        }
		        
		        JSONObject jsonObject = new JSONObject(input);
		        	
		        JSONArray orders = jsonObject.getJSONArray("orders");
		        if (orders.length() == 0) {
		        	break;
		        }
		        
		        for (int i = 0; i < orders.length(); i++) {
		        	Order order = new Order();
		        	order.setAttributes(orders.getJSONObject(i));
		        	// if order is cancelled, fully refunded, voided, or is a test payment, ignore it
		        	if (!order.test && 
		        			order.cancelled_at == null && 
		        			order.financial_status != Order.FinancialStatus.voided) {
		        		// if order status is paid, pending or approved, just add the total price
			        	if (order.financial_status == Order.FinancialStatus.paid ||
			        			order.financial_status == Order.FinancialStatus.pending ||
			        			order.financial_status == Order.FinancialStatus.authorized) {
			        		BigDecimal newRevenueTotal = mRevenueMap.get(order.currency.toLowerCase());
			        		if (newRevenueTotal == null) {
			        			newRevenueTotal = new BigDecimal(0);
			        		}
			        		newRevenueTotal = newRevenueTotal.add(BigDecimal.valueOf(order.total_price));
			        		
			        		mRevenueMap.put(order.currency.toLowerCase(), newRevenueTotal);
		        		} else {
		        			// otherwise, go through the transactions
		        			processTransactionsByOrderId(order.id);
		        		}
		        	}
		        }
		        
		        in.close();
		        page++;
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		
		// print out revenues by currency
		for (Map.Entry<String, BigDecimal> currency : mRevenueMap.entrySet()) {
			System.out.println(currency.getKey() + ": " + currency.getValue());
		}
	}
	
	
	private static void processTransactionsByOrderId(String orderId) throws IOException, JSONException {
		URL transactionUrl = new URL(ORDER_URL + '/' + orderId + "/transactions.json");
        HttpURLConnection httpUrlConnection = (HttpURLConnection) transactionUrl.openConnection();
        httpUrlConnection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(AUTH.getBytes()));
        httpUrlConnection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
        		new InputStreamReader(httpUrlConnection.getInputStream()));

        String inputLine;
        String input = "";
        while ((inputLine = in.readLine()) != null) {
            input += inputLine;
        }
        
        JSONObject jsonObject = new JSONObject(input);
        	
        JSONArray transactions = jsonObject.getJSONArray("transactions");
        
        for (int i = 0; i < transactions.length(); i++) {
        	Transaction transaction = new Transaction();
        	transaction.setAttributes(transactions.getJSONObject(i));
        	
        	// skip all refunded, void, failed, error and test transactions
        	if (!transaction.test &&
        			transaction.kind != Transaction.Kind.void_kind &&
                	transaction.kind != Transaction.Kind.refund &&
        			transaction.status != Transaction.Status.failure &&
        			transaction.status != Transaction.Status.error) {
        		

        		BigDecimal newRevenueTotal = mRevenueMap.get(transaction.currency.toLowerCase());
        		if (newRevenueTotal == null) {
        			newRevenueTotal = new BigDecimal(0);
        		}
        		newRevenueTotal = newRevenueTotal.add(BigDecimal.valueOf(transaction.amount));
        		
        		mRevenueMap.put(transaction.currency.toLowerCase(), newRevenueTotal);
        	}
        }
	}
}
