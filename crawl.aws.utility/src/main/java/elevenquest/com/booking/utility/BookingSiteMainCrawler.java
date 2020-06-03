package elevenquest.com.booking.utility;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.htmlcleaner.TagNode;

import elevenquest.com.booking.HouseInfo;
import elevenquest.com.crawl.BaseCrawler;

public class BookingSiteMainCrawler extends BaseCrawler {
  
  public static final String SITE_MOTEL_MAIN = "https://www.yanolja.com/top-adver/r-%6d/keyword-motel?advert=AREA&rentType=0&stayType=0&advertsPosition=AREA_TOP";
  public static final String SITE_HOUSE_ELEM_XPATH = "//*[@id=\"__next\"]/div[2]/section/div/div/div[]/a";

  public static String getTop100UriFromPosition(int position) {
    return String.format(SITE_MOTEL_MAIN, position);
  }

  public static List<HouseInfo> getHouseInfos(String top100uri) throws Exception {
    List<HouseInfo> rtn = new Vector<HouseInfo>();
    TagNode rootNode = cleanHtml(crawlSite(top100uri));
    TagNode[] houseInfos = evalList(rootNode, SITE_HOUSE_ELEM_XPATH);
    Arrays.stream(houseInfos).forEach(houseNode -> {
      HouseInfo oneItem = new HouseInfo();
      oneItem.title = houseNode.getAttributeByName("title");
      oneItem.relUriPath = houseNode.getAttributeByName("href");
      rtn.add(oneItem);
    });
    return rtn;
  }

  public static void main(String[] args) throws Exception {
    List<HouseInfo> houses = getHouseInfos(getTop100UriFromPosition(910001));
    houses.forEach(house -> System.out.println(house.title + ":" + house.relUriPath));
  }

}