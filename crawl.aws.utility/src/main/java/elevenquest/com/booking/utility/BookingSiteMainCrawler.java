package elevenquest.com.booking.utility;

import java.util.List;
import java.util.Map;
import java.util.Vector;

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

  private static String getUriFromCategoryAndPosition(BookingCategory category, int position) {
    switch(category) {
      case MOTEL:
        return String.format(SITE_MOTEL_API, getTodayStr(), getTommorowStr(), position);
      case HOTEL:
        return String.format(SITE_HOTEL_API, getTodayStr(), getTommorowStr(), position);
      case GUEST:
        return String.format(SITE_GUEST_API, getTodayStr(), getTommorowStr(), position);
      case PENSION:
        return String.format(SITE_PENSION_API, getTodayStr(), getTommorowStr(), position);
    }
    return null;
  }
  
  private static Map<String, String> getHotelApiHeader(int position) {
    Map<String, String> defaultHeader = getDefaultApiHeader();
    defaultHeader.put("Referer", String.format(SITE_HOTEL_MAIN, position));
    return defaultHeader;
  }

  private static Map<String,String> getMotelApiHeader(int position) {
    Map<String, String> defaultHeader = getDefaultApiHeader();
    defaultHeader.put("Referer", String.format(SITE_MOTEL_MAIN, position));
    return defaultHeader;
  }

  private static Map<String, String> getGuestApiHeader(int position) {
    Map<String, String> defaultHeader = getDefaultApiHeader();
    defaultHeader.put("Referer", String.format(SITE_GUEST_MAIN, position));
    return defaultHeader;
  }

  private static Map<String, String> getPensionApiHeader(int position) {
    Map<String, String> defaultHeader = getDefaultApiHeader();
    defaultHeader.put("Referer", String.format(SITE_PENSION_MAIN, position));
    return defaultHeader;
  }

  /*
  private static List<HouseInfo> getHouseInfos(String uri) throws Exception {
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
  */

  public static List<HouseInfo> getHouseInfoFromJson(BookingCategory category, String region, int position) throws Exception {
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
      info.category = category;
      info.regionCode = position;
      info.region = region;
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
    List<HouseInfo> houses = getHouseInfoFromJson(BookingCategory.PENSION, "속초/양평", 900622);
    houses.forEach(house -> System.out.println(house.title + ":" + house.relUriPath));
  }

}