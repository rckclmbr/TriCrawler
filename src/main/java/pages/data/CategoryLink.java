package pages.data;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 10:51:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryLink {

    public String url;
    public String title;
    public String image;

    public DBObject getDbObject() {
        DBObject obj = new BasicDBObject();
        obj.put("url", url);
        obj.put("title", title);
        obj.put("image", image);
        return obj;
    }
}
