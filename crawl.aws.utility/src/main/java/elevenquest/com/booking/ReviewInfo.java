package elevenquest.com.booking;

import java.util.List;

import com.amazonaws.services.comprehend.model.KeyPhrase;

public class ReviewInfo {
  public String translated;
  public List<KeyPhrase> korphrases;
  public List<KeyPhrase> engphrases;
  public ReviewInfo(int ratio, String review) {
    this.ratio = ratio;
    this.review = review;
  }
  public int ratio;
  public String review;
}