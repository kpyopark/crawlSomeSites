package elevenquest.com.crawl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.json.JSONObject;

public class BaseCrawler {

  public static TagNode eval(TagNode parentNode, String xPath) throws XPatherException {
    return (TagNode) (parentNode.evaluateXPath(xPath)[0]);
  }

  public static TagNode[] evalList(TagNode parentNode, String xPath) throws XPatherException {
    Object[] motelList = parentNode.evaluateXPath(xPath);
    return Arrays.copyOf(motelList, 
      motelList.length,
      TagNode[].class);
  }

  public static JSONObject toJson(String body) {
    JSONObject obj = new JSONObject(body);
    return obj;
  }

  public static String crawlSite(String uri) throws IOException, InterruptedException {
    HashMap<String, String> defaultHeader = new HashMap<String, String>();
    defaultHeader.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
    defaultHeader.put("Accept-Encoding","gzip, deflate, br");
    defaultHeader.put("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
    // defaultHeader.put("Host","www.yanolja.com");
    defaultHeader.put("Sec-Fetch-Dest","document");
    defaultHeader.put("Sec-Fetch-Mode","navigate");
    defaultHeader.put("Sec-Fetch-Site","same-origin");
    defaultHeader.put("Sec-Fetch-User","1");
    defaultHeader.put("Upgrade-Insecure-Requests","1");
    defaultHeader.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
    return crawlSite(uri, defaultHeader);
  }

  public static String crawlSiteWithUnzip(String uri, Map<String, String> headers) throws IOException, InterruptedException {
    StringBuffer sb = new StringBuffer();
    HttpClient client = HttpClient.newHttpClient();
    Builder builder = HttpRequest.newBuilder();
    builder.uri(URI.create(uri));
    headers.forEach((key, value) -> {
      builder.setHeader(key, value);
    });
    HttpRequest request = builder.build();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(new GZIPInputStream(client.send(request, BodyHandlers.ofInputStream()).body())));
      String line;
      while((line = br.readLine()) != null) {
        sb.append(line);
      }
    } finally {
      if(br != null) try { br.close(); } catch(Exception e) {}
    }
    return sb.toString();
  }

  public static String crawlSite(String uri, Map<String, String> headers) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    Builder builder = HttpRequest.newBuilder();
    builder.uri(URI.create(uri));
    headers.forEach((key, value) -> {
      builder.setHeader(key, value);
    });
    HttpRequest request = builder.build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return response.body();
  }

  public static TagNode cleanHtml(String body) {
    HtmlCleaner cleaner = new HtmlCleaner();
    return cleaner.clean(body);
  }

  public static void printPrettyJson(String json) throws FileNotFoundException {
    JSONObject prettyJson = new JSONObject(json);
    System.out.println(prettyJson.toString(2));
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(new FileOutputStream("./categories.json"));
      pw.println(prettyJson.toString(2));
    } finally {
      if (pw != null)
        try {
          pw.close();
        } catch (Exception e) {
          // eat it.
        }
    }
  }

}