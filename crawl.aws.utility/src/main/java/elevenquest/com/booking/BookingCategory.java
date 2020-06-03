package elevenquest.com.booking;

public enum BookingCategory {

  MOTEL("/motel"),
  HOTEL("/hotel"),
  PESION("/pension"),
  GUEST("/guest-house"),
  LEISURE("/leisure"),
  GLOBAL("/global/place")
  ;

  BookingCategory(String uri) {
    this.uri = uri;
  }

  public String getUri() {
    return this.uri;
  }

  private String uri;
}