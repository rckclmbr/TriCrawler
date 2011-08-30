package util;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 11:00:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextUtil {

    public static String fixUrl(String url) {            
        if (url.contains("http")) {
            return url;
        } else {
            return "http://www.trisports.com/"+url;
        }
    }
}
