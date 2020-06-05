package elevenquest.com.booking;

import java.util.List;
import java.util.Vector;

public class HouseInfo {
  public BookingCategory category;
  public int regionCode;
  public String region;
  public String ratio;
  public String title;
  public String reviewCount;
  public String key;
  public String relUriPath;
  public List<ReviewInfo> reviews = new Vector<ReviewInfo>();
  public List<ImageInfo> imageUris = new Vector<ImageInfo>();
}