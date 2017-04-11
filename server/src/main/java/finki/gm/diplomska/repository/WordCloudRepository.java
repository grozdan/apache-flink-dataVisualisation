package finki.gm.diplomska.repository;

import finki.gm.diplomska.model.WordCloudModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordCloudRepository extends JpaRepository<WordCloudModel, Long> {

}
