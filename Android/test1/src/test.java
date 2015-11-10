

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;


import org.apache.http.HttpResponse;

import org.apache.http.client.ClientProtocolException;

import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.DefaultHttpClient;


public class test {

	/**
	 * @param args
	 * @throws IOException 

	 * @throws ClientProtocolException 
	
 */
	public static void main(String[] args) throws ClientProtocolException, IOException {

		// TODO Auto-generated method stub

		
HttpClient client = new DefaultHttpClient();  
		
String getURL = "http://api.cosm.com/v2/feeds/119454.json?datastreams=Ambient_Light,Analog_Temperature,Digital_Temperature,Digital_Humidity";
		
HttpGet getter = new HttpGet(getURL);
		
getter.setHeader("X-ApiKey", "lE7byNYvcnll598T_1L8NRjLljKSAKx3anFnSWYrUXF6Zz0g");
		
HttpResponse response = client.execute(getter);  

		
StringBuffer textView = new StringBuffer();
		
BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		
String line = "";
		
while ((line = rd.readLine()) != null) {
			
textView.append(line);

		}
		
		
System.out.println(textView.toString());


	}
}
