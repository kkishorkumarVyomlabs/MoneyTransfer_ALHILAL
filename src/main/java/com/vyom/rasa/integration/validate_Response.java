package com.vyom.rasa.integration;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class validate_Response{
	private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.04";
	// GET action URL, query string appended to the URL as ?stype=models
	private final String urlRaiseRequest = "http://192.168.1.194:8888/aeengine/rest/execute";

	// POST action URL
	private final static String urlPOST = "http://10.41.16.111:8080/parse";

	// Post data or a payload

	public String inputLine=null;
	// Main class
	public String check_response(String msg) throws Exception
	{

		// POST example URL
		URL obj = new URL(urlPOST);

		// Send post request
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Basic reuqest header to simulate a browser request
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Upgrade-Insecure-Requests", "1");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		String postDataBody = "{\"q\":\""+msg+"\"}";
		// Payload
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		System.out.println("Sent body="+postDataBody);
		// POST data added to the request as a part of body
		wr.writeBytes(postDataBody);
		wr.flush();
		wr.close();

		// Reading the HTML output of the POST HTTP request
		//int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String str = "";
		while ((inputLine = in.readLine()) != null)
		{
			str=str+inputLine;
		}
		System.out.println("Json code="+str);
		JSONObject entities = null;
		JSONArray user, pwd;
		String amount = null;
		String payee =null;
		try
		{
			entities = new JSONObject(new JSONObject(str).get("entities").toString().replace("[", "").replace("]", ""));
			System.out.println("Entities="+entities);
			user = new JSONArray(new JSONObject(str).get("entities").toString());
			amount = new JSONObject(user.get(0).toString()).get("value").toString();
			pwd = new JSONArray(new JSONObject(str).get("entities").toString());
			payee = new JSONObject(pwd.get(1).toString()).get("value").toString();
		}
		catch (Exception e) {
		}
		System.out.println("amount:"+amount);
		System.out.println("Payee:"+payee);
		String temp = "amount:"+amount +" payee:"+payee;
		JSONObject intent = new JSONObject(str).getJSONObject("intent");
		//String name = new JSONObject(str).getString("text");
		System.out.println("confidence"+intent.get("confidence"));
		System.out.println("name="+intent.get("name"));
		in.close();
		try
		{
            Integer.valueOf((String)amount);
            temp = "amount:"+amount;
		}catch(Exception e){

			if(amount == null && payee == null)
			{
				return intent.get("name").toString();

			}else if(payee == null){
				return "payee:"+amount;
			}
		}
		return temp;

	}

	public static void main(String[] args ) throws Exception
	{
		System.out.println("Rasa Responce="+new validate_Response().check_response("i want to transfer money to mom"));
		String msg =new validate_Response().check_response("i want to transfer money to mom");
		String payee[]=msg.split(":");
		System.out.println(payee[payee.length-1]);
	}
}

