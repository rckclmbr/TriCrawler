package pages;

import com.mongodb.DBObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pages.data.CategoryLink;
import pages.data.ProductLink;
import util.TextUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 7:47:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubcategoryPage extends Page {
    public SubcategoryPage(String url, Document d) {
        super(url, d);
    }

    @Override
    protected void persistContent(DBObject m) {
        m.put("type", "subcategory");

        List<DBObject> list = new LinkedList<DBObject>();
        for (ProductLink l : this.getProducts()) {
            list.add(l.getDbObject());
        }
        m.put("products", list);

//        List<DBObject> categories = new LinkedList<DBObject>();
//        for (ProductLink l : this.getProducts()) {
//            categories.add(l.getDbObject());
//        }
//        m.put("categories", categories);
    }

    public List<CategoryLink> getCategories() {
        List<ProductLink> products = this.getProducts();
        List<CategoryLink> links = new ArrayList<CategoryLink>();

        for (ProductLink p : products) {
            if (p.prodId.equals("")) {
                CategoryLink c = new CategoryLink();
                c.image = p.smallImage;
                c.title = p.title;
                c.url = p.url;
                links.add(c);
            }
        }
        return links;
    }


    public List<ProductLink> getProducts() {
        Elements links = doc.getElementsByTag("script");

        // Get the right javascript tag

        String jsdata = null;
        for (Element l : links) {
            if (!l.hasAttr("href") && l.html().contains("window.item")) {
                jsdata = l.html();
                break;
            }
        }

        // Cleanup!

        assert jsdata != null;
        int index = jsdata.indexOf("var addToCartImage =");
        jsdata = jsdata.substring(0, index);
        jsdata = jsdata.replaceAll("onmouseover=\\\\\"[^\"]+\\\\\"", "");
        jsdata = jsdata.replaceAll("onmouseover=\"[^\"]+\"", "");
        jsdata = jsdata.replaceAll("onmouseout=\\\\\"[^\"]+\\\\\"", "");
        jsdata = jsdata.replaceAll("onmouseout=\"[^\"]+\"", "");        
        jsdata = jsdata.replaceAll("onClick=\\\\\"[^\"]+\\\\\"", "");

        // Split into separate items

        String[] items = jsdata.split(";");

        List<ProductLink> catlinks = new LinkedList<ProductLink>();

        for (String item : items) {
            item = item.replaceFirst(" ?window.item\\d+=new pagingItem", "");
            String[] att = item.split(",");
            if (att.length < 11)
                continue;


            ProductLink p = new ProductLink();

            String smallImage = Jsoup.parse(att[3]).getElementsByTag("img").attr("src");
            String largeImage = Jsoup.parse(att[4]).getElementsByTag("img").attr("src");

            p.prodId = strip(att[1]);
            p.title = strip(att[2]);
            p.url = TextUtil.fixUrl( strip(att[0]).substring(1).toLowerCase()+".html" );
            p.price = att[5];
            p.smallImage = smallImage;
            p.largeImage = largeImage;
            p.isNew = Boolean.parseBoolean(att[10]);
            p.discount = att[6];
            
            catlinks.add(p);
        }

        return catlinks;
    }

    private String strip(String str) {
        if (str.length() > 1)
            return str.substring(1, str.length()-1);
        return "";
    }


}
