import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.niocchi.core.Crawler;
import org.niocchi.core.URLPool;
import org.niocchi.core.URLPoolException;
import org.niocchi.core.Worker;
import org.niocchi.core.resource.ResourceCreator;
import org.niocchi.core.resource.ResourceException;
import org.niocchi.core.resource.ResourcePool;
import org.niocchi.monitor.Monitor;
import org.niocchi.monitor.Monitorable;
import org.niocchi.resources.HTMLResourceCreator;
import org.niocchi.urlpools.SimpleListURLPool;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple crawler given as an implementation exemple.<br>
 * Takes a file with one url per line as an input.
 *
 * @author Iv‡n de Prado, FL Mommens
 *
 */
public class GenericCrawler implements Monitorable
{
    protected static final String CONFIG_PATH = "crawl";
    private static final String USAGE = "GenericCrawler seed_file\n";

    static Logger log = Logger.getLogger(GenericCrawler.class);

    int resourcesCount = 5 ; // number of url that can be crawled simultaneously
    int monitorPort = 6001 ;
    String userAgent = "firefox 3.0" ;
    String seedFile = null;

    // Chicha classes
    URLPool urlPool;
    Crawler crawler;
    ResourcePool resPool;
    Worker worker;


    public void init( ) throws IOException
    {

        ResourceCreator resCreator = new HTMLResourceCreator();
        ResourcePool resPool = new ResourcePool(resCreator, resourcesCount);

        // create the worker
        crawler = new Crawler(resPool);
        crawler.setUserAgent(userAgent ) ;
        crawler.setTimeout(20000);


        List<String> list = new ArrayList<String>();
        list.add("http://www.trisports.com");
//        urlPool = new SimpleListURLPool(list);
        
        // create the url pool
        urlPool =  new OngoingUrlPool("http://www.trisports.com/");


        // --- create the worker
        worker = new OngoingWorker(crawler, "/tmp") ;

        System.out.printf("Resource count [%d]\n", resourcesCount);
        System.out.printf("Monitor listening in port [%d]\n", monitorPort);
        System.out.printf("Crawler timeout[%d] User-Agent: %s\n", crawler.getTimeout(), crawler.getUserAgent());
    }

    // ------------------------------------------------------------
    public void crawl() throws IOException, InterruptedException,
                               ResourceException, URLPoolException
    {
        // start workers
        worker.start();

        // start monitoring
        Monitor monitor = new Monitor(monitorPort);
        monitor.addMonitored(this);
        monitor.start();

        // start crawler
        System.out.println("Starting crawler");
        crawler.run(urlPool);

        // wait for workers to finish
        worker.join();
    }

    // ------------------------------------------------------------
    public void printMonitoredState(PrintStream out_) {
        crawler.printMonitoredState(out_);
    };

    public void dump() {}

    protected void help() {
        System.out.println(USAGE);
    }

    public void execute(String argv_[]) throws Exception
    {


        this.init();
        this.crawl();

        System.out.println(this.crawler.processed_count + " URL processed");
        System.out.println(this.crawler.status_200 + " with status 200");
        System.out.println(this.crawler.redirected_count + " redirections");
        System.out.println(this.crawler.status_other + " other status");
        System.out.println(this.crawler.incomplete_count + " incomplete");

        System.exit(0);
    }

    // ------------------------------------------------------------
    public static void main(String argv_[]) throws Exception
    {
        BasicConfigurator.configure();
        GenericCrawler crawler = new GenericCrawler();
        crawler.execute(argv_);
    }
}
