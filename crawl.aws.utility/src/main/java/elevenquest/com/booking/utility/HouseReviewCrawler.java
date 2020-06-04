package elevenquest.com.booking.utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import elevenquest.com.booking.HouseInfo;
import elevenquest.com.crawl.BaseCrawler;

public class HouseReviewCrawler extends BaseCrawler {

  public final static String MOTEL_REVIEW_URL = "https://www.yanolja.com/motel/%s/reviews";
  public final static String REVIEW_XPATH = "//*[@id=\"__next\"]/div[1]/section[2]/article[]/div[2]";

  public static String getHouseReviewUri(HouseInfo houseInfo) {
    return String.format(MOTEL_REVIEW_URL, houseInfo.key);
  }

  public static void fillHouseInfo(HouseInfo houseInfo) throws IOException, InterruptedException, XPatherException {
    Map<String, String> headers = getDefaultApiHeader();
    TagNode root = cleanHtml(crawlSiteWithUnzip(getHouseReviewUri(houseInfo), headers));
    TagNode[] reviewNodes = evalList(root, REVIEW_XPATH);
    Arrays.stream(reviewNodes).forEach(node -> {
      TagNode reviewNode = (TagNode)node;
      houseInfo.reviews.add(reviewNode.getText().toString());
    });
  }
  
  private static void printReviews(HouseInfo houseInfo) {
    System.out.println("Key:" + houseInfo.key);
    if (houseInfo.reviews != null)
      houseInfo.reviews.forEach(review -> {
        System.out.println("review:" + review.length() + ":" + review);
      });
  }

  public static void main(String[] args) throws Exception {
    HouseInfo houseInfo = new HouseInfo();
    houseInfo.key = "3015463";
    fillHouseInfo(houseInfo);
    printReviews(houseInfo);
  }
}