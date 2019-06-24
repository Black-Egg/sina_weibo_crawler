package xuxue.weibo.sina.commen.util;


import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuxue.weibo.sina.commen.bean.CommentBean;
import xuxue.weibo.sina.commen.bean.ContentBean;


/**
 * http://weibo.com/aj/v6/comment/small?ajwvr=6&act=list&mid=3955167675692254&uid=5814032273&isMain=true&dissDataFromFeed=%5Bobject%20Object%5D&ouid=1642591402&location=page_100808_home&filter_actionlog=&_t=0&__rnd=1458481534202
 * http://weibo.com/aj/v6/comment/big?ajwvr=6&id=3955167675692254&__rnd=1458481592460
 * @author LM
 *
 */
public class CommentPage {
	
	public ContentBean blog;
	
	private GrabPageResource page;
	
	private int maxComment;
	
	private MyJson json=new MyJson();
	
	
	public LinkedList<CommentBean> getComment(int maxComment) throws  GetPageException{
		
		LinkedList<CommentBean> comments=new LinkedList<CommentBean>();
		
		int maxPageNum=maxComment/20;
		if((blog.getComment()/20)+1<maxPageNum){
			maxPageNum=blog.getComment()/20+1;
		}
		
		for(int i=1;i<=maxPageNum;i++){
			
			comments.addAll(getCommentByPage(i));
			
		}
		return comments;
	}
	
	
	public LinkedList<CommentBean> getCommentByPage(int pageNum) throws GetPageException{
		
		LinkedList<CommentBean> comments=new LinkedList<CommentBean>();
		String url="http://weibo.com/aj/v6/comment/big?ajwvr=6&id="+blog.getMid()+"&max_id="+blog.getComment()+"&page="+pageNum;
		String data=page.getPageSourceOfSina(url, null, true);
		Document doc=parseToDocument(data);
		Elements ele=doc.select(".list_li.S_line1.clearfix");
		for(Element e:ele){
			comments.add(initCommentBean(e));
		}
		return comments;
	}
	
	private CommentBean initCommentBean(Element e){
		CommentBean bean=new CommentBean();
		bean.setId(e.attr("comment_id"));
		bean.setNick(e.select(".WB_text>a").get(0).text());
		String content=e.select(".WB_text").text();
		bean.setContent(content.replace(bean.getNick(), "").replace("：", ""));
		
		bean.setTime(e.select(".WB_from.S_txt2").text());
		
		bean.setUid(e.select(".WB_text>a[usercard]").attr("usercard").replace("id=", ""));
		
		bean.setMid(blog.getMid());
		
		try {
			bean.setPraise(Integer.parseInt(e.select("a[node-type]").text()));
			
		} catch (Exception e2) {
			bean.setPraise(0);
		}
		return bean;
	}
	
	
	public Document parseToDocument(String data){
		
		json.createJson(data);
		String html=json.getStringByKey("data");
		json.createJson(html);
		html=json.getStringByKey("html");
		return Jsoup.parse(html);
		
	}
	
	
	
	
	public void init(ContentBean bean,GrabPageResource page){
		this.page=page;
		this.blog=bean;
		maxComment=bean.getComment();
	}

}
