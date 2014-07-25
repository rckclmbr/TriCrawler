package pages.data;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 19, 2011
 * Time: 9:50:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemData {

    public String sku;
    public String stock;

    public DBObject getDBObject() {
        DBObject obj = new BasicDBObject();
        obj.put("sku", sku);
        obj.put("stock", stock);
        return obj;
    }

    public String toString() {
        return "<ItemData"
                + " sku=" + sku
                + " stock=" + stock
                + ">";
    }
    
}
