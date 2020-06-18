package elevenquest.com.booking.aiml;

import java.util.List;

import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.comprehend.model.KeyPhrase;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClientBuilder;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;

public class TextReviewProcess {

  private static long extractCount = 0;
  public static List<KeyPhrase> extractKeyPhrases(String review, String languageCode) {
    System.out.println("Extraction key phrases... " + (++extractCount));
    DetectKeyPhrasesResult result = null;
    try {
      AmazonComprehend comprehendClient = AmazonComprehendClientBuilder.defaultClient();
      DetectKeyPhrasesRequest request = new DetectKeyPhrasesRequest()
        .withText(review)
        .withLanguageCode(languageCode);
      result = comprehendClient.detectKeyPhrases(request);
      DetectSentimentRequest sentimentRequest = new DetectSentimentRequest()
        .withText(review)
        .withLanguageCode(languageCode);
    } catch (Throwable thr) {
      thr.printStackTrace();
      return null;
    }
    return result.getKeyPhrases();
  }

  public static DetectSentimentResult extractSentiment(String review, String languageCode) {
    System.out.println("Extraction key phrases... " + (++extractCount));
    DetectSentimentResult result = null;
    try {
      AmazonComprehend comprehendClient = AmazonComprehendClientBuilder.defaultClient();
      DetectSentimentRequest sentimentRequest = new DetectSentimentRequest()
        .withText(review)
        .withLanguageCode(languageCode);
      result = comprehendClient.detectSentiment(sentimentRequest);
    } catch (Throwable thr) {
      thr.printStackTrace();
      return null;
    }
    return result;
  }

  private static long transCount = 0;
  public static String translateKoreanToEnglish(String review) {
    System.out.println("Translating review written in Korean... " + (++transCount) );
    TranslateTextResult result = null;
    try {
      AmazonTranslate translateClient = AmazonTranslateClientBuilder.defaultClient();
      TranslateTextRequest request = new TranslateTextRequest().withText(review).withSourceLanguageCode("ko")
        .withTargetLanguageCode("en");
      result = translateClient.translateText(request);
    } catch (Throwable thr) {
      thr.printStackTrace();
      return null;
    }
    return result.getTranslatedText();
  }

  public static void main(String[] args) {
    String krstr = "후기 보고 간건데 별로였어요 일단 와이파이가 너무 약하고 티비도 나오다 끊기고,, 화장실도 블라인드가 있긴 한데 너무 후지고 오래돼서 사이사이로 다 보여요 바로 옆에 건물이 있고 사람들 지나다니는것도 보여서 무지 신경쓰였어요 다른 방은 어떤지 모르겠지만,,그리고 복도에 사람들 얘기하는것도 너무 잘들려서 별로였어요 티비를 켠 상태인데도 불구하고";
    String eng = translateKoreanToEnglish(krstr);
    System.out.println(eng);
    extractKeyPhrases(eng, "en").forEach(keyPhrase -> System.out.println(keyPhrase.getText() + ":" + keyPhrase.getScore()));
    extractKeyPhrases(
        krstr, "ko")
        .forEach(keyPhrase -> System.out.println(keyPhrase.getText() + ":" + keyPhrase.getScore()));
  }

}