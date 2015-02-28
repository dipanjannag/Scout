package com.newsio.core;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.newsio.types.NewsStorageItem;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class CNNCrawler extends WebCrawler {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpe?g"
			+ "|png|mp3|mp3|zip|gz))$");

	/**
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 * In this example, we are instructing the crawler to ignore urls that
	 * have css, js, git, ... extensions and to only accept urls that start
	 * with "http://www.ics.uci.edu/". In this case, we didn't need the
	 * referringPage parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& href.contains("cnn.com");
	}

	private void ParseNews(Page page)
	{
		String headline = null;
		String news = null;
		String dateText = null;
		String path = page.getWebURL().getPath().toLowerCase() ;
		String url = null;
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		String htm = htmlParseData.getHtml();
		Document doc = Jsoup.parse(htm);
		String className = "body-text";
		Element elemBody = doc.getElementById(className);
		if (elemBody!=null) {
			//System.out.println("yes! thats a news!");
			news = elemBody.text();
		}
		else
		{
			//System.out.println("It isn't a news page");
			return;
		}
		String id = "pg-headline";
		Element elem = doc.getElementsByClass(id).first();
		headline = elem.text();
		Element dateElem = doc.getElementsByClass("update-time").first();
		dateText = dateElem.text();
		url = page.getWebURL().toString().toLowerCase();
		NewsStorageItem item = new NewsStorageItem();
		item.setDateStr(dateText);
		item.setUrl(url);
		item.setHeadLine(headline);
		item.setDetailsNews(news);
		item.setPath(path);
		item.setSource("cnn");
		synchronized (ScoutController.printerLock) {
			//System.out.println(headline+"\t"+news+"\t"+path+"\tbbc");
			System.out.println(item.toString());
		}
	}
	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
		//System.out.println("Visiting page "+ page.getWebURL().toString());
		this.ParseNews(page);
		//System.out.println("Visiting page "+ page.getWebURL().toString());
	}

}
