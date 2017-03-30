package finki.gm.diplomska.repository;

import finki.gm.diplomska.model.CountryTweet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryTweetRepository extends JpaRepository<CountryTweet, Long> {
  List<CountryTweet> findAllByCountry(String country);
}
