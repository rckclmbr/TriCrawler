package pages;

import org.junit.Test;
import org.junit.Assert;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;

import pages.data.CategoryLink;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 9:10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryTest {

    private CategoryPage getTestPage(String name) throws IOException {
        InputStream in =
            getClass().getClassLoader().getResourceAsStream(name);

        Document doc = Jsoup.parse(in, "UTF-8", "http://www.trisports.com/");

        return new CategoryPage("test.html", doc);
    }

    @Test
    public void testPage() throws IOException {
        CategoryPage p = getTestPage("test_cat.html");
        List<CategoryLink> links = p.getLinkedCategories();
                Assert.assertEquals(7, links.size());

    }

}