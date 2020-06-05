package elevenquest.com.booking;

import java.util.List;

import com.amazonaws.services.rekognition.model.Label;

public class ImageInfo {

  public ImageInfo(String uri, String representativeYn) {
    this.uri = uri;
    this.representativeYn = representativeYn;
  }
  public String uri;
  public List<Label> labels;
  public String representativeYn;
}