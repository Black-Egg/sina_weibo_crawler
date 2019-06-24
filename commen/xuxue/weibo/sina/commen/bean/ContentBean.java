package xuxue.weibo.sina.commen.bean;

/**
 * 新浪微博博文的抽象
 * 
 * @author LM
 * mid 微博mid
 * rmid 原微博mid（转发时出现，其余为null）
 * ouid 所查看微博作者uid
 * rouid 所查看微博原作者uid
 * otime 微博发布时间
 * rotime 原博发布时间
 * blogContent 博文内容
 * resent 转发数
 * comment 评论数
 * praise 点赞数
 *
 */

public class ContentBean {
	
	public enum MediaType{
		feed_list_media_img,fl_pics,feed_list_third_rend,text
	}
	
	private String mid;
	
	private String rmid;
	
	private String ouid;
	
	private String rouid;
	
	private String status;
	
	private String type;
	
	private String otime;
	
	private String rtime;
	
	private String blogContent;
	
	
	private int resent;
	
	private int comment;
	
	private int praise;
	
	private MediaType mediaType;
	
	private String actionData;
	
	private String imageURL;
	
	private String vedioURL;
	
	
	public String toString(){
		
		StringBuilder builder=new StringBuilder();
		
		builder.append("[mid="+mid+" "
				+ " rmid="+rmid+
				" ouid="+ouid+
				" rouid="+rouid+
				" status="+status+
				" type="+type+
				" otime="+otime+
				" blogContent="+blogContent+
				" resent="+resent+
				"comment="+comment+
				"praise="+praise+
				"mediaType="+mediaType+
				"actionData="+actionData+
				"imageURL="+imageURL+
				"]");
		
		return builder.toString();
	}
	
	
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public int getResent() {
		return resent;
	}

	public void setResent(int resent) {
		this.resent = resent;
	}




	public int getComment() {
		return comment;
	}


	public void setComment(int comment) {
		this.comment = comment;
	}



	public int getPraise() {
		return praise;
	}



	public void setPraise(int praise) {
		this.praise = praise;
	}

		
	public String getOuid() {
		return ouid;
	}

	public void setOuid(String ouid) {
		this.ouid = ouid;
	}

	public String getRouid() {
		return rouid;
	}

	public void setRouid(String rouid) {
		this.rouid = rouid;
	}

	public String getOtime() {
		return otime;
	}


	public void setOtime(String otime) {
		this.otime = otime;
	}


	public String getRtime() {
		return rtime;
	}

	public void setRtime(String rtime) {
		this.rtime = rtime;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getRmid() {
		return rmid;
	}

	public void setRmid(String rmid) {
		this.rmid = rmid;
	}

	public String getBlogContent() {
		return blogContent;
	}


	public void setBlogContent(String blogContent) {
		this.blogContent = blogContent;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public String getActionData() {
		return actionData;
	}

	public void setActionData(String actionData) {
	
		this.actionData=actionData;
		//type=feedvideo&objectid=2017607:e8d13d8f2597b5edd90f544027590199
	}
	
	


	public String getVedioURL() {
		return vedioURL;
	}


	public void setVedioURL(String vedioURL) {
		this.vedioURL = vedioURL;
	}
}
