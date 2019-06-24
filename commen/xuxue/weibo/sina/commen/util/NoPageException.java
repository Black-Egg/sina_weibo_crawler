package xuxue.weibo.sina.commen.util;

/**
 * 在访问一个用户的微博或者评论的时候  有可能会超出页面的范围
 * 如果超出页面的范围  会抛出这个异常
 * @author LM
 *
 */
public class NoPageException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public NoPageException(){
		super();
	}
	
	public NoPageException(String message){
		super(message);
	}
	
	public NoPageException(Exception e){
		super(e);
		
	}
}
