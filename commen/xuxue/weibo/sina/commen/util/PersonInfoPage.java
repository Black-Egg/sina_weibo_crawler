package xuxue.weibo.sina.commen.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuxue.weibo.sina.commen.bean.PersonBean;
import xuxue.weibo.sina.commen.system.AccessPara;


public class PersonInfoPage {
	
private Logger logger=Logger.getLogger(PersonInfoPage.class);
	
	private String sendUrl;
	
	private String productURL;
	
	
	private GrabPageResource pageSource;
	
	private String content;
	
	
	private AnsjPaser paser;
	
	private MyJson myJson=new MyJson();
	
	private GetTitle getTitle=new GetTitle();
	
	private String person_uid;
	
	private String domain_id;
	
	private String renZhInfo;
	
	
	
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public PersonBean getPersonInfo()throws URLBlankException, NoHeaderException{
		
		PersonBean person=new PersonBean();
		
		if(sendUrl==null||sendUrl.equals("")){
			logger.info("url 不能为空");
			throw new URLBlankException();
		}
		
		int error_repeat_times=0;
		while(error_repeat_times<AccessPara.error_repeat_times){
			try {
				content=pageSource.getPageSourceOfSina(this.productURL, null);
				String title=getTitle.getTitleByLine(content);
				logger.info("content ==null----->"+content==null);
				logger.info("title=--------->"+title);
				
				if ((!(title==null||title.length()==0))
						&& title.contains("错误提示ʾ")) {
					logger.info("发现请求繁忙，将进行一次线程中断!");
					try {
						Thread.sleep(60 * 1000);
						logger.info("发现请求繁忙，将进行一次线程中断!");
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					logger.info("发现请求繁忙，将进行一次线程中断!");
				} else {
					
					break;
				}
				
			} catch (Exception e) {
				try {
					logger.info("请求出现超时，sleep a while!"+e);
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					logger.info("出现了超时异常，要睡会再继续请求!");
				}
			}
			error_repeat_times+=1;
		}
		
		if (content == null) {
			logger.info(sendUrl + " OPEN ERROR !");
			throw new NoHeaderException();
		}
		
		analyzeHeader(content,person);
		analyzeFans(content, person);
		analyzeSex(content, person);
		
		try{            	  
		     Class.forName("com.mysql.jdbc.Driver").newInstance(); 
		     String url ="jdbc:mysql://localhost:3306/weibocrawler_origin?useUnicode=true&characterEncoding=utf8";
		     Connection con= DriverManager.getConnection(url,"root","LM123");  //与驱动器建立连接 
			 PreparedStatement stmt; //创建一个Statement对象 
			 stmt = con.prepareStatement("insert into user_info values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		     stmt.setString(1,person.getUid());
		     stmt.setString(2,person.getUserName());
		     stmt.setString(3,person.getInfo());
		     stmt.setInt(4,person.getFans());
		     stmt.setInt(5,person.getAttention());
		     stmt.setInt(6,person.getWeiboNumber());
		     stmt.setString(7,person.getSex());
		     stmt.setString(8,person.getPlace());
		     stmt.setString(9,person.getSchool());
		     stmt.setString(10,person.getCompany());
		     stmt.setString(11,person.getSummary());
		     stmt.setString(12,person.getEmail());
		     stmt.setString(13,person.getTeg());
		     stmt.setString(14,person.getBirthday());
		     stmt.executeUpdate();
		    
		     stmt.close();
		     con.close();//关闭连接
		}catch(Exception ex){		
			System.out.println(ex.getMessage());	//打印异常信息
		}
		
		return person;
	}
	
	public void init(GrabPageResource page,PersonBean bean){
		this.pageSource=page;
		this.sendUrl=bean.getHomeAddress();
		this.productURL=bean.getHomeAddress();
	}
	
	
	public void analyzeTitle(String header,PersonBean bean){
		
	}
	
	public void analyzeHeader(String header,PersonBean person) throws NoHeaderException{
		person_uid = getAHeader("oid", header,true);
		logger.info("person_uid=-------->"+person_uid);
		domain_id=getAHeader("domain", header,true);
		logger.info("domain_id=--------->"+domain_id);
		paser=new AnsjPaser("\\{\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Core_UserInfo__[0-9]*?\"","\\}\\)",
				header, AnsjPaser.TEXTEGEXANDNRT);
		renZhInfo=paser.getText();
//		System.out.println("[PersonInfoPage] renZhInfo"+renZhInfo);
		myJson.createJson("{" + renZhInfo + "}");
		renZhInfo = myJson.getStringByKey("html");
		person.setUid(person_uid);
		person.setSex(getAHeader("sex", header, true));
		person.setUserName(getAHeader("onick", header, true));
		Document doc=Jsoup.parse(renZhInfo);
		getPersonInfo(doc, person);
		getPersonMessage(doc, person);
	}
	
	public void analyzeSex(String html,PersonBean bean){
		
		paser=new AnsjPaser("\\{\"ns\":\"pl.header.head.index\",\"domid\":\"Pl_Official_Headerv6__[0-9]*?\"","\\}\\)",
				html, AnsjPaser.TEXTEGEXANDNRT);
		
		String sex=paser.getText();
		myJson.createJson("{"+sex+"}");
		sex=myJson.getStringByKey("html");
		Document doc=Jsoup.parse(sex);
		String sexSelect="div.pf_username>span>a>i";
		Elements e=doc.select(sexSelect);
		String se=e.get(0).attr("class");
		if(se!=null&&se.length()!=0){
			if(se.equals("W_icon icon_pf_female")){
				logger.info("性别为--------------->"+"女");
				bean.setSex("女");
			}else if(se.equals("W_icon icon_pf_male")){
				logger.info("性别为--------------->"+"男");
				bean.setSex("男");
			}
		}
	}
	
	/**
	 * 获取关注数  粉丝数  一共发的微博数   
	 * @param html
	 * @param bean
	 */
	public void analyzeFans(String html,PersonBean bean){
		paser=new AnsjPaser("\\{\"ns\":\"\",\"domid\":\"Pl_Core_T8CustomTriColumn__[0-9]*?\"","\\}\\)",
				html, AnsjPaser.TEXTEGEXANDNRT);
		String fans=paser.getText();
		myJson.createJson("{"+fans+"}");
		fans=myJson.getStringByKey("html");
		Document doc=Jsoup.parse(fans);
		String fanseSelect="td.S_line1";
		Elements fan=doc.select(fanseSelect);
		
		String keySelect="span.S_txt2";
		String valueSelect="strong";
		String address="a";
		for(Element e:fan){
			String key=e.select(keySelect).text();
			String value=e.select(valueSelect).text();
			
			if(key.equals("关注")){
				try {
					bean.setAttention(Integer.parseInt(value));
					bean.setAttensionAddress(e.select(address).attr("href"));
					logger.info("关注数为-------------->"+bean.getAttention());
					logger.info("关注数者地址----------->"+bean.getAttensionAddress());
				} catch (NumberFormatException e2) {
					logger.info(value+" 不是一个数字", e2);
				}
			}else if(key.equals("粉丝")){
				try {
					bean.setFans(Integer.parseInt(value));
					bean.setFanseAddress(e.select(address).attr("href"));
					logger.info("粉丝数为-------------->"+bean.getFans());
					logger.info("粉丝列表地址----------->"+bean.getFanseAddress());
				
				} catch (NumberFormatException e2) {
					logger.info(value+" 不是一个数字", e2);
				}
			}else if(key.equals("微博")){
				try {
					bean.setWeiboNumber(Integer.parseInt(value));
					bean.setHomeAddress(e.select(address).attr("href"));
					logger.info("一共发的微博数为------->"+bean.getWeiboNumber());
					logger.info("微博主页地址---------->"+bean.getHomeAddress());
				} catch (NumberFormatException e2) {
					logger.info(value+" 不是一个数字", e2);
				}
			}
		}
	}
	
	/**
	 * 获取这个用户的简介信息
	 * @param doc
	 * @param bean
	 */
	public void getPersonInfo(Document doc,PersonBean bean){
		Elements e=doc.select("div.verify_area.W_tog_hover.S_line2>p.info");
		bean.setInfo(e.text());
		logger.info("这个用户的简介信息为---->"+bean.getInfo());
	}
	
	public HashMap<String,String> getPersonMessage(Document doc,PersonBean bean){
		HashMap<String,String> map=new HashMap<String,String>(); 
		Elements e=doc.select("ul.ul_detail>li");
		String keySelect="span.item_ico.W_fl>em";
		String value="span.item_text.W_fl";
		for(Element ee:e){
			map.put(ee.select(keySelect).attr("class"), ee.select(value).text());
			logger.info("-------------------->"+ee.select(value).text());
		}
		bean.setMessage(map);
		return map;
	}
	
	public static String getAHeader(String hearderName,String header,boolean notNull)throws NoHeaderException{
		String beginRegex = "CONFIG\\[\'"+hearderName+"\'\\]=\'";
		String 	endRegex = "\'";
		AnsjPaser ansjId = new AnsjPaser(beginRegex, endRegex, header,
				AnsjPaser.TEXTEGEXANDNRT);
		String re=ansjId.getText();
		if(notNull){
			if(re==null||re.length()==0){
				throw  new NoHeaderException(hearderName);
			}
		}
		return re;
	}
}
