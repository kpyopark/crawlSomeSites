package elevenquest.com.booking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import com.amazonaws.services.comprehend.model.KeyPhrase;
import com.amazonaws.services.rekognition.model.Label;

import elevenquest.com.booking.HouseInfo;
import elevenquest.com.booking.ImageInfo;
import elevenquest.com.booking.ReviewInfo;

public class HouseInfoDao extends BaseDao {
  private final static String INSERT_HOUSE_INFO = "insert into TB_HOUSE_INFO (house_key, region_code, region, category, ratio, title, review_count, rel_uri) values (?, ?, ?, ?, ?, ?, ?, ?)";
  private final static String INSERT_HOUSE_IMG_INFO = 
    "insert into TB_HOUSE_IMAGE_INFO (house_key, img_hash, img_uri, first_yn) values (?, ?, ?, ?)";
  private final static String INSERT_HOUSE_IMG_REK_INFO = 
    "insert into TB_HOUSE_IMAGE_REKOG_LABEL_INFOS (house_key, img_hash, label_hash, label_text, confidence) values (?, ?, ?, ?, ?)";
  private final static String INSERT_HOUSE_REVIEW_INFO = 
    "insert into TB_HOUSE_REVIEW_INFO (house_key, review_hash, review_kor, review_eng, review_ratio) values (?, ?, ?, ?, ?)";
  private final static String INSERT_HOUSE_COMP_INFO = 
    "insert into TB_HOUSE_REVIEW_COMP_INFOS (house_key, review_hash, kor_eng, seq, phrase, score) values (?, ?, ?, ?, ?, ?)";

  public void insertHouseInfo(HouseInfo info) {
    Connection con = null;
    PreparedStatement pstmtInsertHouseInfo = null;
    PreparedStatement pstmtInsertHouseImgInfo = null;
    PreparedStatement pstmtInsertHouseImgRekInfo = null;
    PreparedStatement pstmtInsertHouseReviewInfo = null;
    PreparedStatement pstmtInsertHouseCompInfo = null;
    int count;
    try {
      con = getConnection();
      pstmtInsertHouseInfo = con.prepareStatement(INSERT_HOUSE_INFO);
      pstmtInsertHouseImgInfo = con.prepareStatement(INSERT_HOUSE_IMG_INFO);
      pstmtInsertHouseImgRekInfo = con.prepareStatement(INSERT_HOUSE_IMG_REK_INFO);
      pstmtInsertHouseReviewInfo = con.prepareStatement(INSERT_HOUSE_REVIEW_INFO);
      pstmtInsertHouseCompInfo = con.prepareStatement(INSERT_HOUSE_COMP_INFO);

      pstmtInsertHouseInfo.setString(1, info.key);
      pstmtInsertHouseInfo.setString(2, info.regionCode + "");
      pstmtInsertHouseInfo.setString(3, info.region);
      pstmtInsertHouseInfo.setString(4, info.category.name());
      pstmtInsertHouseInfo.setString(5, info.ratio);
      pstmtInsertHouseInfo.setString(6, info.title);
      pstmtInsertHouseInfo.setString(7, info.reviewCount);
      pstmtInsertHouseInfo.setString(8, info.relUriPath);

      pstmtInsertHouseInfo.executeUpdate();

      for(count = 0; info.imageUris != null && count < info.imageUris.size(); count++) {
        ImageInfo imageInfo = info.imageUris.get(count);
        pstmtInsertHouseImgInfo.setString(1, info.key);
        pstmtInsertHouseImgInfo.setString(2, imageInfo.uri.hashCode() + "");
        pstmtInsertHouseImgInfo.setString(3, imageInfo.uri);
        pstmtInsertHouseImgInfo.setString(4, imageInfo.representativeYn);

        pstmtInsertHouseImgInfo.addBatch();
      }
      pstmtInsertHouseImgInfo.executeBatch();

      for (count = 0; info.imageUris != null && count < info.imageUris.size(); count++) {
        int labelcount;
        ImageInfo imageInfo = info.imageUris.get(count);
        List<Label> labels = imageInfo.labels;
        for (labelcount = 0; labels != null && labelcount < labels.size() ; labelcount++) {
          pstmtInsertHouseImgRekInfo.setString(1, info.key);
          pstmtInsertHouseImgRekInfo.setString(2, imageInfo.uri.hashCode() + "");
          pstmtInsertHouseImgRekInfo.setString(3, labels.get(labelcount).getName().hashCode() + "");
          pstmtInsertHouseImgRekInfo.setString(4, labels.get(labelcount).getName());
          pstmtInsertHouseImgRekInfo.setString(5, labels.get(labelcount).getClass() + "");

          pstmtInsertHouseImgRekInfo.addBatch();
        }
      }
      pstmtInsertHouseImgRekInfo.executeBatch();

      for(count = 0; info.reviews != null && count < info.reviews.size(); count++) {
        ReviewInfo reviewInfo = info.reviews.get(count);
        pstmtInsertHouseReviewInfo.setString(1, info.key);
        pstmtInsertHouseReviewInfo.setString(2, reviewInfo.review.hashCode() + "");
        pstmtInsertHouseReviewInfo.setString(3, reviewInfo.review);
        pstmtInsertHouseReviewInfo.setString(4, reviewInfo.translated);
        pstmtInsertHouseReviewInfo.setInt(5, reviewInfo.ratio);

        pstmtInsertHouseReviewInfo.addBatch();
      }
      pstmtInsertHouseReviewInfo.executeBatch();

      for(count = 0; info.reviews != null && count < info.reviews.size(); count++) {
        ReviewInfo reviewInfo = info.reviews.get(count);
        int phraseCount;
        List<KeyPhrase> keyphrases = reviewInfo.korphrases;
        for(phraseCount = 0; keyphrases != null && phraseCount < keyphrases.size() ; phraseCount++ ) {
          pstmtInsertHouseCompInfo.setString(1, info.key);
          pstmtInsertHouseCompInfo.setString(2, reviewInfo.review.hashCode() + "");
          pstmtInsertHouseCompInfo.setString(3, "KOR");
          pstmtInsertHouseCompInfo.setInt(4, phraseCount);
          pstmtInsertHouseCompInfo.setString(5, keyphrases.get(phraseCount).getText());
          pstmtInsertHouseCompInfo.setFloat(6, keyphrases.get(phraseCount).getScore().floatValue());

          pstmtInsertHouseCompInfo.addBatch();
        }
        keyphrases = reviewInfo.engphrases;
        for (phraseCount = 0; keyphrases != null && phraseCount < keyphrases.size(); phraseCount++) {
          pstmtInsertHouseCompInfo.setString(1, info.key);
          pstmtInsertHouseCompInfo.setString(2, reviewInfo.review.hashCode() + "");
          pstmtInsertHouseCompInfo.setString(3, "ENG");
          pstmtInsertHouseCompInfo.setInt(4, phraseCount);
          pstmtInsertHouseCompInfo.setString(5, keyphrases.get(phraseCount).getText());
          pstmtInsertHouseCompInfo.setFloat(6, keyphrases.get(phraseCount).getScore().floatValue());

          pstmtInsertHouseCompInfo.addBatch();
        }
      }
      pstmtInsertHouseCompInfo.executeBatch();
      // con.commit();     
    } catch (Exception e) {
      // TODO : for integrity, you can remove below line remark.
      // con.rollback();
      e.printStackTrace();
    } finally {
      if(con != null) try { con.close(); } catch (Exception e) {}
    }
  }
}