package pages;

import org.junit.Test;
import org.junit.Assert;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import pages.data.CategoryLink;
import pages.data.ProductLink;
import com.mongodb.DBObject;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 9:10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubcatTest {

    @Test
    public void testPage() throws IOException {
        SubcategoryPage p = getTestPage("test_subcat.html");
        List<ProductLink> links = p.getProducts();

        Assert.assertEquals(36, links.size());

    }

    @Test
    public void testPage2() throws IOException {
        SubcategoryPage p = getTestPage("test_subcat2.html");

        List<ProductLink> links = p.getProducts();

        Assert.assertEquals(7, links.size());

    }

    @Test
    public void testPageErrors() throws IOException {
        SubcategoryPage p = getTestPage("test_subcat_errors.html");

        List<ProductLink> links = p.getProducts();

        List<DBObject> objects = new ArrayList<DBObject>();

        for (ProductLink pi : links)
            if (pi.getDbObject().get("isProduct") != "false")
                objects.add(pi.getDbObject());

        

        Assert.assertEquals(11, links.size());

    }

    private SubcategoryPage getTestPage(String name) throws IOException {
        InputStream in =
            getClass().getClassLoader().getResourceAsStream(name);

        Document doc = Jsoup.parse(in, "UTF-8", "http://www.trisports.com/");

        return new SubcategoryPage("test.html", doc);
    }
}