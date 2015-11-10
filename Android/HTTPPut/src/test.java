import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * JS数据交换格式
 * @version v0.1
 * @author longHP
 */
public class test {
    /**
     * 获取js数据
     * @param 元数据map
     * @return js数据
     */
    public synchronized static String getJsData(Object obj) {
        return createJsData(obj);
    }
    
    /**
     * 生成js数据
     * @param 元数据map
     * @return js数据
     */
    @SuppressWarnings("unchecked")
    public static String createJsData(Object obj) {
        if (null == obj) {
            return "\"\"";
        }
        StringBuffer resultStr = new StringBuffer();
        if (obj instanceof Collection) {    // 集合List、Set等类型
            resultStr.append("[");
            Collection collection = (Collection) obj;
            if (collection.size() > 0) {
                Object[] collectionObj = collection.toArray();
                for (int i = 0; i < collectionObj.length; i++) {
                    resultStr.append(createJsData(collectionObj[i]) + ",");
                }
                resultStr.deleteCharAt(resultStr.lastIndexOf(","));
            }
            resultStr.append("]");
        } else if (obj instanceof Map) {    // Map类型
            resultStr.append("{");
            Map map = (Map) obj;
            if (map.size() > 0) {
                Iterator<Entry> iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = iter.next();
                    String key = entry.getKey().toString();
                    resultStr.append("\"" + key + "\":");    // jquery1.4以上要求双引号包裹
                    Object tempObj = entry.getValue();
                    resultStr.append(createJsData(tempObj) + ",");
                }
                resultStr.deleteCharAt(resultStr.lastIndexOf(","));
            }
            resultStr.append("}");
        } else {    // 基本类型
            try {
                int arrLength = Array.getLength(obj);
                resultStr.append("[");
                if (arrLength > 0) {
                    for (int i = 0; i < arrLength; i++) {
                        resultStr.append(createJsData(Array.get(obj, i)) + ",");
                    }
                    resultStr.deleteCharAt(resultStr.lastIndexOf(","));
                }
                resultStr.append("]");
            } catch (IllegalArgumentException e) {    // 不是数组，是最基本的数据
                resultStr.append("\"" + stringToJson(obj + "") + "\"");
            }
        }
        return resultStr.toString();
    }
    
    /**
     * 处理特殊字符
     * @param json字符串
     * @return 处理过的字符串
     */
    public static String stringToJson(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
//        String obj = null;
//        if (Arrays.asList(obj).get(0) == null) {
//            System.out.println(Arrays.asList(obj).get(0));
//        }
//        String str = "fds";
//        java.lang.reflect.Array.getLength(str);
        Map<String, Object> map = new HashMap<String, Object>();
        
        Map childMap1 = new HashMap();
        childMap1.put("id", 1);
        childMap1.put("current_value", 100);
        
        Map childMap2 = new HashMap();
        childMap2.put("id", 2);
        childMap2.put("current_value", 200);
        
        Map childMap3 = new HashMap();
        childMap3.put("id", 3);
        childMap3.put("current_value", 300);
        
        List childList = new ArrayList();
        childList.add(childMap1);
        childList.add(childMap2);
        childList.add(childMap3);
        
        map.put("datastreams", childList);
        map.put("version", "1.0.0");
        
        System.out.println(test.getJsData(map));
        
        try { 
        HttpClient httpclient = new DefaultHttpClient();
		
	    String uri = "http://api.cosm.com/v2/feeds/119454.json";
	  
	    HttpPut httpput = new HttpPut(uri);
	 
	    httpput.addHeader("X-ApiKey", "tyo6j7R2afMSlSm6dTpppVAotXSSAKxYL0gvMEF4Y2pDQT0g");
	   
	    //httppost.addHeader("Content-Length:", "130");
	    
	    httpput.setEntity(new StringEntity(test.getJsData(map)));
	    
	    HttpResponse response;
	   
	    response = httpclient.execute(httpput);
	    HttpEntity rev = null;
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
        
    }
}