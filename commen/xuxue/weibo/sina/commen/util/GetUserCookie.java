package xuxue.weibo.sina.commen.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;





public class GetUserCookie {
	
	private Logger logger=Logger.getLogger(GetUserCookie.class);
	
	private HashMap<String,String> cookies;
	
	private CloseableHttpClient httpclient;
	
	private String userName;
	
	private String passwd;
	
	private ServerMessage serverMessage;
	
	private String cookies_sus;
	
	public static String UUG = "";
	
	public GetUserCookie(){
		cookies=new HashMap<String,String>();
		httpclient=HttpClients.createDefault();
	}
	
	public HashMap<String,String> getCookie(String name,String passwd) throws ClientProtocolException, IOException{
		this.userName=name;
		this.passwd=passwd;
		initMessage();
		System.out.println(serverMessage);
		postServer();
		return cookies;
	}
	
	public String getCookieString(String name,String passwd) throws ClientProtocolException, IOException{
		getCookie(name, passwd);
		String cookie="";
		
		Set<Map.Entry<String, String>> sets=cookies.entrySet();
		for(Map.Entry<String, String> en:sets){
			cookie+=en.getKey()+"="+en.getValue()+";";
		}
		
		return cookie;
	}
	
	
	
	private void postServer() throws ClientProtocolException, IOException{
		HttpPost post=new HttpPost("http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)");
		RequestConfig requestConfig=RequestConfig.custom().setRedirectsEnabled(true).setConnectTimeout(5000).build();
		post.setConfig(requestConfig);
		Map<String, Object> headerMap = getPostHeader();		
		Map<String, String> formNameValueMap = getFirstPostData();
		setPostData(post, formNameValueMap);
		setHeaderMap(post, headerMap);
		CloseableHttpResponse response = httpclient.execute(post);

		HttpEntity entity = response.getEntity();
				// 取得登陆第一次正文
		String content = dump(entity, "GBK");
				// System.out.println("第一次取得的content---" + content);
		/**
		 * 取出跳转的url 对该content取得其跳转时的链接，
		 * 设置相应的cookie从而取得其跳转后的cookies作为最后的cookie
		 */
		String location = getRedirectLocation(content);

		// 取得第一次时的cookie,最后与第二次获得的cookie去做与操作
		if (location.contains("retcode=0")) {
			addCookie(response);
		}


		HttpGet get=new HttpGet(location);
		
		RequestConfig config=RequestConfig.custom().setRedirectsEnabled(false).setConnectionRequestTimeout(5000).build();
		
		get.setConfig(config);
		
		response = httpclient.execute(get);
		
		entity = response.getEntity();
		content = dump(entity, "gbk");

		if (location.contains("retcode=0")) {
			addCookie(response);
		} else if (location.contains("retcode=4049")) {
			logger.info(userName + "-->异地登陆失败,需要验证码!");
			
		} else if (location.contains("retcode=5")) {
			logger.info(userName + "-->异地登陆失败,用户名不存在!");
			
		} else if (location.contains("retcode=2070")) {
			logger.info(userName + "-->异地登陆失败,验证码输入错误!");
			
		} else {
			logger.info(userName + "-->登陆失败,用户名或密码错误!");
		}
		
		System.out.println(content);
	}
	
	/**
	 * 判断这个字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}
	
	//http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su=cWlqaXVjdW54NzU5MCU0MDE2My5jb20%3D&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.18)&_=1458569243671
	
	/**
	 *初始化登录 从服务端得到登录的一些信息 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void initMessage() throws ClientProtocolException, IOException{
		serverMessage=new ServerMessage();
		HttpGet get=new HttpGet(
				"http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su="+
		getUserNameEncode()+"&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.18)&_="+new Date().getTime());
		CloseableHttpResponse res=httpclient.execute(get);
		String content=dump(res.getEntity(), "utf-8");
		AnsjPaser parse=new AnsjPaser("\\{", "\\}", content, AnsjPaser.TEXTEGEXANDNRT);
		String txt=parse.getText();
		MyJson json=new MyJson();
		json.createJson("{"+txt+"}");
		//System.out.println(txt);
		serverMessage.setExectime(Integer.parseInt(json.getStringByKey("exectime")));
		serverMessage.setIs_openlock(Integer.parseInt(json.getStringByKey("is_openlock")));
		serverMessage.setNonce(json.getStringByKey("nonce"));
		serverMessage.setPcid(json.getStringByKey("pcid"));
		serverMessage.setPubkey(json.getStringByKey("pubkey"));
		serverMessage.setRetcode(Integer.parseInt(json.getStringByKey("retcode")));
		serverMessage.setRsakv(json.getStringByKey("rsakv"));
		serverMessage.setServertime(Long.parseLong(json.getStringByKey("servertime")));
		serverMessage.setShowpin(Integer.parseInt(json.getStringByKey("showpin")));
		//System.out.println(serverMessage);
	}
	
	/**
	 * 下载验证码
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void downlodImage() throws ClientProtocolException, IOException{
		//http://login.sina.com.cn/cgi/pin.php?r=23229743&s=0&p=gz-77981d5eb3f4a58faf5ade299a1b38b0f7b8
		String url="http://login.sina.com.cn/cgi/pin.php?r=23229743&s=0&p="+serverMessage.getPcid();
		System.out.println("imageURL=" + url);
		HttpGet get=new HttpGet(url);
		CloseableHttpResponse res=httpclient.execute(get);
		InputStream in=res.getEntity().getContent();
		BufferedImage imge=ImageIO.read(in);
		ImageIO.write(imge, "png",new File(serverMessage.getPcid()+".png"));
		in.close();
		res.close();
	}
	
	

	/**
	 * 对用户名加密
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getUserNameEncode() throws UnsupportedEncodingException {
		byte[] encoded = Base64.encodeBase64(URLEncoder.encode(
				userName, "UTF-8").getBytes());
		String username = new String(encoded);
		return username;
	}
	
	
	
	private void addCookie(HttpResponse res){
		Header[] headers=res.getHeaders("Set-Cookie");
		for(Header header:headers){
			String value=header.getValue();
			String key=value.substring(0,value.indexOf('='));
			String keyValue=value.replace(key+"=", "");
			cookies.put(key, keyValue);
			System.out.println(header.getName()+" "+header.getValue());
			System.out.println(key+" "+keyValue);
			System.out.println("**************************************");
		}
	}
	

	/**
	 * 根据服务器返回的时间，公钥 对密码进行加密
	 * @param pubkey
	 * @param serverTime
	 * @param nonce
	 * @return
	 */
	public String getTimeAndNonceEnc(String pubkey, String serverTime,
			String nonce) {
		String realPwd = PasswordUtil4Sina.getPassEncoding(pubkey, serverTime,
				nonce, passwd);
		return realPwd;
	}
	
	/**
	 * 设置post的数据 
	 * @param post 要post的对象
	 * @param data post的数据
	 * @throws UnsupportedEncodingException
	 */
	public static void setPostData(HttpPost post,Map<String,String> data) throws UnsupportedEncodingException{
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
		Set<Map.Entry<String, String>> ens=data.entrySet();
		for(Map.Entry<String, String> en:ens){
			formparams.add(new BasicNameValuePair(en.getKey(), en.getValue()));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8"); 
		post.setEntity(entity);
	}
	
	public static void setHeaderMap(HttpRequest req,Map<String,Object> data){
		Set<Map.Entry<String, Object>> sets=data.entrySet();
		for(Map.Entry<String, Object> en:sets){
			System.out.println(en.getKey());
			req.addHeader(en.getKey(), en.getValue().toString());
		}
	}
	
	
	
	
	
	/**
	 * 生成第一次第一次登录的请求头信息
	 * @return
	 */
	public HashMap<String,Object> getPostHeader(){
		HashMap<String,Object> headerMap=new HashMap<String,Object>();
		
		headerMap.put("Accept", "textml, application/xhtml+xml, */*");
		headerMap.put("Referer", "http://weibo.com/");
		headerMap.put("Accept-Language", "zh-CN");
		headerMap.put("Cache-Control", "no-cache");
		headerMap.put("Connection", "Keep-Alive");
		headerMap.put("Content-Type", "application/x-www-form-urlencoded");
		headerMap.put("Host", "login.sina.com.cn");
		headerMap.put("DNT", "1");
		headerMap.put("Accept-Encoding", "gzip, deflate");
		headerMap.put("DNT", "1");
		headerMap.put("User-Agent", "Mozilla/5.0 (compatible; MSIE " + "9.0"
				+ "; Windows NT 6.1; WOW64; Trident/6.0)");
		
		return headerMap;
	}
	
	
	/**
	 * 生成第一次登录的表单
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HashMap<String,String> getFirstPostData() throws ClientProtocolException, IOException{
		
		HashMap<String, String> formNameValueMap = new HashMap<String, String>();
		if (serverMessage.getShowpin()==1) {
			//TODO 修改输入验证码的方式
			@SuppressWarnings("resource")
			Scanner scaner=new Scanner(System.in);
			downlodImage();
			System.out.println("请输入"+serverMessage.getPcid()+".png"+"的验证码");
			String code=scaner.nextLine();
			formNameValueMap.put("door",code);
			formNameValueMap.put("pcid", serverMessage.getPcid());
			System.out.println("输入的验证码是---->"+code);
		}
		formNameValueMap.put("encoding", "UTF-8");
		formNameValueMap.put("entry", "weibo");
		formNameValueMap.put("from", "");
		formNameValueMap.put("gateway", "1");
		formNameValueMap.put("nonce", serverMessage.getNonce());
		formNameValueMap.put("prelt", "72");
		formNameValueMap.put("pwencode", "rsa2");
		formNameValueMap.put("returntype", "META");
		formNameValueMap.put("rsakv", serverMessage.getRsakv());
		formNameValueMap.put("savestate", "7");
		formNameValueMap.put("servertime", new Long(serverMessage.getServertime()).toString());
		formNameValueMap.put("service", "miniblog");
		formNameValueMap.put("vsnf", "1");

		formNameValueMap
						.put(
								"url",
								"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
		formNameValueMap.put("useticket", "1");
		formNameValueMap.put("pagerefer", "");
		formNameValueMap.put("su", getUserNameEncode());
		formNameValueMap.put("sp",
				getTimeAndNonceEnc(serverMessage.getPubkey(), new Long(serverMessage.getServertime()).toString(), serverMessage.getNonce()));
				
		return formNameValueMap;
	}
	
	
	//下面都是一些工具类
	//*************************************************************************
	
	/**
	 * 正常解决io流成字符串
	 * 
	 * @param entity
	 * @throws IOException
	 */
	private String dump(HttpEntity entity, String encoding) {
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			br = new BufferedReader(new InputStreamReader(entity.getContent(),
					encoding));
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
	
	private String getRedirectLocation(String content) {
		String regex = "location\\.replace\\([',\"](.*?)[',\"]\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		String location = null;
		if (matcher.find()) {
			location = matcher.group(1);
		}
		return location;
	}

	/**
	 * 将gzip io流转换成字符串
	 * 
	 * @param entity
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private String dumpGZIP(HttpEntity entity, String encoding) {
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(
					entity.getContent()), encoding));
			// br = new BufferedReader(new
			// InputStreamReader(entity.getContent(),
			// "GBK"));
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
	
	public static void main(String[] args) throws ClientProtocolException, IOException{
		GetUserCookie g=new GetUserCookie();
		HashMap<String,String> map=g.getCookie("gua95668@163.com", "aaa333");
		Set<Map.Entry<String, String>> en=map.entrySet();
		for(Map.Entry<String, String> s:en){
			System.out.print(s.getKey()+"="+s.getValue()+";");
		}
	}
	
	
	
	
	public String getCookies_sus() {
		return cookies_sus;
	}

	public void setCookies_sus(String cookies_sus) {
		this.cookies_sus = cookies_sus;
	}




	/**
	 * 初始化登录时从服务器返回的数据
	 * @time 2016-3-22
	 * @author HanHan
	 * 
	 */
	public static class ServerMessage{
		
		private int retcode;
		
		private long servertime;
		
		private String pcid;
		
		private String nonce;
		
		private String pubkey;
		
		private String rsakv;
		
		private int is_openlock;
		
		private int showpin;
		
		private int exectime;
		
		public String toString(){
			StringBuilder builder=new StringBuilder();
			
			builder.append("[retcode="+retcode+" servertime="+servertime+" pcid="+pcid+" nonce="+nonce);
			builder.append("pubkey="+pubkey+" rsakv="+rsakv+" is_openlock"+is_openlock);
			builder.append(" showpin="+showpin+" exectime"+exectime+"]");
			
			return builder.toString();
		}
		
		public int getRetcode() {
			return retcode;
		}

		public void setRetcode(int retcode) {
			this.retcode = retcode;
		}

		public long getServertime() {
			return servertime;
		}

		public void setServertime(long servertime) {
			this.servertime = servertime;
		}

		public String getPcid() {
			return pcid;
		}

		public void setPcid(String pcid) {
			this.pcid = pcid;
		}

		public String getNonce() {
			return nonce;
		}

		public void setNonce(String nonce) {
			this.nonce = nonce;
		}

		public String getPubkey() {
			return pubkey;
		}

		public void setPubkey(String pubkey) {
			this.pubkey = pubkey;
		}

		public String getRsakv() {
			return rsakv;
		}

		public void setRsakv(String rsakv) {
			this.rsakv = rsakv;
		}

		public int getIs_openlock() {
			return is_openlock;
		}

		public void setIs_openlock(int is_openlock) {
			this.is_openlock = is_openlock;
		}

		public int getShowpin() {
			return showpin;
		}

		public void setShowpin(int showpin) {
			this.showpin = showpin;
		}

		public int getExectime() {
			return exectime;
		}

		public void setExectime(int exectime) {
			this.exectime = exectime;
		}
		
		
	}
}
