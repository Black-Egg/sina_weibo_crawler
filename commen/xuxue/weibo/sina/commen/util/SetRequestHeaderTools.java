package xuxue.weibo.sina.commen.util;

import java.util.Map;
import java.util.Set;

import org.apache.http.HttpRequest;

public class SetRequestHeaderTools {
	
	public static void SetRequestHeader(Map<String,Object> header,HttpRequest request){
		Set<Map.Entry<String, Object>> enset=header.entrySet(); 
		for(Map.Entry<String, Object> en:enset){
			System.out.println(en.getKey());
			request.addHeader(en.getKey(), en.getValue().toString());
		}
	}
	
}
