package pages;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import pages.data.CategoryLink;
import util.TextUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 7:40:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryPage extends Page {


    public CategoryPage(String url, Document d) {
        super(url, d);
    }

    @Override
    protected void persistContent(DBObject m) {
        m.put("type", "category");

        List<DBObject> list = new LinkedList<DBObject>();
        for (CategoryLink l : this.getLinkedCategories()) {
            list.add(l.getDbObject());
        }
        m.put("categories", list);
    }

    public List<CategoryLink> getLinkedCategories() {
        Elements links = doc.getElementById("catcontents").getElementsByClass("sectionholderim").select("a[href]");
        List<CategoryLink> catlinks = new LinkedList<CategoryLink>();

        List<String> urls = new ArrayList<String>();
        for (Element l : links) {
            CategoryLink link = new CategoryLink();

            String url = l.attr("href");
            url = TextUtil.fixUrl(url);

            link.url = url;
            link.title = Jsoup.parse( l.getElementsByTag("img").attr("alt") ).text();
            link.image = l.getElementsByTag("img").attr("src");

            catlinks.add(link);

        }

        return catlinks;
    }
    
}
