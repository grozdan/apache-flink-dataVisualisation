package finki.gm.diplomska.service.impl;

import finki.gm.diplomska.model.CountryTweet;
import finki.gm.diplomska.repository.CountryTweetRepository;
import finki.gm.diplomska.service.CountryTweetService;
import finki.gm.diplomska.service.UtilsService;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryTweetServiceImpl implements CountryTweetService {
  @Autowired
  CountryTweetRepository countryTweetRepository;

  @Autowired
  UtilsService utilsService;

  public List<CountryTweet> getTweetsForCountry(String country) {
    return countryTweetRepository.findAllByCountry(country);
  }

  public void saveTweetForCountry(String country, String tweet) {
    CountryTweet ct = new CountryTweet();
    ct.setCountry(country);
    ct.setTweet(tweet);
    countryTweetRepository.save(ct);
  }

  public List<String> createWordsFromTweets(List<CountryTweet> tweetsForCountry) {
    List<String> resultList = new ArrayList<String>();

    for (CountryTweet ct : tweetsForCountry) {
      StringTokenizer tokenizer = new StringTokenizer(ct.getTweet());
      while (tokenizer.hasMoreTokens()) {
        String word = tokenizer.nextToken().trim().toLowerCase();//.replaceAll("[^A-Za-z0-9#]", "");
        if (word.length() > 2) {
          //word = utilsService.trimWord
          word = StringUtils.stripStart(word, "\";:<>?'/.,~`!@$%^&*()-_+=");
          word = StringUtils.stripEnd(word, "\";/.,~`!@$%^&*()-+=");
          resultList.add(word);
        }
      }
    }
    return resultList;
  }

  @Override
  public List<CountryTweet> getAllTweets() {
    return countryTweetRepository.findAll();
  }
}
