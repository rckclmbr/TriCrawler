import db.Persistence;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.niocchi.core.Crawler;
import org.niocchi.core.MemoryResource;
import org.niocchi.core.Query;
import org.niocchi.core.Worker;
import pages.Page;
import pages.PageFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 *
 * @author FL Mommens
 *
 */
public class OngoingWorker extends Worker {

    static Logger log = Logger.getLogger(OngoingWorker.class);
    String savePath ;

    public OngoingWorker( Crawler crawler, String savePath ) {
        super( crawler );
        this.savePath = savePath ;
    }


    public void processResource( Query query ) {
        try {

            if (query.getResource().getHTTPStatus() != 200) {
                return;
            }

            String fileName = query.getURL().getFile();
            if (fileName.length() == 0 ) fileName = "index.html";
            String host = query.getHost();

            MemoryResource resource = (MemoryResource) query.getResource();
            InputStream data = new ByteArrayInputStream(resource.getBytes());
            Document doc = Jsoup.parse(data, "latin1", "http://www.trisports.com/");

            
            Page p = PageFactory.determinePage(query.getURL().toString(), doc);

            // Add new urls
            for (String url : p.getPageLinks())
                OngoingUrlPool.addNewUrl(url);

            // Parse the document
            p.persistPage();

            // Save the (pretty) data
            Persistence.getInstance().savePage(query.getURL().toString(), doc.html());

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
