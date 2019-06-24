package xuxue.weibo.sina.commen.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import xuxue.weibo.sina.commen.bean.CommentBean;
import xuxue.weibo.sina.commen.bean.ContentBean;
import xuxue.weibo.sina.commen.bean.HomePageBean;
import xuxue.weibo.sina.commen.bean.PersonBean;


/**
 * 新浪用户的抽象  可以用这个类访问新浪微博
 *@author LM
 */
public class SinaUser {
	
	private Logger logger=Logger.getLogger(SinaUser.class);
	
	private String nick;
	
	private String passwd;
	
	private GrabPageResource source;
	
	private GetUserCookie getCookie;
	
	private PersonInfoPage personPage=new PersonInfoPage();
	
	private PersonAttension attensionPage=new PersonAttension();
	
	private PersonFansPage fansePage=new PersonFansPage();
	
	/**
	 * 访问用户的微博页面
	 * @param person 这个用户
	 * @param page 表示第几个页面
	 * @return
	 * @throws NoPageException 页面不是微博页面
	 * @throws GetPageException 访问页面发生异常
	 * @throws NoHeaderException 页面不是微博页面
	 */
	public List<ContentBean> visitBlogPage(PersonBean person,int page)throws NoPageException, GetPageException, NoHeaderException{
		blogPage.init(source, person);
		ArrayList<ContentBean> contens=blogPage.getContent(page);
		return contens;
	}

	private WeiboContentPage blogPage=new WeiboContentPage();
	
	private CommentPage commentPage=new CommentPage();
	
	public SinaUser(String nick,String passwd,IdentifyingCodeHandler handler){
		this.nick=nick;
		this.passwd=passwd;
		getCookie=new GetUserCookie();
		source=new GrabPageResource();
	}
	
	public void login() throws ClientProtocolException, IOException{
		String cookie=getCookie.getCookieString(nick, passwd);
		//String cookie="SUS=SID-5813607279-1458630144-GZ-fz2t6-5a67d917086a53d63d3bbce00cf75fdf; path=/; domain=.weibo.com; httponly;LT=1458630144; path=/; domain=login.sina.com.cn;SUHB=0Ry6Bi16sU05xP; expires=Wednesday, 22-Mar-17 07:02:24 GMT; path=/; domain=.weibo.com;SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WhzulGv.m4dT8_VL_yZBEJM5JpX5K2t; expires=Wednesday, 22-Mar-17 07:02:24 GMT; path=/; domain=.weibo.com;tgc=TGT-NTgxMzYwNzI3OQ==-1458630144-gz-0504C7525EDE18E9FED842BE542C6954; domain=login.sina.com.cn; path=/; Httponly;SRF=1458630144; expires=Friday, 20-Mar-26 07:02:24 GMT; path=/; domain=.passport.weibo.com;SUE=es%3Dc61aab2e7fcf45fb9349b6cfc61989d6%26ev%3Dv1%26es2%3D90402e14a64f0ceb0755183a330c0b7a%26rs0%3DJP7U3cp6xz%252B5IMZAQ3XO8fhHRFws7q6CnycV7ZIZ3f13HXxknyjkp%252BchbSpYtmNVPC1Yk6LeazWTwhWrwqWeFzx2injCYXcvb738LGToirR4mRCOntbKDO%252FLljQbsveU35GnxC92zlI3w1DVxjdyXY45B2FyPn57aPeGesv91aE%253D%26rv%3D0;path=/;domain=.weibo.com;Httponly;SUB=_2A2579J5QDeTxGeNG6lEX8CnOzDWIHXVYg4iYrDV8PUNbuNBeLU3QkW9LHetGr4TgaPVyp2UZ38o-uL5cDC3roA..; path=/; domain=.weibo.com; httponly;ALF=1490166144; expires=Wed, 22-Mar-2017 07:02:24 GMT; path=/; domain=.weibo.com;ALC=ac%3D27%26bt%3D1458630144%26cv%3D5.0%26et%3D1490166144%26uid%3D5813607279%26vf%3D0%26vs%3D0%26vt%3D0%26es%3Dacd7bae969c4458766b773d35192e958; expires=Wednesday, 22-Mar-17 07:02:24 GMT; path=/; domain=login.sina.com.cn; httponly;sso_info=v02m6alo5qztKWRk5ClkKOgpY6DgKWRk5SljoSYpZCUhKWRk5yljoOIpZCjoKWRk5ilkJSYpY6UiKWRk5SlkKOApY6EmKWRk5ClkKSIpY6TjKWRk6SlkKSMpZCTgKadlqWkj5OUuIyTjLaMg5yyjbOkwA==; expires=Wednesday, 22-Mar-17 07:02:24 GMT; path=/; domain=.sina.com.cn;SSOLoginState=1458630144; path=/; domain=.weibo.com;SUP=cv%3D1%26bt%3D1458630144%26et%3D1458716544%26d%3Dc909%26i%3D5fdf%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D27%26st%3D0%26uid%3D5813607279%26name%3Dqijiucunx7590%2540163.com%26nick%3D%25E4%25B8%2580%25E5%258F%25AA%25E7%2582%25B8%25E6%25AF%259B%25E5%25B0%258F%25E4%25BB%2593%25E9%25BC%25A0%26fmp%3D%26lcp%3D;path=/;domain=.weibo.com;SRT=E.vAfsKDmqiZv3JZVuvcsmKnmBvXvCvXMd0KtvvnEABvzvvv4m3Ii0kvVmP6z2XvfCvvAivOmKvAmLvAmMvXvCmXmUFvmvFXMd0Ktv*B.vAflW-P9Rc0lR-ykADvnJqiQVbiRVPBtS!r3JZPQVqbgVdWiMZ4siOzu4DbmKPWf5ePjM3RlUOM!PDYaP!oa5P9CM49ii49ndDPIJeA7; expires=Friday, 20-Mar-26 07:02:24 GMT; path=/; domain=.passport.weibo.com; httponly;";
		logger.info("一个用户登录了 cookie是--->"+cookie);
		source.setCookieString(cookie);
	}
	
	/**
	 * 访问一个用户的主页
	 * @param uid 被访问用户的uid
	 * @return 这个主页的内容  包括这个用户的详细信息和这个用户的首页的微博
	 * @throws GetPageException
	 * @throws NoHeaderException 
	 * @throws URLBlankException 
	 */
	public HomePageBean visitHomePage(String uid) throws GetPageException, URLBlankException, NoHeaderException{
		PersonBean person=lookBaseUserMessage(uid);
		personPage.init(source, person);
		person=personPage.getPersonInfo();
		String content=personPage.getContent();
		ArrayList<ContentBean> contents=blogPage.getContent(content);
		HomePageBean page=new HomePageBean();
		page.setContent(contents);
		page.setPerson(person);
		return page;
	}
	
	/**
	 * 浏览一个用户的基本信息  这些信息包括关注数  粉丝数  发的微博数
	 * 还有性别 昵称等
	 * @param uid 用户的uid
	 * @return 这个用户的基本信息
	 * @throws GetPageException 访问这个页面发生异常
	 */
	public PersonBean  lookBaseUserMessage(String uid) throws GetPageException{
		String url="http://weibo.com/aj/v6/user/newcard?ajwvr=6&id="+uid+"&type=1";
		PersonBean person=new PersonBean();
		String data=source.getPageSourceOfSina(url, null,true);
		System.out.println(data);
		AnsjPaser parser=new AnsjPaser("\\{\"", "\"\\}",data, AnsjPaser.TEXTEGEXANDNRT);
		data=parser.getText();
		
		MyJson json=new MyJson();
		json.createJson("{\""+data+"\"}");
		String html=json.getStringByKey("data");
		Document doc=Jsoup.parse(html);
		Elements attentionElement=doc.select(".c_follow.W_fb");
		person.setAttensionAddress(attentionElement.select("a").attr("href"));
		try {
			person.setAttention(Integer.parseInt(attentionElement.select("a>em").text().replace("万", "0000")));
		} catch (Exception e) {
			logger.info("",e);
		}
		Elements fansElement=doc.select(".c_fans.W_fb");
		person.setFanseAddress(fansElement.select("a").attr("href"));
		try {
			person.setFans(Integer.parseInt(fansElement.select("a>em").text().replace("万", "0000")));
		} catch (Exception e) {
			logger.info("",e);
		}
		
		Elements weiboElement=doc.select(".c_weibo.W_fb");
		person.setHomeAddress(weiboElement.select("a").attr("href"));
		try {
			person.setWeiboNumber(Integer.parseInt(weiboElement.select("a>em").text().replace("万", "0000")));
		} catch (Exception e) {
			logger.info("",e);
		}
		person.setUserName(doc.select(".name>a").attr("title"));
		person.setSex(doc.select(".name>em").attr("title"));
		//System.out.println("个人信息:"+person.toString());
		return person;
	}
	
	/**
	 * 访问评论页面
	 * @param blog 这条微博的评论
	 * @param max 抓取的最大评论数量
	 * @return
	 * @throws GetPageException如果访问页面发生异常
	 */
	public List<CommentBean> visitComment(ContentBean blog,int max) throws GetPageException{
		commentPage.init(blog,source);
		List<CommentBean> beans=commentPage.getComment(max);
		return beans;
	}
	
	/**
	 * 访问一个用户的关注页面
	 * @param person 这个用户
	 * @param pageNum 访问的关注页面的页数  不能大于5
	 * @return
	 * @throws SocketTimeoutException 访问页面超时
	 */
	public List<PersonBean>  visitAttentionPage(PersonBean person,int pageNum) throws SocketTimeoutException{
		
		attensionPage.init(source, person);
		List<PersonBean> bean=attensionPage.getAttension(pageNum);
		return bean;
	}
	
	/**
	 * 访问一个用户的粉丝页面
	 * @param person 这个用户
	 * @param pageNum 页面序号  不能大于5
	 * @return
	 * @throws SocketTimeoutException
	 */
	public List<PersonBean> visitFansePage(PersonBean person,int pageNum) throws SocketTimeoutException{
		
		fansePage.init(source, person);
		List<PersonBean> bean=fansePage.getFansByPage(pageNum);
		
		return bean;
	}
	
	public static void main(String[] args) throws GetPageException, ClientProtocolException, IOException, URLBlankException, NoHeaderException{
		SinaUser user=new SinaUser("18055969126", "LM18855971262", null);
		user.login();
		PersonBean person=user.lookBaseUserMessage("6572092658");//登陆用户的uid
		HomePageBean page=user.visitHomePage("6572092658");//访问主页的uid（种子uid）
		ArrayList<ContentBean> cbeans=page.getContent();
		
		
		for(ContentBean b:cbeans){
			System.out.println("b=" + b);
			try{            	  
			     Class.forName("com.mysql.jdbc.Driver").newInstance(); 
			     String url ="jdbc:mysql://localhost:3306/weibocrawler_origin?useUnicode=true&characterEncoding=utf8";
			     Connection con= DriverManager.getConnection(url,"root","LM123");  //与驱动器建立连接 
				 PreparedStatement stmt; //创建一个Statement对象 
				 stmt = con.prepareStatement("insert into weibo_content values(?,?,?,?,?,?,?,?,?)");
			     stmt.setString(1,b.getMid());
			     stmt.setString(2,b.getBlogContent());
			     stmt.setString(3,b.getRmid());
			     stmt.setInt(4,b.getResent());
			     stmt.setInt(5,b.getComment());
			     stmt.setInt(6,b.getPraise());
			     stmt.setString(7,b.getOtime());
			     stmt.setString(8,b.getImageURL());
			     stmt.setString(9,b.getOuid());
			     stmt.executeUpdate();
			    
			     stmt.close();
			     con.close();//关闭连接
			}catch(Exception ex){		
				System.out.println(ex.getMessage());	//打印异常信息
			}
			
			List<CommentBean> bs=user.visitComment(b, 100);
			for(CommentBean bbb:bs){
		
				try{            	  
				     Class.forName("com.mysql.jdbc.Driver").newInstance(); 
				     String url ="jdbc:mysql://localhost:3306/weibocrawler_origin?useUnicode=true&characterEncoding=utf8";
				     Connection con= DriverManager.getConnection(url,"root","LM123");  //与驱动器建立连接 
					 PreparedStatement stmt; //创建一个Statement对象 
					 stmt = con.prepareStatement("insert into weibo_comment() values(?,?,?,?,?,?,?)");
				     stmt.setString(1,bbb.getId());
				     stmt.setString(2,bbb.getUid());
				     stmt.setString(3,bbb.getMid());
				     stmt.setString(4,bbb.getNick());
				     stmt.setString(5,bbb.getContent());
				     stmt.setInt(6,bbb.getPraise());
				     stmt.setString(7,bbb.getTime());
				   
				     stmt.executeUpdate();
				    
				     stmt.close();
				     con.close();//关闭连接
				}catch(Exception ex){		
					System.out.println(ex.getMessage());	//打印异常信息
				}

				
				System.out.println("bbb="+bbb);
			}
		}
		
	}
	
}
