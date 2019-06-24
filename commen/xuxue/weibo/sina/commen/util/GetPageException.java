package xuxue.weibo.sina.commen.util;

public class GetPageException extends Exception{
	
	public GetPageException(){
		super();
	}
	
	
	public GetPageException(String s){
		super(s);
	}
	
	public GetPageException(Exception e){
		super(e);
	}
}
