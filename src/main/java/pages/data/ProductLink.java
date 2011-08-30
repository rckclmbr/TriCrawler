package pages.data;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 11:25:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductLink {
    public String url;
    public String title;
    public String price;
    public String smallImage;
    public String largeImage;
    public boolean isNew;
    public String discount;
    public String prodId;

    public DBObject getDbObject() {
        DBObject obj = new BasicDBObject();
        obj.put("url", url);
        obj.put("title", title);
        obj.put("discount", discount);
        obj.put("price", price);
        obj.put("smallImage", smallImage);
        obj.put("largeImage", largeImage);
        obj.put("isNew", isNew);
        obj.put("prodId", prodId);
        obj.put("isProduct", !prodId.equals(""));
        return obj;
    }
}
