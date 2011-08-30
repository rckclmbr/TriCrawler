package pages;

import org.junit.Test;
import org.junit.Assert;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 9:10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageTest {

    @Test
    public void testPage() throws IOException {
        InputStream in =
            getClass().getClassLoader().getResourceAsStream("test_subcat.html");

        Document doc = Jsoup.parse(in, "UTF-8", "http://www.trisports.com/");

        Page p = new Page("test.html", doc);
        List<String> links = p.getPageLinks();
        
        Assert.assertTrue( "Links contain 'blackburn-airstick.html'",
                links.contains("http://www.trisports.com/blackburn-airstick.html") );
    }
}