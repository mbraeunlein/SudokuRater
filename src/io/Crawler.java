package io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler {
	public Crawler() {
		try {
			java.util.logging.Logger.getLogger("com.gargoylesoftware")
					.setLevel(java.util.logging.Level.OFF); /*
															 * comment out to
															 * turn off annoying
															 * htmlunit warnings
															 */
			WebClient webClient = new WebClient();
			String url = "http://view.websudoku.com/?";
			System.out.println("Loading page now: " + url);
			HtmlPage page = webClient.getPage(url);
			System.out.println(page.asXml());
			webClient.waitForBackgroundJavaScript(30 * 1000); /*
															 * will wait
															 * JavaScript to
															 * execute up to 30s
															 */

			for(int i = 0; i < 9; i++) {
				for(int j = 0; j < 9; j++) {
					System.out.println(page.getElementById("c" + i + j).getTextContent());
				}
			}

			// System.out.println("#FULL source after JavaScript execution:\n "+pageAsXml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}