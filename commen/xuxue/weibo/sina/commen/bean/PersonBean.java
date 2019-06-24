package xuxue.weibo.sina.commen.bean;

/**
 * 新浪微博用户的抽象
 * 
 * @author LM
 * uid 用户uid
 * info 用户简介
 * sex 用户性别
 * fans 粉丝数
 * attention 关注数
 * weiboNumber 发布微博数
 * userName 用户名
 * place 居住地
 * school 毕业学校
 * company 职业信息
 * summary 个人简介
 * email 邮箱信息
 * teg 标签信息
 *
 */


import java.io.Serializable;
import java.util.HashMap;




public class PersonBean implements Serializable{
	
	
	private static final long serialVersionUID = 1L;

	private static final String PLACE_KAY="W_ficon ficon_cd_place S_ficon";
	
	private static final String SCHOOL_KEY="W_ficon ficon_edu S_ficon";
	
	private static final String COMPANY_KEY="W_ficon ficon_bag S_ficon";
	
	private static final String SUMMAY_KEY="W_ficon ficon_pinfo S_ficon";
	
	private static final String EMAIL_KEY="W_ficon ficon_email S_ficon";
	
	private static final String TEG_KEY="W_ficon ficon_cd_coupon S_ficon";
	
	private static final String BIRTHDAY_KEY="W_ficon ficon_constellation S_ficon";
	
	private String uid;

	private String verify_info;
	
	private String info;
	
	private String sex;
	
	private int fans;
	
	private int attention;
	
	private int weiboNumber;
	
	private String userName;
	
	private String place;
	
	private String school;
	
	private String company;
	
	private String summary;
	
	private String email;
	
	private String teg;
	
	private String birthday;
	
	private HashMap<String,String> message;
	
	private String homeAddress;
	
	private String attensionAddress;
	
	private String fanseAddress;
	
	
	
	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getAttensionAddress() {
		return attensionAddress;
	}

	public void setAttensionAddress(String attensionAddress) {
		this.attensionAddress = attensionAddress;
	}

	public String getFanseAddress() {
		return fanseAddress;
	}

	public void setFanseAddress(String fanseAddress) {
		this.fanseAddress = fanseAddress;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getVerify_info() {
		return verify_info;
	}

	public void setVerify_info(String verify_info) {
		this.verify_info = verify_info;
	}

	public HashMap<String,String> getMessage() {
		return message;
	}
	
	
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTeg() {
		return teg;
	}

	public void setTeg(String teg) {
		this.teg = teg;
	}
	
	
	
	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getAttention() {
		return attention;
	}

	public void setAttention(int attention) {
		this.attention = attention;
	}

	public int getWeiboNumber() {
		return weiboNumber;
	}

	public void setWeiboNumber(int weiboNumber) {
		this.weiboNumber = weiboNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	public String toString(){
		StringBuilder b=new StringBuilder();
		b.append("[uid="+uid);
		b.append(" userName="+userName);
		b.append(" info="+info);
		b.append(" fans="+fans);
		b.append(" attention="+attention);
		b.append(" weiboNumber="+weiboNumber);
		b.append(" sex="+sex);
		b.append(" place="+place);
		b.append(" school="+school);
		b.append(" company="+company);
		b.append(" summary="+summary);
		b.append(" email="+email);
		b.append(" teg="+teg);
		b.append(" birthday="+birthday);
		b.append("]");
		return b.toString();
	}
	
	public void setMessage(HashMap<String,String> message) {
		this.message = message;
		
		teg=message.get(TEG_KEY);
		email=message.get(EMAIL_KEY);
		place=message.get(PLACE_KAY);
		school=message.get(SCHOOL_KEY);
		company=message.get(COMPANY_KEY);
		summary=message.get(SUMMAY_KEY);
		birthday=message.get(BIRTHDAY_KEY);
	}
	
		
}
