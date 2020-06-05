package elevenquest.com.booking.aiml;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;


public class ImageReviewProcess {
  public static void parsePhrases(String review) {

  }

  private static int parseImageCount=0;
  public static List<Label> parseImage(String uri) {
    System.out.println("Parsing and extracting labels from images." + (++parseImageCount) );
    List<Label> rtn = null;
    ByteBuffer imageBytes;
    HttpClient client = HttpClient.newHttpClient();
    Builder builder = HttpRequest.newBuilder();
    builder.uri(URI.create(uri));
    HttpRequest request = builder.build();
    try {
      imageBytes = ByteBuffer.wrap(client.send(request, BodyHandlers.ofByteArray()).body());
    } catch (Exception ioe) {
      ioe.printStackTrace();
      return null;
    }
    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
    DetectLabelsRequest labelRequest = new DetectLabelsRequest().withImage(new Image().withBytes(imageBytes))
        .withMaxLabels(20).withMinConfidence(60F);
    try {

      DetectLabelsResult result = rekognitionClient.detectLabels(labelRequest);
      rtn = result.getLabels();
      for (Label label : rtn) {
        System.out.println(label.getName() + ": " + label.getConfidence().toString());
      }

    } catch (AmazonRekognitionException e) {
      e.printStackTrace();
    }
    return rtn;
  }
  
  public static void main(String[] args) throws Exception {
    parseImage("https://yaimg.yanolja.com/v5/2018/08/14/18/640/5b729db459dee6.48252478.jpg");
  }
}