package xuxue.weibo.sina.commen.util;

/**
 * 登录新浪微博会发生的异常
 * 
 * @author LM
 *
 */
public class LoginException extends Exception{

	/**
	 * 第二版本
	 */
	private static final long serialVersionUID = 2L;

	public LoginException(String message){
		super(message);
	}
	
	public LoginException(Exception e){
		super(e);
	}
	
	public LoginException(){
		super();
	}
}
