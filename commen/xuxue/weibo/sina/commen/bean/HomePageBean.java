package xuxue.weibo.sina.commen.bean;

/**
 * 新浪微博用户主页的抽象
 * 
 * @author LM
 * person 用户抽象
 * content 主页微博抽象集合
 *
 */


import java.util.ArrayList;

public class HomePageBean {
	
	private PersonBean person;
	
	private ArrayList<ContentBean> content;
	
	public String toString(){
		StringBuilder builder=new StringBuilder();
		builder.append(person.toString());
		
		for(ContentBean con:content){
			builder.append(con+"\n");
		}
		return builder.toString();
	}
	
	public PersonBean getPerson() {
		return person;
	}

	public void setPerson(PersonBean person) {
		this.person = person;
	}

	public ArrayList<ContentBean> getContent() {
		return content;
	}

	public void setContent(ArrayList<ContentBean> content) {
		this.content = content;
	}
	
}
