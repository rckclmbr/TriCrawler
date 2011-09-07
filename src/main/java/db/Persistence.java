package db;

import com.mongodb.*;
import org.json.JSONObject;

import java.io.*;
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
    
    private String directory = "/var/www/d/";

    private static Persistence instance;
    //private DB db;

    public static Persistence getInstance() {
        if (instance == null) {
            instance = new Persistence();
        }
        return instance;
    }

    //public DB getDB() {
    //    return db;
    //}

    public void savePage(String url, String document) {
        //DB db = Persistence.getInstance().getDB();
        //DBCollection coll = db.getCollection("cachedata");
        // Calendar cal = Calendar.getInstance();
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
        // String date = sdf.format(cal.getTime());
        // 
        // 
        // BasicDBObject m = new BasicDBObject();
        // m.put("time", date);
        // m.put("url", url);
        // m.put("document", document);
        // 
        // this.update(coll, "url", url, m);


    }

    public void update(String key, String url, DBObject obj) {
        JSONObject o = new JSONObject(obj.toMap());
        String filename = this.directory + url.replaceFirst("http:\\/\\/", "");
        
        if (filename.equals(this.directory+"www.trisports.com/")) {
            filename = this.directory+"www.trisports.com/index.html";
        }
        
        System.out.println(filename);
        
        try { 
            FileWriter wr = new FileWriter(filename);
            wr.write(o.toString());
            wr.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
