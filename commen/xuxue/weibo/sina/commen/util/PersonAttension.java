package xuxue.weibo.sina.commen.util;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuxue.weibo.sina.commen.bean.PersonBean;
import xuxue.weibo.sina.commen.system.AccessPara;


public class PersonAttension {
	
	private Logger logger=Logger.getLogger(PersonAttension.class);
			
	
	private String url;
	
	private GrabPageResource page;
	
	private String content;
	
	private AnsjPaser paser;
	
	private MyJson json=new MyJson();
	
	public PersonAttension(){
		
	}
	
	public List<PersonBean> getAttension(int page)throws SocketTimeoutException{
		ArrayList<PersonBean> bean=new ArrayList<PersonBean>();
		content=getContent(page);
		paser=new AnsjPaser("\\{\"ns\":\"pl.content.followTab.index\",\"domid\":\"Pl_Official_HisRelation__[0-9].*?\"","\\}\\)",
				content, AnsjPaser.TEXTEGEXANDNRT);
		String content=paser.getText();
		json.createJson("{"+content+"}");
		Document doc=Jsoup.parse(json.getStringByKey("html"));
		Elements es=doc.select("li.follow_item.S_line2");
		for(Element e:es){
			System.out.println("********我是分割线***********");
			PersonBean b=new PersonBean();
			String data=e.attr("action-data");
			fillPersonByData(data, b);
			fillPersonFans(e, b);
			bean.add(b);
		}
		
		return bean;
	}
	
	private void fillPersonFans(Element e,PersonBean person){
		String fanseselect="span.conn_type";
		String hrefSelect="a";
		Elements eles=e.select(fanseselect);
		for(Element r:eles){
			String key=r.text();
			if(key.contains("关注")){
				try {
					String attensionAddress=r.select(hrefSelect).attr("href");
					String number=r.select(hrefSelect).text();
					person.setAttention(Integer.parseInt(number));
					person.setAttensionAddress(AccessPara.BASEURL+attensionAddress);
					logger.info("这个关注用户的关注数目为------->"+person.getAttention());
					logger.info("这个关注用户的关注地址为------->"+person.getAttensionAddress());
				} catch (Exception e2) {
					logger.info("获得关注用户是出现错误",e2);
				}
			}else if(key.contains("粉丝")){
				try {
					String fansAddress=r.select(hrefSelect).attr("href");
					String number=r.select(hrefSelect).text();
					person.setFans(Integer.parseInt(number));
					person.setFanseAddress(AccessPara.BASEURL+fansAddress);
					logger.info("这个关注用户粉丝数目为------->"+person.getFans());
					logger.info("这个关注用户粉丝地址为------->"+person.getAttensionAddress());
				} catch (Exception e2) {
					logger.info("获得粉丝出现错误",e2);
				}
			}else if(key.contains("微博")){
				try {
					String homeAddress=r.select(hrefSelect).attr("href");
					String number=r.select(hrefSelect).text();
					person.setWeiboNumber(Integer.parseInt(number));
					person.setHomeAddress(AccessPara.BASEURL+homeAddress);
					logger.info("这个关注用户微博数目为------->"+person.getWeiboNumber());
					logger.info("这个关注用户微博主页为------->"+person.getAttensionAddress());
				} catch (Exception e2) {
					logger.info("获得微博出现错误",e2);
				}
			}
		}
	}
	
	private void fillPersonByData(String data,PersonBean bean){
		String[] dataArray=data.split("&");
		for(String s:dataArray){
			if(s.contains("uid")){
				bean.setUid(s.substring(4));
				logger.info("获得一个关注用户 他的uid是---------->"+bean.getUid());
			}else if(s.contains("nick")){
				bean.setUserName(s.substring(6));
				logger.info("获得一个关注用户 名字是------------->"+bean.getUserName());
			}else if(s.contains("sex")){
				bean.setSex(s.substring(4));
				logger.info("获得一个关注的用户  他的性别是-------->"+bean.getSex());
			}
		}
	}
	
	public String getContent(int pageNum) throws SocketTimeoutException{
		//TODO 可能返回的文档有问题
		String senURL="";
		if(url.contains("?")){
			senURL=url+"&page="+pageNum;
		}else{
			senURL=url+"?page="+pageNum;
		}
		String content=page.getPageSourceOfSina(senURL, null);
		System.out.println(content);
		return content;
	}
	
	public void init(GrabPageResource page,PersonBean bean){
		this.page=page;
		this.url=bean.getAttensionAddress();
	}
	
	
	
}
