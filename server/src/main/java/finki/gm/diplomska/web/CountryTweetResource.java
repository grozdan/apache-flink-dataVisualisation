package finki.gm.diplomska.web;

import finki.gm.diplomska.model.CountryTweet;
import finki.gm.diplomska.service.CountryTweetService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.*;

@RestController
@RequestMapping(value = "/api/country_tweets", produces = "application/json")
public class CountryTweetResource {

  @Autowired
  CountryTweetService countryTweetService;

  @RequestMapping(value = "/get_tweets_for_country", method = RequestMethod.GET)
  public List<String> getTweetsForCountry(@RequestParam("country") String country) {
    List<CountryTweet> tweetsForCountry = countryTweetService.getTweetsForCountry(country);
    List<String> wordsInTweets = countryTweetService.createWordsFromTweets(tweetsForCountry);
    List<String> result = new ArrayList<>();
    Map<String, Integer> wordCountMap =
        wordsInTweets.stream().collect(groupingBy(Function.identity(), summingInt(e -> 20)));

    wordCountMap = wordCountMap.entrySet().stream()
        .filter(entry -> entry.getValue()>20)
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(100)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
            (e1, e2) -> e1, LinkedHashMap::new));

    for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
      JSONObject obj = new JSONObject();
      obj.put("text", entry.getKey());
      obj.put("size", entry.getValue());
      result.add(obj.toJSONString());
    }
    return result;
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<CountryTweet> getAllTweets() {
    return countryTweetService.getAllTweets();
  }

  //@RequestMapping(value = "/add_tweet_for_country", method = RequestMethod.POST)
  //public void addTweetForCountry(@RequestBody Map<String,Object> requestBody) {
  //  String country = (String) requestBody.get("country");
  //  String tweet = (String) requestBody.get("tweet");
  //
  // // countryTweetService.saveTweetForCountry(country, tweet);
  //}
}
