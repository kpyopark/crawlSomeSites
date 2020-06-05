package elevenquest.com.booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import elevenquest.com.booking.utility.BookingCategoryCrawler;

public class BookingCategoryExt {

  static class Entry {
    public String region;
    public int code;
    public Entry(int code, String region) {
      Entry.this.region = region;
      Entry.this.code = code;
    }
  }

  //public static BookingCategoryExt MOTEL
  BookingCategory category;

  public BookingCategory getCategory() {
    return this.category;
  }

  private BookingCategoryExt(BookingCategory category, String[] regions, int[] points) {
    if(regions == null || points == null || regions.length != points.length)
      throw new RuntimeException("Wrong category info. You should check region/points arraies are null or mismatched.");
    this.category = category;
    int count;
    for(count = 0; count < regions.length; count++) {
      addEntry(points[count], regions[count]);
    }
  }

  private BookingCategoryExt(BookingCategory category, Map<Integer /* code */, String /* region name */> regions) {
    this.category = category;
    regions.forEach((regionCode, regionName) -> {
      addEntry(regionCode, regionName);
    });
  }

  private BookingCategoryExt(BookingCategory category) {
    this.category = category;
    Map<Integer, String> regions = new HashMap<Integer, String>();    // if you this variable to set null, you need more codes. 
    try {
      regions = BookingCategoryCrawler.getRegionCode(category);
    } catch (Exception e) {
      e.printStackTrace();
    }
    regions.forEach((regionCode, regionName) -> {
      addEntry(regionCode, regionName);
    });
  }

  List<Entry> points = new ArrayList<Entry>();  
  public void addEntry(int code, String region) {
    this.points.add(new Entry(code, region));
  }
  public Map<Integer, String> getPoints() {
    Map<Integer, String> rtn = new HashMap<Integer, String>();
    this.points.forEach(entry -> {
      Entry entryObj = (Entry)entry;
      rtn.put(entryObj.code, entryObj.region);
    });
    return rtn;
  }

  public static BookingCategoryExt MOTEL = new BookingCategoryExt(BookingCategory.MOTEL);
  public static BookingCategoryExt HOTEL = new BookingCategoryExt(BookingCategory.HOTEL);
  public static BookingCategoryExt PENSION = new BookingCategoryExt(BookingCategory.PENSION);
  public static BookingCategoryExt GUEST = new BookingCategoryExt(BookingCategory.GUEST);
  // public static BookingCategoryExt LEISURE = new BookingCategoryExt(BookingCategory.LEISURE);
  // public static BookingCategoryExt GLOBAL = new BookingCategoryExt(BookingCategory.GLOBAL);

  public static BookingCategoryExt[] CATEGORY_LIST = {
    MOTEL, 
    HOTEL, 
    PENSION, 
    GUEST, 
    // LEISURE, 
    // GLOBAL
  };

  // public static BookingCategoryExt MOTEL = new BookingCategoryExt(BookingCategory.MOTEL, 
  //   new String[] {"서울TOP", "경기TOP", "인천TOP", "강원TOP", "제주TOP", "대전TOP", "충북TOP", "충남TOP", "부산TOP", "울산TOP", "경남TOP", "대구TOP", "경북TOP", "광주TOP", "전남TOP", "전북TOP"},
  //   new int[]    {910001,    910002,    910003,    910004,    910005,    910006,    910007,    910008,    910009,    910010,    910011,    910012,    910013,    910014,    910015,    910016}
  // );

  // public static BookingCategoryExt HOTEL = new BookingCategoryExt(BookingCategory.HOTEL,
  //   new String[] {"서울 강남/송파/서초", "서울 홍대/용산/종로", "서울 영등포/강서", "강원 강릉/속초/양양", "부산 해운대/광안리", "제주 서귀포/제주시", "경상 경주/포항/울산", "경기 수원/용인/화성", "전남 여수/순천/목포", "인천 국제공항/송도"},
  //   new int[]    {910161               , 910134               , 910135            , 910136               , 910137              , 910138              , 910139               , 910140               , 910141               , 910142}
  // );

  // public static BookingCategoryExt PENSION = new BookingCategoryExt(BookingCategory.PESION,
  //   new String[] {"가평(명지계곡/가평북부)", "가평(남이섬/자라섬)", "가평(쁘띠프랑스)", "경주 내륙(보문단지)", "경주 해안(감포)", "여수", "강릉", "속초/고성", "거제도", "포항" },
  //   new int[]    {900622                   , 900694               , 910170            , 900623               , 910171           , 900624, 900663, 910131     , 900664  , 900689 }
  // );

  // public static BookingCategoryExt 
  
}