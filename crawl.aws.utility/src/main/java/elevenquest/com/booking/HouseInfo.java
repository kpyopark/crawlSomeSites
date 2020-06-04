package elevenquest.com.booking;

import java.util.List;
import java.util.Vector;

public class HouseInfo {
  public BookingCategory category;
  public int regionCode;
  public String ratio;
  public String title;
  public String reviewCount;
  public String key;
  public String relUriPath;
  public List<String> reviews = new Vector<String>();
  public List<String> imageUris = new Vector<String>();
}