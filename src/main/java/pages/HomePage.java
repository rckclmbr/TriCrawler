package pages;

import com.mongodb.DBObject;
import org.jsoup.nodes.Document;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 7:42:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class HomePage extends Page {
    public HomePage(String url, Document d) {
        super(url, d);
    }

    @Override
    protected void persistContent(DBObject m) {
        m.put("type", "home");
    }
}
