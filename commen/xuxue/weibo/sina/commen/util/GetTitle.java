package xuxue.weibo.sina.commen.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetTitle {
	
	public static final String EMPTY_STRING = "";
	
	public final static String all_text_regex = "[\\s\\S]*?";
	
	private String title_regex = "<title>" + all_text_regex + "</title>";

	private Pattern title_pattern = Pattern.compile(title_regex);
	
	private Matcher matcher = null;
	
	private String temp = null;

	public String getTitleByLine(String line) {
		matcher = this.title_pattern.matcher(line);
		if (matcher.find()) {
			temp = matcher.group();
			return temp.replace("<title>", EMPTY_STRING).replace("</title>",
					EMPTY_STRING);
		}
		return null;
	}
}
