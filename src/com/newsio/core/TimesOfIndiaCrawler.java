package com.newsio.core;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.newsio.types.NewsStorageFile;
import com.newsio.types.NewsStorageItem;
import com.newsio.utility.DataTransporter;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class TimesOfIndiaCrawler extends WebCrawler {

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
				&& href.contains("indiatimes.com");
	}

	private void ParseNews(Page page)
	{
		String headline = null;
		String news = null;
		String path = page.getWebURL().getPath().toLowerCase() ;
		String dateStr = null;
		String url = null;
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		String htm = htmlParseData.getHtml();
		Document doc = Jsoup.parse(htm);
		String className = "arttle";
		Elements elems = doc.getElementsByClass(className);
		if(elems.size()!=0)
		{
			
			Element headlineHeading = elems.first();
			headline = headlineHeading.text();
			//System.out.println( headline );
		}
		// trying to extract text
		String idName = "artext1";
		Element textElem = doc.getElementById(idName);
		if (textElem!=null) {
			//System.out.println("Detail News");
			news = textElem.text();
			//System.out.println(news);
		}
		else
		{
			className = "section1";
			Element newsText = doc.getElementsByClass(className).first();
			news = newsText.text();
			//System.out.println(news);
		}
		url = page.getWebURL().toString().toLowerCase();
		Element dateElem = doc.getElementById("byline");
		dateStr = dateElem.text();
		NewsStorageItem item = new NewsStorageItem();
		item.setHeadLine(headline);
		item.setDetailsNews(news);
		item.setPath(path);
		item.setDateStr(dateStr);
		item.setUrl(url);
		item.setSource("timesOfIndia");
		synchronized (ScoutController.printerLock) {
			//System.out.println(headline+"\t"+news+"\t"+path+"\tTimesOfIndia");
			System.out.println(item.toString());
			System.out.println(",");
		}
	}
	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
		if(page.getWebURL().getPath().contains("mocktails"))
		{
			return;
		}
		if (page.getWebURL().getPath().toString().toLowerCase().endsWith(".cms")) {
			if (!page.isRedirect()) {
				//System.out.println(page.getWebURL().toString());
				//System.out.println("this page is news page");
				this.ParseNews(page);
			}

		}
		else
		{
			//System.out.println(page.getWebURL().toString());
			//System.out.println("This page is cat page");
		}
	}
}