package pages;

import com.mongodb.DBObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pages.data.ItemData;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 7:40:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductPage extends Page {
    public ProductPage(String url, Document d) {
        super(url, d);
    }

    public String toString() {
        return "<ProductPage id:" + getItemId()
                + " pageName:" + getPageName()
                + ">";
    }

    protected String getItemId() {
        return doc.getElementById("itemcode").text().replace("Item #: ", "");
    }

    protected String getPageName() {
        return doc.getElementById("pagename").text();
    }

    protected String getDescriptionHtml() {
        return doc.getElementById("captioninner").html();
    }

    protected String getPrice() {
        Element el = doc.getElementById("itempriceblue");
        if (el == null)
            el = doc.getElementById("itempricegrey");
        if (el == null)
            return doc.getElementById("ysw-quantity-table").getElementsByClass("ysw-quantity-pricing-price").get(0).text().replace("$","");
        return el.text().replace("Regular price: $","");
    }

    protected String getDiscountPrice() {
        Element el = doc.getElementById("itemsaleprice");
        if (el == null)
            return "";
        return el.getElementsByClass("redtext").get(0).text().replace("$","");
    }

    protected String getAvailability() {
        Elements order = doc.getElementById("orderinner").getElementsByClass("orderleft");

        for (Element i : order) {
            if (i.text().equals("Availability:")) {
                return i.nextElementSibling().text();
            }
        }
        return "";
    }


    @Override
    protected void persistContent(DBObject m) {
        m.put("type", "product");

        m.put("images", this.getProductImages());
        m.put("description_html", getDescriptionHtml());
        m.put("title", getPageName());
        m.put("availability", getAvailability());
        m.put("item_id", getItemId());
        m.put("price", getPrice());
        m.put("discount_price", getDiscountPrice());

        List<DBObject> objects = new LinkedList<DBObject>();
        for (ItemData d : getItemData()) {
            objects.add(d.getDBObject());
        }

        m.put("items", objects);

        if (objects.size() == 0) {
            m.put("stock_status", getProductStockStatus( getItemId() ));
        }

    }

    public List<ItemData> getItemData() {
        Elements options = doc.getElementById("options").getElementsByTag("option");

        List<ItemData> ret = new ArrayList<ItemData>();
        List<String> skus = new LinkedList<String>();

        for (Element i : options) {
            if (i.attr("value").equals("Please Select"))
                continue;

            ItemData d = new ItemData();
            //d.inStock = i.text().contains("(in stock)");
            d.sku = i.attr("value");

            ret.add(d);
            skus.add(d.sku);
        }

        String formName = doc.getElementById("orderinner").parent().attr("name");
        String code = getItemId();

        Map<String, String> stock = getStockStatus(formName, code, skus);


        for (ItemData d : ret) {
            d.stock = stock.get(d.sku);
        }

        return ret;

    }

    public List<String> getProductImages() {
        Elements links = doc.getElementById("itemimage").getElementsByAttributeValue("rel", "lightbox[item]");


        List<String> urls = new ArrayList<String>();
        for (Element l : links) {


            String url = l.attr("href");

            if (!urls.contains(url))
                urls.add(url);

        }

        return urls;
    }

    protected String getProductStockStatus(String code) {

        String url = "http://www.kingwebtools.com/tri_sports/yahoo_mom/product_inventory_single-new.php?code="+code;


        MultiThreadedHttpConnectionManager connectionManager =
            new MultiThreadedHttpConnectionManager();
        HttpClient client = new HttpClient(connectionManager);
        HttpMethod m = new GetMethod(url);


        try {
            int statusCode = client.executeMethod(m);

            if (statusCode != 200) {
                System.err.println("Status was not 200");
                return "";
            }

            String res = m.getResponseBodyAsString();

            Pattern pattern = Pattern.compile("'([^']*)';$");
            Matcher ma = pattern.matcher(res);

            if (ma.find()) {

                String ret = ma.group(1).toLowerCase();
                if (ret != null) return ret;
            }

        }
        catch (IOException e) {
            e.printStackTrace(System.err);
        }
        finally {
            m.releaseConnection();
        }

        return "";
    }


    protected Map<String, String> getStockStatus(String formName, String code, List<String> options) {

        Map<String, String> ret = new HashMap<String,String>();

        // Set default (OOS)

        for (int i=0; i<options.size(); i++) {
            ret.put(options.get(i), "");
        }

        if (options.size() == 0)
            return ret;

        String data = "";
        try {
            data = URLEncoder.encode( StringUtils.join(options, "~~~"), "latin1" );
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }


        String url = "http://www.kingwebtools.com/tri_sports/yahoo_mom/product_inventory.php?" +
                "form_name="+formName+"&" +
                "code="+code+"&" +
                "select_name=Size&" +
                "data=Please%20Select~~~"+data;

        MultiThreadedHttpConnectionManager connectionManager =
            new MultiThreadedHttpConnectionManager();
        HttpClient client = new HttpClient(connectionManager);
        HttpMethod m = new GetMethod(url);

        try {
            int statusCode = client.executeMethod(m);

            if (statusCode != 200) {
                return ret;
            }

            String res = m.getResponseBodyAsString();


            res = res.substring(res.indexOf("CheckOptionValues;")+18);

            if (res.isEmpty()) {
                return ret;
            }



            String[] items = res.split(";");

            int j = 0;

            try {

                for (String i : items) {


                    Pattern pattern = Pattern.compile("'\\s*(.*)'");
                    Matcher ma = pattern.matcher(i);

                    String stock = "";

                    if (ma.find()) {
                        stock = ma.group(1);
                        if (stock == null) stock = "";
                    }

                    ret.put(options.get(j), stock);

                    j++;
                }

            }
            catch (Exception e) {
                e.printStackTrace(System.err);
                System.err.println("Error with items: "+res);
                
            }
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            m.releaseConnection();
        }

        return ret;
    }

}
