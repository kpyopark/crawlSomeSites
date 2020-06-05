package elevenquest.com.booking;

import java.util.List;

import elevenquest.com.booking.aiml.ImageReviewProcess;
import elevenquest.com.booking.aiml.TextReviewProcess;
import elevenquest.com.booking.dao.HouseInfoDao;
import elevenquest.com.booking.utility.BookingSiteAllCrawler;

/**
 * This class has responsibility to crwal all the house information from the site
 * and to make these information enriched with Amazon Comprehend and Rekognition
 */
public class SiteAnalyzer {
  
  static int maxImagePerHouse = 10;

  public static void main(String[] args) {
    List<HouseInfo> infolist = BookingSiteAllCrawler.getHouseInfoList(Integer.MAX_VALUE, 5, 250);
    infolist.forEach(houseInfo -> {
      try {
        BookingSiteAllCrawler.fillAllHouseInfo(houseInfo);
        if(houseInfo.imageUris != null) {
          System.out.println("Image Count:" + houseInfo.imageUris.size() );
          int maxNumber = Math.min(maxImagePerHouse, houseInfo.imageUris.size());
          houseInfo.imageUris.subList(0, maxNumber).forEach(imageInfo -> {
            imageInfo.labels = ImageReviewProcess.parseImage(imageInfo.uri);
          });
        }
        houseInfo.reviews.forEach(reviewInfo -> {
          if(reviewInfo.review != null && reviewInfo.review.length() > 20) {
            reviewInfo.translated = TextReviewProcess.translateKoreanToEnglish(reviewInfo.review);
            reviewInfo.engphrases = TextReviewProcess.extractKeyPhrases(reviewInfo.translated, "en");
            reviewInfo.korphrases = TextReviewProcess.extractKeyPhrases(reviewInfo.review, "ko");
          }
        });
        System.out.println("Insert House Info.");
        HouseInfoDao dbio = new HouseInfoDao();
        dbio.insertHouseInfo(houseInfo);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}