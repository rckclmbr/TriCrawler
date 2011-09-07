package pages;

import com.mongodb.*;
import db.Persistence;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class Page {

    Document doc;
    String url;


    public Page(String url, Document d) {
        doc = d;
        this.url = url;

    }

    public List<String> getPageLinks() {
        Elements links = doc.select("a[href]");

        List<String> urls = new ArrayList<String>();
        for (Element l : links) {
            String url = TextUtil.fixUrl( l.attr("href") );
            if (url.contains("www.trisports.com"))
                urls.add(url);
        }

        return urls;
    }

    public void persistPage() {
        //DB db = Persistence.getInstance().getDB();
        //DBCollection coll = db.getCollection("pages");
        BasicDBObject m = new BasicDBObject();

        m.put("title", doc.title());
        m.put("url", this.url);
        m.put("links", this.getPageLinks().toArray() );

        this.persistContent(m);

        Persistence.getInstance().update("url", url, m);
    }

    protected void persistContent(DBObject m) {
        m.put("type", "generic");
    }


}
