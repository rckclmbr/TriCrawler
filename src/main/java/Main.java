import org.apache.log4j.BasicConfigurator;
import org.niocchi.core.Crawler;
import org.niocchi.core.MemoryResourceFactory;
import org.niocchi.core.ResourceException;
import org.niocchi.core.URLPool;
import org.niocchi.core.URLPoolException;
import org.niocchi.core.Worker;
import org.niocchi.monitor.Monitor;
import org.niocchi.monitor.Monitorable;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: jbraegger
 * Date: 7/24/14
 */
public class Main implements Monitorable {

    protected static final String CONFIG_PATH = "crawl";
    private static final String USAGE = "GenericCrawler seed_file\n";

    static Logger log = Logger.getLogger(Main.class.getSimpleName());

    private int resourcesCount = 5; // number of url that can be crawled simultaneously
    private int monitorPort = 6001;
    private String userAgent = "firefox 3.0" ;

    // Chicha classes
    private URLPool urlPool;
    private Crawler crawler;
    private Worker worker;


    public void init() throws IOException {

        // create the worker
        this.crawler = new Crawler(new MemoryResourceFactory(), this.resourcesCount);
        crawler.setUserAgent(userAgent);
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
        System.out.printf("Crawler timeout[%d] User-Agent: %s\n", crawler.getReadTimeout(), crawler.getUserAgent());
    }

    // ------------------------------------------------------------
    public void crawl() throws IOException, InterruptedException,
            ResourceException, URLPoolException {
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
    }

    public void dump() {
    }

    protected void help() {
        System.out.println(USAGE);
    }

    public void execute(String argv_[]) throws Exception {
        this.init();
        this.crawl();

        System.out.println(this.crawler.processed_count + " URL processed");
        System.out.println(this.crawler.status_200 + " with status 200");
        System.out.println(this.crawler.redirected_count + " redirections");
        System.out.println(this.crawler.status_other + " other status");
        System.out.println(this.crawler.incomplete_count + " incomplete");

        System.exit(0);
    }

    public static void main(String argv_[]) throws Exception {
        BasicConfigurator.configure();
        Main crawler = new Main();
        crawler.execute(argv_);
    }
}
