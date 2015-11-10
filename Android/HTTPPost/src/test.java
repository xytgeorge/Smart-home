import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.*;


public class test {
	  public static void main(String[] args) {
		 test a = new test();
		 
			System.out.println(a.HttpPostData());
		
	  }
	  public String HttpPostData() {
		  HttpEntity rev = null;
		  
		  try {
			  
			  HttpClient httpclient = new DefaultHttpClient();
				
			    String uri = "http://api.cosm.com/v2/feeds/119454.json";
			  
			    HttpPut httpput = new HttpPut(uri);
			 
			    httpput.addHeader("X-ApiKey", "tyo6j7R2afMSlSm6dTpppVAotXSSAKxYL0gvMEF4Y2pDQT0g");
			   
			    //httppost.addHeader("Content-Length:", "130");
			    
			    String a = "{\"version\":\"1.0.0\",\"datastreams\":[{\"id\":\"1\",\"current_value\":\"200\"}]}";
			    
			    httpput.setEntity(new StringEntity(a));
			    
			    HttpResponse response;
			   
			    response = httpclient.execute(httpput);
			    
			    int code = response.getStatusLine().getStatusCode();
			    System.out.println(code);
			      if (code == 200) {
			    	  
			          rev = response.getEntity();      
			          
			          System.out.println(rev.toString());
			      }
		   
		  
		      } catch (ClientProtocolException e) {  
		    	  System.out.println(e);
		      } catch (IOException e) {  
		    	  System.out.println(e);
		     } catch (Exception e) {
		    	 System.out.println(e);
		      }
		return rev.toString();
		  
		  }

}
