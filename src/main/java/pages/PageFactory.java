package pages;

import org.jsoup.nodes.Document;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 13, 2011
 * Time: 7:40:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageFactory {

    public static Page determinePage(String url, Document d) {

        if (d.getElementById("homemainarea") != null) {
            return new HomePage(url, d);
        }
        else if (d.getElementById("catcontents") != null) {
            return new CategoryPage(url, d);
        }
        else if (d.getElementById("contentsbox") != null) {
            return new SubcategoryPage(url, d); 
        }
        else if (d.getElementById("itemimage") != null) {
            return new ProductPage(url, d);
        }

        return new Page(url, d);
        
    }
}
