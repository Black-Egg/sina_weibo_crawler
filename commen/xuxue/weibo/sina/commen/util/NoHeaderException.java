package xuxue.weibo.sina.commen.util;

public class NoHeaderException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoHeaderException(){
		super();
	}
	
	public NoHeaderException(String s){
		super(s);
	}
	
	public NoHeaderException(Exception e){
		super(e);
	}
}
