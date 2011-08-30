package db;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 8:19:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class Persistence {

    private static Persistence instance;
    private DB db;

    public static Persistence getInstance() {
        if (instance == null) {
            try {
                instance = new Persistence();

                Mongo m = new Mongo("localhost");
                instance.db = m.getDB("data");

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public DB getDB() {
        return db;
    }

    public void savePage(String url, String document) {
        DB db = Persistence.getInstance().getDB();
        DBCollection coll = db.getCollection("cachedata");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
        String date = sdf.format(cal.getTime());


        BasicDBObject m = new BasicDBObject();
        m.put("time", date);
        m.put("url", url);
        m.put("document", document);

        this.update(coll, "url", url, m);

    }

    public void update(DBCollection coll, String key, String value, DBObject obj) {
        BasicDBObject query = new BasicDBObject();
        query.put(key, value);
        DBObject o = coll.findAndModify(query, obj);
        if (o == null) {
            coll.insert(obj);
        }
    }
    
}
