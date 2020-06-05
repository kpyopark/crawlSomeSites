package elevenquest.com.booking.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import elevenquest.com.booking.HouseInfo;
import elevenquest.com.booking.ImageInfo;
import elevenquest.com.crawl.BaseCrawler;

public class HouseDetailCrawler extends BaseCrawler {
  private final static String ROOM_DETAIL_URL = "https://www.yanolja.com/api/v1/place/detail/%s/rooms?check_in_date=%s&check_out_date=%s&device=4";
  private final static String ROOM_DETAIL_REFERER_URL = "https://www.yanolja.com/motel/%s";

  protected static String getHouseDetailUri(HouseInfo houseInfo) {
    return String.format(ROOM_DETAIL_URL, houseInfo.key, getTodayStr(), getTommorowStr());
  }

  public static void fillHouseInfo(HouseInfo houseInfo) throws IOException, InterruptedException {
    Map<String, String> headers = getDefaultApiHeader();
    headers.put("Referer", String.format(ROOM_DETAIL_REFERER_URL, houseInfo.key));
    String jsonBody = crawlSiteWithUnzip(getHouseDetailUri(houseInfo), headers);
    JSONObject json = toJson(jsonBody);
    JSONArray rooms = json.getJSONArray("rooms");
    HashMap<String, ImageInfo> uniqueImageInfos = new HashMap<String, ImageInfo>();
    rooms.forEach(roomjson -> {
      JSONObject photojson = ((JSONObject)roomjson).getJSONObject("photo");
      String uri = photojson.getJSONObject("representative").getString("uri");
      uniqueImageInfos.put(uri.hashCode() + "", new ImageInfo(uri, "Y"));
      JSONArray images = photojson.getJSONArray("items");
      images.forEach(imagejson -> {
        String imageuri = ((JSONObject)imagejson).getString("uri");
        uniqueImageInfos.put(imageuri.hashCode() + "", new ImageInfo(imageuri, "N"));
      });
    });
    houseInfo.imageUris = new ArrayList<ImageInfo>(uniqueImageInfos.values());
    // printPrettyJson(jsonBody);
  }

  private static void printImageUris(HouseInfo houseInfo) {
    System.out.println("Key:" + houseInfo.key);
    if(houseInfo.imageUris != null)
    houseInfo.imageUris.forEach(uri -> {
      System.out.println("Uri:" + uri);
    });
  }

  public static void main(String[] args) throws Exception {
    HouseInfo houseInfo = new HouseInfo();
    houseInfo.key = "3015463";
    fillHouseInfo(houseInfo);
    printImageUris(houseInfo);
  }

}