package xuxue.weibo.sina.commen.util;


import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuxue.weibo.sina.commen.bean.ContentBean;
import xuxue.weibo.sina.commen.bean.ContentBean.MediaType;
import xuxue.weibo.sina.commen.bean.PersonBean;


public class WeiboContentPage {
	
	private PersonBean person;
	
	private GrabPageResource source;
	
	private int maxPage;
	
	private MyJson json=new MyJson();
	
	
	private int pageNum;
	
	private String domid;
	
	private String pageId;
	
	public WeiboContentPage(){
		
	}
	
	public ArrayList<ContentBean> getContent(int page) throws GetPageException, NoHeaderException{
		ArrayList<ContentBean> beans=new ArrayList<ContentBean>();
		Elements elements=getPageToElements(page);
		for(Element e:elements){
			System.out.println("get bean");
			beans.add(parseElement(e));
		}
		return beans;
	}
	
	/**
	 * 根据一个html页面解析得到微博的内容
	 * @param data html页面
	 * @return 微博内容的列表
	 * @throws NoHeaderException 如果这个页面错误
	 * @throws GetPageException 初始化视频地址出现错误
	 */
	public ArrayList<ContentBean> getContent(String data) throws NoHeaderException, GetPageException{
		Document doc=filterToDocument(data, false);
		Elements ele=filterToElement(doc);
		ArrayList<ContentBean> bean=new ArrayList<ContentBean>();
		for(Element e:ele){
			bean.add(parseElement(e));
		}
		return bean;
	}
	
	private ContentBean parseElement(Element content) throws GetPageException{
		
		ContentBean bean=new ContentBean();
		initUid(content, bean);
		initTime(content, bean);
		initContent(content, bean);
		initNumber(content, bean);
		initMediaType(content, bean);
		initMediaURL(content, bean);
		System.out.println("**********************************");
		System.out.println(bean);
		
		return bean;
	}
	
	/**
	 * 根据第几页返回所有包含微博的Element对象
	 * @param pageNum
	 * @return
	 * @throws NoHeaderException 
	 * @throws GetPageException 
	 * @throws Exception
	 */
	private Elements getPageToElements(int pageNum) throws GetPageException, NoHeaderException {
		Elements ele=new Elements();
		for(int i=-1;i<2;i++){
			Document doc=getPageToDocument(pageNum, i);
			
			ele.addAll(filterToElement(doc));
			if(i==1){
				initMaxPage(doc);
			}
		}
		
		return ele;
	}
	
	
	
	/**
	 * 将Document转换为包含微博内容的Element
	 * @param doc
	 * @return
	 */
	private Elements filterToElement(Document doc){
		Elements e=doc.select(".WB_cardwrap.WB_feed_type.S_bg2");
		return e;
	}
	
	
	
	/**
	 * 根据页面的页数和pagebar返回这个页面的Document
	 * @param pageNum
	 * @param pagebar
	 * @return
	 * @throws GetPageException 
	 * @throws NoHeaderException 
	 * @throws Exception 
	 */
	private Document getPageToDocument(int pageNum,int pagebar) throws GetPageException, NoHeaderException{
		
		String url=translateToAllPageURL(pageNum, pagebar);
		String content=null;
		if(pagebar<0){
			content=source.getPageSourceOfSina(url, null, false);
			
			return filterToDocument(content, false);
		}else{
			content=source.getPageSourceOfSina(url, null,true);  
			return filterToDocument(content, true);
		}
		
	}
	
	/**
	 * 根据页面的内容返回一个Document
	 * @param content 页面的内容
	 * @param isAjax 是否是ajax内容
	 * @return
	 * @throws NoHeaderException 
	 * @throws Exception
	 */
	private Document filterToDocument(String content,boolean isAjax) throws NoHeaderException{
		if(!isAjax){
			this.domid=PersonInfoPage.getAHeader("domain", content, true);
			this.pageId=PersonInfoPage.getAHeader("page_id", content, true);
			AnsjPaser paser=new AnsjPaser("\\{\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Official_MyProfileFeed__[0-9].*?\"","\\}\\)",
					content, AnsjPaser.TEXTEGEXANDNRT);
			String html=paser.getText();
			json.createJson("{" + html+ "}");
			html=json.getStringByKey("html");
			return Jsoup.parse(html);
		}else{
			json.createJson(content);
			String s=json.getStringByKey("data");
			return Jsoup.parse(s);
		}
	}
	
	
	
	
	/**
	 * 根据页数和这个页数的pagebar 得到地址
	 * @param pageIndex 页数
	 * @param pagebar
	 * @return 地址
	 */
	private String translateToAllPageURL(int pageIndex,int pagebar){
		if(pagebar==-1){
			String url=person.getHomeAddress();
			int index=url.indexOf("?");
			url=url.substring(0,index);
			url+="?is_search=0&visible=0&is_all=1&is_tag=0&profile_ftype=1&page="+pageIndex;
			return url;
		}else{
			String url="http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6";
			url+="&pagebar="+pagebar;
			url+="&is_all=1";
			url+="&domain="+domid;
			url+="&id="+pageId;
			url+="&pre_page="+pageIndex;
			url+="&is_search=0&visible=0&is_all=1&is_tag=0&profile_ftype=1";
			return url;
		}
		
	}
	
	
	
	public void init(GrabPageResource source,PersonBean person){
		this.source=source;
		this.person=person;
	}
	
	private void initUid(Element e,ContentBean bean){
		String tbinfo=e.attr("tbinfo");
		if(tbinfo.contains("&")){
			String[] ids=tbinfo.split("&");
			for(String id:ids){
				if(id.contains("ouid=")){
					id=id.replace("ouid=", "");
					bean.setOuid(id);
				}else{
					id=id.replace("rouid=", "");
					bean.setRouid(id);
				}
			}
			bean.setMid(e.attr("mid"));
			bean.setRmid(e.attr("omid"));
		}else{
			bean.setOuid(tbinfo.replace("ouid=", ""));
			bean.setMid(e.attr("mid"));
		}
	}
	
	private void initTime(Element e,ContentBean bean){
		Elements ele=e.select(".WB_detail>div.WB_from.S_txt2>a");
		bean.setOtime(ele.attr("title"));
	}
	
	//图片的ur class-----> ur.WB_media_a WB_media_a_mn WB_media_a_m2 clearfix
	//图片li的 class -----> li.WB_pic li_1 S_bg1 S_line2 bigcursor li_n_h
	//视频的   li Class li.WB_video  S_bg1 WB_video_a
	//视频的ur class----->  ul.WB_media_a WB_media_a_m1 clearfix
	
	
	//文字的class ------>div.WB_text W_f14
	private void initContent(Element e,ContentBean bean) throws GetPageException{
		String txt=e.select(".WB_text.W_f14").text();
		
		Elements ele=e.select(".WB_text.W_f14").select(".WB_text_opt");
		String longText=null;
		if(ele.size()!=0){
			longText=getLongText(bean.getMid());
			txt+=longText;
		}
		System.out.println("txt = "+txt);
		bean.setBlogContent(txt);
	}
	
	
	
	
	/**
	 * 初始化赞  转发数  和评论数
	 * @param e
	 * @param bean
	 */
	private void initNumber(Element e,ContentBean bean){
		Elements ele=e.select(".WB_row_line.WB_row_r4.clearfix.S_line2");
		String s=ele.text();
		String[] numbers=s.split(" ");
		try {
			bean.setResent(Integer.parseInt(numbers[1].replaceAll("[^0-9]", "")));
		} catch (Exception e2) {
			bean.setResent(0);
		}
		try {
			bean.setComment(Integer.parseInt(numbers[2].replaceAll("[^0-9]", "")));
		} catch (Exception e2) {
			bean.setComment(0);
		}
		try {
			bean.setPraise(Integer.parseInt(numbers[3].replaceAll("[^0-9]", "")));
		} catch (Exception e2) {
			bean.setPraise(0);
		}
	}
	
	private void initMediaType(Element e,ContentBean bean){
		Elements ele=e.select(".media_box");
		
		try {
			ele=ele.select("ul>li");
			if(ele.size()==0){
				bean.setMediaType(MediaType.text);
				return;
			}
		} catch (NullPointerException e2) {
			bean.setMediaType(MediaType.text);
			return;
		}
		String type=ele.get(0).attr("action-type");
		if(type.equals("feed_list_media_img")){
			bean.setMediaType(MediaType.feed_list_media_img);
		}else if(type.equals("feed_list_third_rend")){
			bean.setMediaType(MediaType.feed_list_third_rend);
		}else if(type.equals("fl_pics")){
			bean.setMediaType(MediaType.fl_pics);
		}else{
			bean.setMediaType(MediaType.text);
		}	
	}
	
	
	private void initMediaURL(Element e,ContentBean bean){
		if(bean.getMediaType()==MediaType.text){
			return;
		}
		
		Elements ele=e.select(".media_box>ul>li");
		String actionData="";
		String imageURL="";
		if(ele.size()==0)
			return;
		for(int i=0;i<ele.size();i++){
			if(i==0){
				actionData+=ele.get(i).attr("action-data");
				String url=ele.get(i).select("img").attr("src");
				imageURL+=url;
			}
			else{
				actionData+=","+ele.get(i).attr("action-data");
				String url=ele.get(i).select("img").attr("src");
				imageURL+=","+url;
			}
		}
		
		bean.setActionData(actionData);
		bean.setImageURL(imageURL);
	}
	
	
	//http://weibo.com/p/aj/mblog/getlongtext?ajwvr=6&mid=3954849588679104
	public String getLongText(String mid) throws GetPageException{
		String url="http://weibo.com/p/aj/mblog/getlongtext?ajwvr=6&mid="+mid;
		String content=source.getPageSourceOfSina(url, null, true);
		json.createJson(content);
		content=json.getStringByKey("data");
		json.createJson(content);
		content=json.getStringByKey("html");
		Document doc=Jsoup.parse(content);
		return doc.text();
	}
	
	
	
	
	public String getVideoURL(String mid,String objectId,String type,String isforword){
		
		return null;
	}
	
	public void initMaxPage(Document doc){
		Elements e=doc.select("a[bpfilter=page]");
		for(Element ee:e){
			System.out.println(ee.toString());
		}
		this.setMaxPage(e.size()+1);
	}
	
	public static void main(String[] args){
		
		
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}
	
}
