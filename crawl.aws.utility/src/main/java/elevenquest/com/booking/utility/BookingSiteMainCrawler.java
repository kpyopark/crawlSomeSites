package elevenquest.com.booking.utility;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.htmlcleaner.TagNode;
import org.json.JSONArray;
import org.json.JSONObject;

import elevenquest.com.booking.BookingCategory;
import elevenquest.com.booking.HouseInfo;
import elevenquest.com.crawl.BaseCrawler;

public class BookingSiteMainCrawler extends BaseCrawler {

  public static final String SITE_HOTEL_API  = "https://www.yanolja.com/api/v1/contents/search?capacityAdult=2&capacityChild=0&page=1&limit=50&advert=AREA&hotel=1&checkinDate=%s&checkoutDate=%s&region=%6d&searchType=hotel&rentType=1&stayType=1&sort=106&themes=&lat=37.50681&lng=127.06624&capacityChildAges=";
  public static final String SITE_HOTEL_MAIN = "https://www.yanolja.com/hotel/r-%6d/?advert=AREA&hotel=1";

  public static final String SITE_MOTEL_API  = "https://www.yanolja.com/api/v1/adverts?page=1&limit=100&capacityAdult=2&capacityChild=0&advert=AREA&rentType=0&stayType=0&advertsPosition=AREA_TOP&checkinDate=%s&checkoutDate=%s&region=%6d&searchType=recommend&recommend=1&sort=107&themes=&lat=37.50681&lng=127.06624";
  public static final String SITE_MOTEL_MAIN = "https://www.yanolja.com/top-adver/r-%6d/keyword-motel?advert=AREA&rentType=0&stayType=0&advertsPosition=AREA_TOP";

  public static final String SITE_GUEST_API = "https://www.yanolja.com/api/v1/contents/search?capacityAdult=2&capacityChild=0&page=1&limit=50&advert=AREA&checkinDate=%s&checkoutDate=%s&region=%6d&searchType=guestHouse&guestHouse=1&rentType=1&stayType=1&sort=106&themes=&lat=37.50681&lng=127.06624&capacityChildAges=";
  public static final String SITE_GUEST_MAIN = "https://www.yanolja.com/guest-house/r-%6d/?advert=AREA";

  public static final String SITE_PENSION_API = "https://www.yanolja.com/api/v1/contents/search?capacityAdult=2&capacityChild=0&page=1&limit=50&advert=AREA&pension=1&checkinDate=%s&checkoutDate=%s&region=%6d&searchType=pension&rentType=1&stayType=1&sort=106&themes=&lat=37.50681&lng=127.06624&capacityChildAges=";
  public static final String SITE_PENSION_MAIN = "https://www.yanolja.com/pension/r-%6d/?advert=AREA&pension=1";

  public static final String SITE_HOUSE_ELEM_XPATH = "//*[@id=\"__next\"]/div[2]/section/div/div/div[]/a";

  static String pattern = "yyyy-MM-dd";
  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

  private static String getTodayStr() {
    return simpleDateFormat.format(new java.util.Date());
  }

  private static String getTommorowStr() {
    return simpleDateFormat.format(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
  }

  public static String getUriFromCategoryAndPosition(BookingCategory category, int position) {
    switch(category) {
      case MOTEL:
        return String.format(SITE_MOTEL_API, getTodayStr(), getTodayStr(), position);
      case HOTEL:
        return String.format(SITE_HOTEL_API, getTodayStr(), getTommorowStr(), position);
      case GUEST:
        return String.format(SITE_GUEST_API, getTodayStr(), getTommorowStr(), position);
      case PENSION:
        return String.format(SITE_PENSION_API, getTodayStr(), getTommorowStr(), position);
    }
    return null;
  }
  
  private static Map<String, String> getDefaultApiHeader(int position) {
    Map<String, String> defaultHeader = new HashMap<String, String>();
    defaultHeader.put("Accept", "application/json, text/plain, */*");
    defaultHeader.put("Accept-Encoding", "gzip, deflate, br");
    defaultHeader.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
    defaultHeader.put("Sec-Fetch-Dest", "empty");
    defaultHeader.put("Sec-Fetch-Mode", "cors");
    defaultHeader.put("Sec-Fetch-Site", "same-origin");
    defaultHeader.put("User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
    defaultHeader.put("X-Requested-With", "XMLHttpRequest");
    return defaultHeader;
  }

  private static Map<String, String> getHotelApiHeader(int position) {
    Map<String, String> defaultHeader = getDefaultApiHeader(position);
    defaultHeader.put("Referer", String.format(SITE_HOTEL_MAIN, position));
    return defaultHeader;
  }

  private static Map<String,String> getMotelApiHeader(int position) {
    Map<String, String> defaultHeader = getDefaultApiHeader(position);
    defaultHeader.put("Referer", String.format(SITE_MOTEL_MAIN, position));
    return defaultHeader;
  }

  private static Map<String, String> getGuestApiHeader(int position) {
    Map<String, String> defaultHeader = getDefaultApiHeader(position);
    defaultHeader.put("Referer", String.format(SITE_GUEST_MAIN, position));
    return defaultHeader;
  }

  private static Map<String, String> getPensionApiHeader(int position) {
    Map<String, String> defaultHeader = getDefaultApiHeader(position);
    defaultHeader.put("Referer", String.format(SITE_PENSION_MAIN, position));
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
  public static List<HouseInfo> getHouseInfos(String uri) throws Exception {
    List<HouseInfo> rtn = new Vector<HouseInfo>();
    TagNode rootNode = cleanHtml(crawlSite(uri));
    TagNode[] houseInfos = evalList(rootNode, SITE_HOUSE_ELEM_XPATH);
    Arrays.stream(houseInfos).forEach(houseNode -> {
      HouseInfo oneItem = new HouseInfo();
      oneItem.title = houseNode.getAttributeByName("title");
      oneItem.relUriPath = houseNode.getAttributeByName("href");
      rtn.add(oneItem);
    });
    return rtn;
  }

  public static List<HouseInfo> getHouseInfoFromJson(BookingCategory category, int position) throws Exception {
    List<HouseInfo> rtn = new Vector<HouseInfo>();
    Map<String, String> headers = null;
    switch(category) {
      case MOTEL:
        headers = getMotelApiHeader(position);
        break;
      case HOTEL:
        headers = getHotelApiHeader(position);
        break;
      case GUEST:
        headers = getGuestApiHeader(position);
        break;
      case PENSION:
        headers = getPensionApiHeader(position);
        break;
    }

    String jsonBody = crawlSiteWithUnzip(getUriFromCategoryAndPosition(category, position), headers);
    JSONObject node = toJson(jsonBody);
    JSONArray hotels = null;
    switch(category) {
      case MOTEL: 
        hotels = node.getJSONArray("lists");
        break;
      case HOTEL:
      case GUEST:
      case PENSION:
        hotels = node.getJSONObject("motels").getJSONArray("lists");
        break;
    }
    hotels.forEach(hotel -> {
      JSONObject hotelObj = (JSONObject)hotel;
      HouseInfo info = new HouseInfo();
      info.title = hotelObj.getString("title");
      info.ratio = hotelObj.getString("reviewStar");
      info.reviewCount = hotelObj.getString("ownerReplyCount");
      info.key = hotelObj.getString("key");
      info.relUriPath = category.getUri() + "/" + info.key;
      rtn.add(info);
    });
    return rtn;
  }  

  public static void main(String[] args) throws Exception {
    // List<HouseInfo> houses = getHouseInfoFromJson(BookingCategory.MOTEL, 910001);
    // List<HouseInfo> houses = getHouseInfoFromJson(BookingCategory.HOTEL, 910161);
    // List<HouseInfo> houses = getHouseInfoFromJson(BookingCategory.GUEST, 900119);
    List<HouseInfo> houses = getHouseInfoFromJson(BookingCategory.PENSION, 900622);
    houses.forEach(house -> System.out.println(house.title + ":" + house.relUriPath));
  }

}