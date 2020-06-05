package elevenquest.com.booking.sites;

import java.util.List;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

public class BookingSiteMain {

  public static final String HOTEL_LIST_XPATH = "//*[@id=\"__next\"]/div[2]/section/div/div/div[]/a";  // title path
  public static final String HOTEL_IMAGE_XPATH = "/div[1]/div[2]/div"; // extract http uri in style attribute.
  public static final String HOTEL_RATIO_XPATH = "/div[2]/div[2]/span[1]/text()";
  public static final String HOTEL_REVIEW_COUNT_XPATH = "/div[2]/div[2]/span[2]/b";

  TagNode rootNode;

  public BookingSiteMain(TagNode rootNode) throws XPatherException {
    this.rootNode = rootNode;
    parse();
  }

  public static TagNode eval(TagNode parentNode, String xPath) throws XPatherException {
    return (TagNode)(parentNode.evaluateXPath(xPath)[0]);
  }

  /**
   * @deprecated
   * @throws XPatherException
   */
  private void parse() throws XPatherException {
    /*
    Object[] motelList = rootNode.evaluateXPath(HOTEL_LIST_XPATH);
    Vector<BookingSiteMain> rtn = new Vector<BookingSiteMain>();
    Arrays.stream(motelList).forEach(motelInfo -> {
      TagNode motelNode = (TagNode)motelInfo;
      String detailPageLink = motelNode.getAttributeByName("href");
      String name = motelNode.getAttributeByName("title");
      String imageUri;
      String ratio;
      String reviewCount;
      try {
        imageUri = eval(motelNode, HOTEL_IMAGE_XPATH).getAttributeByName("style");
        ratio = eval(motelNode, HOTEL_RATIO_XPATH).getText().toString();
        reviewCount = eval(motelNode, HOTEL_REVIEW_COUNT_XPATH).getText().toString();
      } catch (XPatherException xpe) {
        xpe.printStackTrace();
      }
      // *[@id="__next"]/div[2]/section/div/div/div[1]/a/div[2]/div[2]/span[1]/text()  --> ratio
      //*[@id="__next"]/div[2]/section/div/div/div[1]/a/div[2]/div[2]/span[2]/b --> review count
    });
    */
  }

  public String hotelName;
  public String ratio;
  public String link;
  public List<String> imageUris;
  public List<String> reviews;
}