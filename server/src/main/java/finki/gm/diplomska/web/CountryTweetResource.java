package finki.gm.diplomska.web;

import finki.gm.diplomska.model.CountryTweet;
import finki.gm.diplomska.service.CountryTweetService;
import finki.gm.diplomska.service.WordCloudService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.*;
import org.json.simple.JSONArray;
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

  @Autowired
  WordCloudService wordCloudService;

  @RequestMapping(value = "/get_tweets_for_country", method = RequestMethod.GET)
  public String getTweetsForCountry(@RequestParam("country") String country) {
    List<CountryTweet> tweetsForCountry = countryTweetService.getTweetsForCountry(country);
    List<String> wordsInTweets = countryTweetService.createWordsFromTweets(tweetsForCountry);
    JSONArray cloudArray = new JSONArray();
    JSONArray barChartArray = new JSONArray();

    Map<String, Integer> wordCountMap =
        wordsInTweets.stream().collect(groupingBy(Function.identity(), summingInt(e -> 2)));

    wordCountMap = wordCountMap.entrySet().stream()
        //.filter(entry -> entry.getValue()>=20)
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(50)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
            (e1, e2) -> e1, LinkedHashMap::new));

    boolean scaleSizeFlag = true;
    double doubleScaledSize = 0;
    boolean scaleLarger = false;
    for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
      int wordSize = entry.getValue();
      if (scaleSizeFlag) {
        if (wordSize < 100) {
          doubleScaledSize = 100 / wordSize;
          scaleLarger = true;
        } else {
          doubleScaledSize = wordSize * 1.0 / 100;
          scaleLarger = false;
        }
        scaleSizeFlag = false;
      }
      int scaledSize = (int) Math.round(doubleScaledSize);
      JSONObject obj = wordCloudService.createWordJsonObject(entry.getKey(), wordSize, scaledSize, scaleLarger);
      cloudArray.add(obj);

      JSONObject barChartObj = wordCloudService.createJsonObjectForBarChart(entry.getKey(), entry.getValue());
      barChartArray.add(barChartObj);
    }
    JSONObject barChartToReturnObj = new JSONObject();
    barChartToReturnObj.put("key", "Words Number");
    barChartToReturnObj.put("values", barChartArray);

    JSONObject wordCloudBarChartObj = new JSONObject();
    wordCloudBarChartObj.put("wordCloud", cloudArray);
    wordCloudBarChartObj.put("barChart", barChartToReturnObj);
    System.err.println(wordCloudBarChartObj.toString());
    return wordCloudBarChartObj.toString();
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
