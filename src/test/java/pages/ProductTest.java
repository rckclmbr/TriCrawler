package pages;

import org.junit.Test;
import org.junit.Assert;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;

import pages.data.ItemData;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 9:10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductTest {

    private ProductPage getTestPage(String name) throws IOException {
        InputStream in =
            getClass().getClassLoader().getResourceAsStream(name);

        Document doc = Jsoup.parse(in, "latin1", "http://www.trisports.com/");

        return new ProductPage("test.html", doc);
    }


    @Test
    public void testPage() throws IOException {

        ProductPage p = getTestPage("test_prod.html");
        
        List<String> images = p.getProductImages();
        List<ItemData> items = p.getItemData();

        Assert.assertEquals("XXS", items.get(0).sku);
        Assert.assertEquals("(in stock)", items.get(0).stock);

    }

    @Test
    public void testOutOfStockPage() throws IOException {
        ProductPage p = getTestPage("test_prod_oos.html");

        List<ItemData> items = p.getItemData();

        Assert.assertEquals("56", items.get(0).sku);
        Assert.assertEquals("", items.get(0).stock);
    }

    @Test
    public void testOneProductInStock() throws IOException {
        ProductPage p = getTestPage("test_prod_1prod.html");

        List<ItemData> items = p.getItemData();

        Assert.assertEquals(0, items.size());

        Assert.assertEquals("", p.getProductStockStatus("07-011101"));
        Assert.assertEquals("in stock", p.getProductStockStatus("27-501101"));
    }

    @Test
    public void testItemQuantity() {

        ProductPage p = new ProductPage("test.html", null);

        List<String> items = new ArrayList<String>();
        items.add("XXS");
        items.add("XS");
        items.add("SM");
        items.add("MS");
        items.add("ML");
        items.add("LG");
        items.add("XL");
        items.add("XXL");

        Map<String,String> data = p.getStockStatus("form-as-ironman-phantom-wetsuit", "07-011101", items);

        Assert.assertEquals("(out of stock)", data.get("XS"));

    }

    @Test
    public void testGetPrice() throws IOException {
        ProductPage p = getTestPage("test_prod.html");
        Assert.assertEquals("649.95", p.getPrice());
        Assert.assertEquals("", p.getDiscountPrice());
    }

    @Test
    public void testGetPriceDiscount() throws IOException {
        ProductPage p = getTestPage("test_prod_discount.html");
        Assert.assertEquals("699.00", p.getPrice());
        Assert.assertEquals("449.47", p.getDiscountPrice());

    }

    @Test
    public void testGetPriceDiscountPricing() throws IOException {
        ProductPage p = getTestPage("test_prod_discount_pricing.html");
        Assert.assertEquals("4.95", p.getPrice());
        Assert.assertEquals("", p.getDiscountPrice());

    }

    @Test
    public void testDescriptionEncoding() throws IOException {
        ProductPage p = getTestPage("test_prod_encoding.html");
        String d = p.getDescriptionHtml();
        
        Assert.assertEquals("The Camelbak Ergo Hydrolock has a 90-degree bend that puts the Big Bite&ordf; Valve into an ergonomic " +
                "position for drinking. With a simple flip, the on/off mechanism can be activated or " +
                "shut off. Fits most Camelbak&ordf; hydration systems.\n" +
                "<br />\n" +
                "<br /> \n" +
                "<i><font size=\"-2\">Shipping Weight: 0.25 lb.</font></i>\n" +
                "<br />\n" +
                "<br />", d);
    }
}