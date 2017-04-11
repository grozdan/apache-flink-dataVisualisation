package finki.gm.diplomska.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Grozdan.Madjarov on 4/4/2017.
 */
@Entity
@Table(name = "word_cloud_model")
public class WordCloudModel {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "tweets_per_country", nullable = false)
  private String tweetsPerCountry;

  @Column(name = "date_time")
  private Timestamp date;

  public WordCloudModel(String tweetsPerCountry, Timestamp dateTime) {
    this.tweetsPerCountry = tweetsPerCountry;
    this.date = dateTime;
  }

  public WordCloudModel() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTweetsPerCountry() {
    return tweetsPerCountry;
  }

  public void setTweetsPerCountry(String tweetsPerCountry) {
    this.tweetsPerCountry = tweetsPerCountry;
  }

  public Timestamp getDateTime() {
    return date;
  }

  public void setDateTime(Timestamp dateTime) {
    this.date = dateTime;
  }
}
