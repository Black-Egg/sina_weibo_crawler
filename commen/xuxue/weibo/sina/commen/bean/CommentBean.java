package xuxue.weibo.sina.commen.bean;

/**
 * 新浪微博评论的抽象
 * 
 * @author LM
 * id 评论id
 * uid 评论用户uid
 * content 评论内容
 * praise 评论被赞数
 * mid 评论微博mid
 * nick 评论用户昵称
 * time 评论时间
 *
 */


public class CommentBean {
	
	private String id;
	
	private String uid;
	
	private String content;
	
	private int praise;
	
	private String mid;
	
	private String nick;
	
	private String time;
	
	
	public String toString(){
		StringBuilder builder=new StringBuilder();
		
		builder.append("["
				+ " id="+id
				+ " uid= "+uid
				+ " content="+content
				+ " praise="+praise
				+ " mid="+mid
				+ " nick="+nick
				+ " time="+time);
		
		return builder.toString();
	}
	
	public String getId() {
		return id;
	}
	
	

	public String getNick() {
		return nick;
	}

	

	public String getTime() {
		return time;
	}



	public void setTime(String time) {
		this.time = time;
	}



	public void setNick(String nick) {
		this.nick = nick;
	}



	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	
	
}
