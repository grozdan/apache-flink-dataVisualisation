package finki.gm.diplomska.repository;

import finki.gm.diplomska.model.WordCloudModel;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WordCloudRepository extends JpaRepository<WordCloudModel, Long> {
  @Query("select w from WordCloudModel w " +
      "where w.date between ?1 and ?2")
  List<WordCloudModel> getWordCloudsBetweenDates(Timestamp fromTime, Timestamp toTime);
}
