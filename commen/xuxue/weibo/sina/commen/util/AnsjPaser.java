package xuxue.weibo.sina.commen.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AnsjPaser {

	private String beginRegex;

	private String endRegex;

	private Matcher matcher;

	public final static String TEXTTEGEX = ".*?";

	public final static String W = "\\W*?";

	public final static String N = "";

	public final static String TEXTEGEXANDNRT = "[\\s\\S]*?";
	public final static String zel_all_chars = "[\\s\\S]*";

	private List<String> filterRegexList = new ArrayList<String>();

	public AnsjPaser(String beginRegex, String endRegex, String content,
			String textRegex) {

		this.beginRegex = beginRegex;

		this.endRegex = endRegex;

		StringBuilder sb = new StringBuilder();

		sb.append(beginRegex);

		sb.append(textRegex);

		sb.append(endRegex);
		matcher = Pattern.compile(sb.toString()).matcher(content);
	}

	public AnsjPaser(String beginRegex, String textRegex, String endRegex,
			String content, String flag) {
		this.beginRegex = beginRegex;

		this.endRegex = endRegex;

		StringBuilder sb = new StringBuilder();

		sb.append(beginRegex);

		sb.append(textRegex);

		sb.append(endRegex);
		// System.out.println("sb--------------" + sb);
		matcher = Pattern.compile(sb.toString()).matcher(content);
	}

	public AnsjPaser(String beginRegex, String endRegex, String textRegex) {

		this.beginRegex = beginRegex;

		this.endRegex = endRegex;

		StringBuilder sb = new StringBuilder();

		sb.append(beginRegex);

		sb.append(textRegex);

		sb.append(endRegex);
		matcher = Pattern.compile(sb.toString()).matcher(N);
	}

	public AnsjPaser(String beginRegex, String endRegex) {

		this.beginRegex = beginRegex;

		this.endRegex = endRegex;

		StringBuilder sb = new StringBuilder();

		sb.append(beginRegex);

		sb.append(TEXTTEGEX);

		sb.append(endRegex);

		matcher = Pattern.compile(sb.toString()).matcher(N);
	}

	public String getSimpleText() {
		if (matcher.find()) {
			String str = matcher.group().trim();
			return str;
		}
		return null;
	}

	public String getText() {
		if (matcher.find()) {
			String str = matcher.group().trim().replaceFirst(beginRegex, N)
					.replaceAll(endRegex, N);
			Iterator<String> it = filterRegexList.iterator();
			while (it.hasNext()) {
				str = str.replaceAll(it.next(), N);
			}
			return str;
		}
		return null;
	}

	public String getLastText() {
		String str = null;
		while (matcher.find()) {
			str = matcher.group().trim().replaceFirst(beginRegex, N)
					.replaceAll(endRegex, N);
		}
		return str;
	}

	public String getNext() {
		return matcher.group();
	}

	public String getNextTxt() {
		String str = matcher.group().trim().replaceFirst(beginRegex, N)
				.replaceAll(endRegex, N);
		Iterator<String> it = filterRegexList.iterator();
		while (it.hasNext()) {
			str = str.replaceAll(it.next(), N);
		}
		return str;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getNextAddFilter() {
		String str = matcher.group();
		Iterator<String> it = filterRegexList.iterator();
		while (it.hasNext()) {
			str = str.replaceAll(it.next(), N);
		}
		return str;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getNextText() {
		String str = matcher.group();
		str = str.replaceFirst(beginRegex, N).replaceAll(endRegex, N);
		return str;
	}

	public boolean hasNext() {
		return matcher.find();
	}

	public AnsjPaser reset(String content) {
		this.matcher.reset(content);
		return this;
	}

	public AnsjPaser addFilterRegex(String filterRegex) {
		filterRegexList.add(filterRegex);
		return this;
	}

	public String getTextList() {
		String str = "";
		int count = 0;
		while (matcher.find()) {
			if (count == 0) {
				str = matcher.group().trim().replaceFirst(beginRegex, N)
						.replaceAll(endRegex, N);
			} else {
				str += ("#" + matcher.group().trim()
						.replaceFirst(beginRegex, N).replaceAll(endRegex, N));
			}
			count++;
		}
		return str;
	}

	public String getTextList4QQ() {
		String str = "";
		int count = 0;
		while (matcher.find()) {
			if (count == 0) {
				str = matcher.group().trim().replaceFirst(beginRegex, N)
						.replaceAll(endRegex, N);
				str = getIdByUrl(str);
			} else {
				str += ("#" + getIdByUrl(matcher.group().trim()
						.replaceFirst(beginRegex, N).replaceAll(endRegex, N)));
			}
			count++;
		}
		return str;
	}
	
	public static String getIdByUrl(String url) {
		String suffix_str = null;
		url=url.trim();
		if (url.lastIndexOf("/") == url.length() - 1) {
			url = url.substring(0, url.length() - 1);
			suffix_str = url.substring(url.lastIndexOf('/') + 1, url.length());
		} else {
			suffix_str = url.substring(url.lastIndexOf('/') + 1, url.length());
		}
		return suffix_str;
	}

	public static void main(String[] args) {
		String beginRegex = "<dd" + AnsjPaser.TEXTEGEXANDNRT + "</a>";
		String endRegex = "<span>";
		String text = "<dd>    <a a b c>1</a>//@<a b c d>2</a>3 4<span>";
		AnsjPaser ansjSayUrl = new AnsjPaser(beginRegex, endRegex, text,
				AnsjPaser.TEXTEGEXANDNRT);

		System.out.println(ansjSayUrl.getText());

	}

}
