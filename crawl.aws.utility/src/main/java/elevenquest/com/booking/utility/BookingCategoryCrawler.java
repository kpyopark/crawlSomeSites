package elevenquest.com.booking.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.htmlcleaner.TagNode;
import org.json.JSONArray;
import org.json.JSONObject;

import elevenquest.com.booking.BookingCategory;
import elevenquest.com.crawl.BaseCrawler;

public class BookingCategoryCrawler extends BaseCrawler {
  
  public static String getCategoryUri(BookingCategory category) {
    return "https://www.yanolja.com" + category.getUri();
  }

  public static Map<Integer, String> getRegionCode(BookingCategory category) throws Exception {
    Map<Integer, String> rtn = new HashMap<Integer, String>();
    TagNode rootNode = cleanHtml(crawlSiteWithUnzip(getCategoryUri(category), getDefaultHeader()));
    TagNode jsonbody = eval(rootNode, "//*[@id=\"__NEXT_DATA__\"]");
    JSONObject regionBody = toJson(jsonbody.getText().toString());
    JSONArray regions = null;
    switch (category) {
      case PENSION :
      case HOTEL :
        regions = regionBody.getJSONObject("props").getJSONObject("pageProps").getJSONObject("regions")
            .getJSONArray("regions");
        break;
      case MOTEL :
      case GUEST :
        regions = regionBody.getJSONObject("props").getJSONObject("pageProps").getJSONArray("locationData");
        break;
      default :
        regions = regionBody.getJSONObject("props").getJSONObject("pageProps").getJSONArray("locationData");
        break;
    }
    regions.forEach(regionObj -> {
      JSONObject region = (JSONObject) regionObj;
      JSONArray children = region.getJSONArray("children");
      children.forEach(subregionObj -> {
        JSONObject subregion = (JSONObject) subregionObj;
        String name = subregion.getString("name");
        int regionCode = subregion.getInt("region");
        rtn.put(regionCode, name);
      });
    });
    return rtn;
  }

  public static void main(String[] args) throws Exception {
    Map<String, Integer> counts = new HashMap<String, Integer>();
    Arrays.stream(BookingCategory.values()).forEach(category -> {
      try {
        Map<Integer, String> result = getRegionCode(category);
        result.forEach((code, name)->{
          System.out.println(code + ":" + name);
        });
        counts.put(category.name(), result.size());
      } catch (Exception e) {
        System.out.println("Error Category:" + category.name());
        e.printStackTrace();
      }
    }
    );
    counts.forEach((category, count)-> System.out.println(String.format("Category[%s]:Count[%s]", category, count)));
  }
}