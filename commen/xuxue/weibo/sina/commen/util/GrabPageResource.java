package xuxue.weibo.sina.commen.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;



public class GrabPageResource {
	
	private static final Logger logger = Logger
			.getLogger(GrabPageResource.class);
	
	private HashMap<String,String> cookieMap;
	
	private String cookieString;
	
	private long temp_timestamp = 0;
	
	private boolean flag = true;
	
	private String ss = null;
	
	private String original_cookieString;
	
	private BasicCookieStore cookiestore;
	

	public GrabPageResource(String cookieString){
		cookiestore=new BasicCookieStore();
		
		this.httpClient=HttpClients.custom().setDefaultCookieStore(cookiestore).build();
		cookieMap=new HashMap<String,String>();
		setCookieString(cookieString);
	}
	
	public GrabPageResource(){
		
		cookiestore=new BasicCookieStore();
		this.httpClient=HttpClients.custom().setDefaultCookieStore(cookiestore).build();
		cookieMap=new HashMap<String,String>();
		
	}
	
	public String getCookieString() {
		return cookieString;
	}
	
	
	
	private boolean addCookie(HttpResponse res) throws IOException{
		Header[] header=res.getAllHeaders();
		for(Header h:header){
			if(h.getName().equals("Set-Cookie")){
				System.out.println("set Value="+h.getValue());
				cookieMap.put(getKey(h.getValue()), h.getValue());
				StringBuilder builder=new StringBuilder();
				Set<Map.Entry<String, String>> sets=cookieMap.entrySet();
				for(Map.Entry<String, String> en:sets){
					builder.append(en.getValue());
				}
				setCookieString(builder.toString());
				return true;
			}
		}
		return false;
	}
	
	
	public void setCookieString(String cookieString) {
		String[] k=cookieString.split(";");
		String pkey="";
		for(int i=0;i<k.length;i++){
			String key=null;
			
			try {
				key=getKey(k[i]).trim();
			} catch (Exception e) {
				key=getKey(k[i]);
			}
			
			
			if(key==null){
				cookieMap.put(pkey, cookieMap.get(pkey)+""+k[i]+";");
			}else if(key.equals("path")||key.equals("Path")){
				cookieMap.put(pkey, cookieMap.get(pkey)+";"+k[i]+";");
			}else if(key.equals("domain")){
				cookieMap.put(pkey, cookieMap.get(pkey)+""+k[i]+";");
			}else{
				cookieMap.put(key, k[i]);
				pkey=key;
			}
		}
		
		Set<Map.Entry<String, String>> sets=cookieMap.entrySet();
		
		StringBuilder builder=new StringBuilder();
		for(Map.Entry<String, String> en:sets){
			builder.append(en.getValue());
		}
		System.out.println("set cookie"+cookieString);
		this.cookieString = builder.toString();
	}
	
	private String getKey(String cookie){
		try {
			int index=cookie.indexOf("=");
			String key=cookie.substring(0, index);
			return key;
		} catch (Exception e) {
			return null;
		}
	}




	HttpGet get = null;
	
	private CloseableHttpClient httpClient;

	

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public HttpResponse getHttpResponse(String url, String referer)
			throws Exception {
		HttpGet get=new HttpGet(url);
		
		Map<String, Object> headerMap = new HashMap<String, Object>();

		headerMap
				.put(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)");

		if (url.contains("s.weibo.com")) {
			headerMap.put("Host", "s.weibo.com");
		} else if (url.contains("e.weibo.com")) {
			headerMap.put("Host", "e.weibo.com");
		} else if (url.contains("media.weibo.com")) {
			headerMap.put("Host", "media.weibo.com");
		} else if (url.contains("huati.weibo.com")) {
			headerMap.put("Host", "huati.weibo.com");
			headerMap.put("X-Requested-With", "XMLHttpRequest");
			headerMap.put("Content-Type", "application/x-www-form-urlencoded");
		} else {
			headerMap.put("Host", "www.weibo.com");
		}
		headerMap.put("Cookie", cookieString);
		headerMap.put("Connection", "Keep-Alive");
		headerMap.put("Accept", "text/html, application/xhtml+xml, */*");

		if (referer != null) {
			headerMap.put("Referer", referer);
		}
		SetRequestHeaderTools.SetRequestHeader(headerMap, get);
		return httpClient.execute(get);
	}

	public HttpResponse getHttpResponse(String url, String referer,
			boolean isAjax) throws Exception {

		
		HttpGet get=new HttpGet(url);

		Map<String, Object> headerMap = new HashMap<String, Object>();

	
		headerMap
				.put(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)");

		if (url.contains("s.weibo.com")) {
			headerMap.put("Host", "s.weibo.com");
		} else if (url.contains("e.weibo.com")) {
			headerMap.put("Host", "e.weibo.com");
		} else if (url.contains("media.weibo.com")) {
			headerMap.put("Host", "media.weibo.com");
		} else if (url.contains("huati.weibo.com")) {
			headerMap.put("Host", "huati.weibo.com");
			headerMap.put("X-Requested-With", "XMLHttpRequest");
			headerMap.put("Content-Type", "application/x-www-form-urlencoded");
		} else {
			headerMap.put("Host", "www.weibo.com");
		}

		if (isAjax) {
			get.setHeader("X-Requested-With", "XMLHttpRequest");
		}

		headerMap.put("Cookie", cookieString);
		headerMap.put("Connection", "Keep-Alive");
		headerMap.put("Accept", "text/html, application/xhtml+xml, */*");

		if (referer != null) {
			headerMap.put("Referer", referer);
		}
		SetRequestHeaderTools.SetRequestHeader(headerMap, get);
		return httpClient.execute(get);
	}

	
	
	public String postPageSourceOfSina(String url,HashMap<String,String> data) throws ClientProtocolException, IOException{
		String ss=null;
		if(flag){

			InputStream urlStream = null;
			try {
				CloseableHttpResponse response = ajaxPost(data, url);
				boolean set=addCookie(response);
				urlStream = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(urlStream, "UTF-8"));
				String line;
				StringBuilder pageBuffer = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					pageBuffer.append(line);
				}
				ss = pageBuffer.toString();
				
				if(set){
					cookiestore.clear();
					System.out.println("clear");
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				throw e;
			} catch (IOException e) {
				logger.error("");
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (urlStream != null) {
					try {
						urlStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		
		}
		return ss;
	}
	

	public String getPageSourceOfSina(String url, String referer)
			throws SocketTimeoutException {
		ss = null;
		if (flag) {
			InputStream urlStream = null;
			try {
				HttpResponse response = getHttpResponse(url, referer);
				boolean set=addCookie(response);
				urlStream = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(urlStream, "UTF-8"));
				String line;
				StringBuilder pageBuffer = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					pageBuffer.append(line);
				}
				ss = pageBuffer.toString();
				
				if(set){
					cookiestore.clear();
					System.out.println("clear");
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				throw e;
			} catch (IOException e) {
				logger.error("");
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (urlStream != null) {
					try {
						urlStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return ss;
	}

	public String getPageSourceOfSina(String url, String referer, boolean isAjax)
			throws GetPageException {
		ss = null;
		if (flag) {
			InputStream urlStream = null;
			try {
				HttpResponse response = getHttpResponse(url, referer, isAjax);
				addCookie(response);
				urlStream = response.getEntity().getContent();
			
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(urlStream, "UTF-8"));
				String line;
				
				StringBuilder pageBuffer = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					pageBuffer.append(line);
				}
				ss = pageBuffer.toString();
				boolean set=addCookie(response);
				
				if(set){
					cookiestore.clear();
					System.out.println("remove cookie");
				}
			} catch (Exception e) {
				// e.printStackTrace();
				throw new GetPageException(e);
				// return null;
			} finally {
				if (urlStream != null) {
					try {
						urlStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return ss;
	}

	private boolean validate(String content) {
		if (content
				.contains("location.replace(\"http://weibo.com/aj/mblog/mbloglist?")
				&& content.contains("&retcode=6102\");")) {
			
			return false;
		}
		if (content.contains("location.replace(\"http://weibo.com/")
				&& content.contains("follow?retcode=6102\");")) {
			return false;
		}
		if (content.contains("location.replace(\"http://weibo.com/")
				&& content.contains("&retcode=6102\");")) {
			
			return false;
		}
		if (content.contains("location.replace(\"http://s.weibo.com/")
				&& content.contains("?retcode=6102\");")) {
			
			return false;
		}

		if (content.contains("/info/?retcode=6102")) {
			
			return false;
		}
		if (content.contains("http://i.sso.sina.com.cn/js/ssologin.js")) {
			
			return false;
		}
		if (content.contains("/weibo.com/sso/login.php")
				&& content.contains("&retcode=0")) {
			return false;
		}
		return true;
	}



	public static String dumpGZIP(HttpEntity entity) {
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			br = new BufferedReader(new InputStreamReader(entity.getContent(),
					"GBK"));
			sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}


	public void download(String url, String fileFullName, String searchUrl) {
			
			HttpGet downFileGetMethod = new HttpGet(url);
			
			downFileGetMethod.setHeader("Host", "s.weibo.com");
			downFileGetMethod.setHeader("Cookie", cookieString);
			downFileGetMethod.setHeader("Connection", "Keep-Alive");
			downFileGetMethod.setHeader("Accept",
					"image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
			downFileGetMethod.setHeader("Referer", searchUrl);
			downFileGetMethod.setHeader("Accept-Encoding", "gzip, deflate");
			downFileGetMethod.setHeader("User-Agent",
					"Mozilla/5.0 (compatible; MSIE " + "9.0"
							+ "; Windows NT 6.1; WOW64; Trident/6.0)");
			
			try {
				CloseableHttpResponse response = httpClient.execute(
						downFileGetMethod);

				String temp = "";
				org.apache.http.Header[] h = response.getAllHeaders();
				for (int i = 0; i < h.length; i++) {
					// System.out.println("header[" + i + "]------" + h[i]);
					if (h[i].getName().contains("Set-Cookie")) {
						temp = h[i].getValue();
					}
				}
				this.cookieString = this.original_cookieString + ";" + temp + ";";

				byte[] buffer = new byte[1024 * 8];
				int read;
				File localFile = new File(fileFullName);
				localFile = localFile.getParentFile();
				if (localFile != null) {
					localFile.mkdirs();
				}
				BufferedInputStream bin = new BufferedInputStream(response
						.getEntity().getContent());
				BufferedOutputStream bout = new BufferedOutputStream(
						new FileOutputStream(fileFullName));
				while ((read = bin.read(buffer)) > -1) {
					bout.write(buffer, 0, read);
				}
				bout.flush();
				bout.close();
				response.close();
			} catch (Exception e) {
				
				System.out.println("" + e.getLocalizedMessage());
				e.printStackTrace();
			} finally {
				downFileGetMethod.abort();
			}
		}


	
	public String getVerifyResult(String verifyCode, String searchURL) {
			HttpPost post = new HttpPost(
					"http://s.weibo.com/ajax/pincode/verified?__rnd="
							+ DateUtil.getLongByDate());
			
			post.setHeader("Accept", "*/*");
			post.setHeader("Accept-Language", "zh-CN");
			post.setHeader("Cache-Control", "no-cache");
			post.setHeader("Connection", "Keep-Alive");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Accept-Encoding", "gzip, deflate");
			post.setHeader("Referer", searchURL);
			post.setHeader("x-requested-with", "XMLHttpRequest");
			post.setHeader("Host", "s.weibo.com");
			post.setHeader("Cookie", cookieString);
			post.setHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE " + "9.0"
					+ "; Windows NT 6.1; WOW64; Trident/6.0)");
			

			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("_t", "0"));
			qparams.add(new BasicNameValuePair("pageid", "weibo"));
			qparams.add(new BasicNameValuePair("secode", verifyCode));
			qparams.add(new BasicNameValuePair("type", "sass"));

			UrlEncodedFormEntity params = null;
			try {
				params = new UrlEncodedFormEntity(qparams, "UTF-8");
				post.setEntity(params);
				CloseableHttpResponse response = httpClient.execute(post);
				HttpEntity entity = response.getEntity();
				String content = dumpGZIP(entity);
				// System.out.println("keyword search content----" + content);
				return content;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}


	
	public CloseableHttpResponse ajaxPost(HashMap<String,String> data,String url) throws ClientProtocolException, IOException{
		HttpPost post=new HttpPost(url);
		GetUserCookie.setPostData(post, data);
		post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		post.addHeader("Connection", "close");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		post.addHeader("Cookie", cookieString);
		post.addHeader("Host","weibo.com");
		post.addHeader("Referer",url);
		post.addHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE " + "9.0"
					+ "; Windows NT 6.1; WOW64; Trident/6.0)");
		post.addHeader("X-Requested-With", "XMLHttpRequest");
		return httpClient.execute(post);
	}
		
	public static void main(String[] args) throws Exception {
	
	}
}
