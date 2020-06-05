package elevenquest.com.booking.utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.htmlcleaner.XPatherException;

import elevenquest.com.booking.BookingCategoryExt;
import elevenquest.com.booking.HouseInfo;
import elevenquest.com.crawl.BaseCrawler;

public class BookingSiteAllCrawler extends BaseCrawler {

  private static Map<Integer, String> subMap(Map<Integer, String> map, int maxCount) {
    int realMaxCount = Math.min(map.size(), maxCount);
    Map<Integer, String> targetCategory = new HashMap<Integer, String>();
    Iterator<Integer> iter = map.keySet().iterator();
    int regionCount = 0;
    while(iter.hasNext()) {
      if(regionCount < realMaxCount) {
        Integer key = iter.next();
        targetCategory.put(key, map.get(key));
        regionCount++;
      } else {
        break;
      }
    }
    return targetCategory;
  }

  public static List<HouseInfo> getHouseInfoList(int maxRegion, int maxHousePerRegion, int maxHouse) {
    List<HouseInfo> rtn = new Vector<HouseInfo>();
    BookingCategoryExt categoryEx = BookingCategoryExt.MOTEL;
    Map<Integer, String> targetRegions = subMap(categoryEx.getPoints(), maxRegion);
    Iterator<Integer> iter = targetRegions.keySet().iterator();
    while(iter.hasNext()) {
      if (rtn.size() < maxHouse) {
        Integer regionCode = iter.next();
        String region = targetRegions.get(regionCode);
        try {
          System.out.print("Retreiving accomodation list in " + region);
          List<HouseInfo> infos = BookingSiteMainCrawler.getHouseInfoFromJson(categoryEx.getCategory(), region,
              regionCode);
          if(infos.size() > maxHousePerRegion)
            infos = infos.subList(0, maxHousePerRegion);
          rtn.addAll(infos);
          System.out.println(" - # of count :" + rtn.size());
          Thread.sleep(100);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        break;
      }
    }
    return rtn;
  }

  private static void printReviews(HouseInfo houseInfo) {
    System.out.println("Key:" + houseInfo.key);
    if (houseInfo.reviews != null)
      houseInfo.reviews.forEach(review -> {
        System.out.println("review:" + review.review.length() + ":" + review.review);
      });
  }
  
  private static void printImageUris(HouseInfo houseInfo) {
    System.out.println("Key:" + houseInfo.key);
    if (houseInfo.imageUris != null)
      houseInfo.imageUris.forEach(uri -> {
        System.out.println("Uri:" + uri);
      });
  }

  public static void fillAllHouseInfo(HouseInfo houseInfo) throws IOException, InterruptedException, XPatherException {
    HouseDetailCrawler.fillHouseInfo(houseInfo);
    HouseReviewCrawler.fillHouseInfo(houseInfo);
  }

  public static void main(String[] args) {
    List<HouseInfo> list = getHouseInfoList(1, 1, 1);
    list.forEach(infoobj -> {
      try {
        HouseInfo info = (HouseInfo) infoobj;
        System.out.println(info.title + ":" + info.key);
        fillAllHouseInfo(info);
        printImageUris(info);
        printReviews(info);
        Thread.sleep(100);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
  
}