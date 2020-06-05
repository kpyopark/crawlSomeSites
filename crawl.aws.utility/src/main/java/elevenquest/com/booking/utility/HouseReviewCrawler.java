package elevenquest.com.booking.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import elevenquest.com.booking.HouseInfo;
import elevenquest.com.booking.ReviewInfo;
import elevenquest.com.crawl.BaseCrawler;

public class HouseReviewCrawler extends BaseCrawler {

  public final static String MOTEL_REVIEW_URL = "https://www.yanolja.com/motel/%s/reviews";
  // public final static String REVIEW_XPATH = "//*[@id=\"__next\"]/div[1]/section[2]/article[]/div[2]";
  public final static String REVIEW_XPATH = "//*[@id=\"__next\"]/div[1]/section[2]/article[]";
  public final static String REVIEW_TEXT_REL_XPATH = "/div[2]";
  // public final static String REVIEW_TEXT_REL_STAR = "/div[1]/div/div[]/i[@class=_1SyRhT]";
  public final static String REVIEW_TEXT_REL_STAR = "//i[@class = '_1SyRhT' ]";

  public static String getHouseReviewUri(HouseInfo houseInfo) {
    return String.format(MOTEL_REVIEW_URL, houseInfo.key);
  }

  public static void fillHouseInfo(HouseInfo houseInfo) throws IOException, InterruptedException, XPatherException {
    Map<String, String> headers = getDefaultApiHeader();
    TagNode root = cleanHtml(crawlSiteWithUnzip(getHouseReviewUri(houseInfo), headers));
    TagNode[] reviewNodes = evalList(root, REVIEW_XPATH);
    HashMap<String, ReviewInfo> uniqueReview = new HashMap<String, ReviewInfo>();
    Arrays.stream(reviewNodes).forEach(node -> {
      TagNode reviewNode = (TagNode)node;
      try {
        String reviewText = eval(reviewNode, REVIEW_TEXT_REL_XPATH).getText().toString();
        uniqueReview.put(
          reviewText.hashCode() + ""
          ,
        new ReviewInfo(
          evalList(reviewNode, REVIEW_TEXT_REL_STAR).length ,
          reviewText)
        );
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    houseInfo.reviews = new ArrayList<ReviewInfo>(uniqueReview.values());
  }
  
  private static void printReviews(HouseInfo houseInfo) {
    System.out.println("Key:" + houseInfo.key);
    if (houseInfo.reviews != null)
      houseInfo.reviews.forEach(review -> {
        System.out.println("review ratio:" + review.ratio + ":" + review.review);
      });
  }

  public static void main(String[] args) throws Exception {
    HouseInfo houseInfo = new HouseInfo();
    houseInfo.key = "3015463";
    fillHouseInfo(houseInfo);
    printReviews(houseInfo);
  }
  
}