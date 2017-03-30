package finki.gm.diplomska.service;

import finki.gm.diplomska.model.CountryTweet;
import java.util.List;

public interface CountryTweetService {
  List<CountryTweet> getTweetsForCountry(String country);

  void saveTweetForCountry(String country, String tweet);

  List<String> createWordsFromTweets(List<CountryTweet> tweetsForCountry);

  List<CountryTweet> getAllTweets();
}
