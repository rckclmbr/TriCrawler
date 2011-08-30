import org.apache.log4j.Logger;
import org.niocchi.core.URLPool;
import org.niocchi.core.URLPoolException;
import org.niocchi.core.query.Query;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 5:30:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class OngoingUrlPool implements URLPool {

    static Logger log = Logger.getLogger(OngoingUrlPool.class);

    static ArrayList<String> urls = new ArrayList<String>();
    static HashSet<String> urlsVisited = new HashSet<String>();

    public OngoingUrlPool(String initial) {
        addNewUrl(initial);
    }

    public static void addNewUrl(String url) {
        if (!urlsVisited.contains(url)) {
            urls.add(url);
            urlsVisited.add(url);
        }
    }


    public boolean hasNextQuery() {
        return true;
//        return urls.size() > 0;
    }

    public Query getNextQuery() throws URLPoolException {
        String url = "";
        int numTries = 0;
        while (numTries < 15) {
            try {
                url = urls.remove(0);
                numTries = 0;
                Query q = new Query( url ) ;
                return q;
            } catch (IndexOutOfBoundsException e) {
                try {
                    Thread.sleep(2000);
                    numTries++;
                }
                catch (InterruptedException ef) {
                    // Nothing
                }
            } catch (MalformedURLException e) {
                log.error("Invalid URL: "+url);
//                throw new URLPoolException( "invalid url: "+url, e ) ;
            }
        }
        throw new URLPoolException( "no more urls" );
//        return null;
    }

    public void setProcessed(Query query) {
//        System.out.println("processed");
        /* NOTHING */
    }
}
