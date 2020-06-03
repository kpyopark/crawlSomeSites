package elevenquest.com.booking.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.htmlcleaner.TagNode;
import org.json.JSONArray;
import org.json.JSONObject;

import elevenquest.com.booking.HouseInfo;
import elevenquest.com.crawl.BaseCrawler;

public class BookingSiteMainCrawler extends BaseCrawler {

  public static final String SITE_MOTEL_API = "https://www.yanolja.com/api/v1/adverts?page=1&limit=100&capacityAdult=2&capacityChild=0&advert=AREA&rentType=0&stayType=0&advertsPosition=AREA_TOP&checkinDate=2020-06-03&checkoutDate=2020-06-04&region=%6d&searchType=recommend&recommend=1&sort=107&themes=&lat=37.50681&lng=127.06624";
  public static final String SITE_MOTEL_MAIN = "https://www.yanolja.com/top-adver/r-%6d/keyword-motel?advert=AREA&rentType=0&stayType=0&advertsPosition=AREA_TOP";
  public static final String SITE_HOUSE_ELEM_XPATH = "//*[@id=\"__next\"]/div[2]/section/div/div/div[]/a";

  public static String getTop100UriFromPosition(int position) {
    return String.format(SITE_MOTEL_API, position);
  }

  private static Map<String,String> getApiHeader(int position) {
    Map<String, String> defaultHeader = new HashMap<String, String>();
    defaultHeader.put("Accept", "application/json, text/plain, */*");
    defaultHeader.put("Accept-Encoding", "gzip, deflate, br");
    defaultHeader.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
    defaultHeader.put("Referer", String.format(SITE_MOTEL_MAIN, position));
    defaultHeader.put("Sec-Fetch-Dest", "empty");
    defaultHeader.put("Sec-Fetch-Mode", "cors");
    defaultHeader.put("Sec-Fetch-Site", "same-origin");
    defaultHeader.put("User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
    defaultHeader.put("X-Requested-With", "XMLHttpRequest");
    return defaultHeader;
  }

  /**
   * This method for parsing original Html element of specific booking site. 
   * But some sites are only using API service to retrieve detail information of hotel. 
   * 
   * So we couldn't use this function to parse HTML body. 
   * 
   * 
   * @deprecated
   * @param top100uri
   * @return
   * @throws Exception
   */
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

  public static List<HouseInfo> getHouseInfoFromJson(int position) throws Exception {
    List<HouseInfo> rtn = new Vector<HouseInfo>();
    String jsonBody = crawlSiteWithUnzip(getTop100UriFromPosition(position), getApiHeader(position));
    //System.out.println(jsonBody);
    //printPrettyJson(jsonBody);
    JSONObject node = toJson(jsonBody);
    JSONArray hotels = node.getJSONArray("lists");
    hotels.forEach(hotel -> {
      JSONObject hotelObj = (JSONObject)hotel;
      HouseInfo info = new HouseInfo();
      info.title = hotelObj.getString("title");
      info.ratio = hotelObj.getString("reviewStar");
      info.reviewCount = hotelObj.getString("ownerReplyCount");
      info.key = hotelObj.getString("key");
      info.relUriPath = "/motel/" + info.key;
      rtn.add(info);
    });
    return rtn;
  }  

  public static void main(String[] args) throws Exception {
    List<HouseInfo> houses = getHouseInfoFromJson(910001);
    houses.forEach(house -> System.out.println(house.title + ":" + house.relUriPath));
  }

}