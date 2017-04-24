package finki.gm.diplomska.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import finki.gm.diplomska.model.WordCloudModel;
import finki.gm.diplomska.repository.WordCloudRepository;
import finki.gm.diplomska.service.WordCloudService;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordCloudServiceImpl implements WordCloudService {

  public static final double MAX_WORD_SIZE_FOR_WORD_CLOUD = 100.0;

  @Autowired
  WordCloudRepository wordCloudRepository;

  @Override
  public JSONObject createWordCloudJsonObject(String word, int size, double scaledSize, boolean scaleLarger) {
    double scale;
    if (scaleLarger) {
      scale = size * scaledSize;
    } else {
      scale = size / scaledSize;
    }

    JSONObject cloudObj = new JSONObject();
    cloudObj.put("text", word);
    cloudObj.put("size", Math.round(scale));
    return cloudObj;
  }

  @Override
  public JSONObject createJsonObjectForBarChart(String label, int value) {
    JSONObject barChartObj = new JSONObject();
    barChartObj.put("label", label);
    barChartObj.put("value", value);
    return barChartObj;
  }

  @Override
  public String getDatesForSlider(int dividePeriods) {
    JSONArray datesForSliderJsonArray = new JSONArray();
    List<WordCloudModel> allWordClouds = wordCloudRepository.findAll();
    if (!allWordClouds.isEmpty()) {
      List<String> datesForSlider = createDatesForSlider(allWordClouds, dividePeriods);

      for (String period : datesForSlider) {
        JSONObject obj = new JSONObject();
        obj.put("value", period.toString());
        datesForSliderJsonArray.add(obj);
      }
    }
    System.err.println(datesForSliderJsonArray);
    return datesForSliderJsonArray.toString();
  }

  //one wordCloudObject is one row for tweets_per_country with word and number of words
  //allWordClouds can be list of wordClouds between two dates
  @Override
  public List<Map<String, String>> createMapFromWordCloudObjects(List<WordCloudModel> allWordClouds) {
    return allWordClouds.stream().map(wc -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(wc.getTweetsPerCountry(), new TypeReference<Map<String, String>>() {
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
      return new HashMap<String, String>();
    }).collect(Collectors.toList());
  }

  @Override
  public List<WordCloudModel> getWordCloudsBetweenDates(Timestamp timestampFrom, Timestamp timestampTo) {
    return wordCloudRepository.getWordCloudsBetweenDates(timestampFrom, timestampTo);
  }

  @Override
  public Map<String, Integer> createMergedMapFromMultipleWordClouds(List<Map<String, String>> listMaps) {
    Map<String, Integer> mergedMap = new HashMap<>();

    for (Map<String, String> map : listMaps) {
      for (Map.Entry<String, String> entry : map.entrySet()) {
        if (mergedMap.containsKey(entry.getKey())) {
          int newSize = mergedMap.get(entry.getKey()) + Integer.parseInt(entry.getValue());
          mergedMap.put(entry.getKey(), newSize);
        } else {
          mergedMap.put(entry.getKey(), Integer.parseInt(entry.getValue()));
        }
      }
    }

    mergedMap = mergedMap.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(50)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
            (e1, e2) -> e1, LinkedHashMap::new));

    System.err.println(mergedMap);
    //return mergedMap.entrySet().stream()
    //    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
    //    .collect(Collectors.toMap(Map.Entry::getKey,
    //        e -> String.valueOf(e.getValue())));

    return mergedMap;
  }

  @Override
  public String createWordCloudAndBarChartObjects(Map<String, Integer> mergedMapFromMultipleWordClouds) {
    JSONArray cloudArray = new JSONArray();
    JSONArray barChartArray = new JSONArray();
    boolean scaleSizeFlag = true;
    double doubleScaledSize = 0;
    boolean scaleLarger = false;

    //give to this for method created map to scale sizes and creates wordCloud and barChart jsonArrays
    //map must be sorted, starting with word with the most appearance.
    System.err.println(mergedMapFromMultipleWordClouds);
    for (Map.Entry<String, Integer> entry : mergedMapFromMultipleWordClouds.entrySet()) {
      int wordSize = entry.getValue();
      if (scaleSizeFlag) {
        if (wordSize < MAX_WORD_SIZE_FOR_WORD_CLOUD) {
          doubleScaledSize = MAX_WORD_SIZE_FOR_WORD_CLOUD / wordSize;
          scaleLarger = true;
        } else {
          doubleScaledSize = wordSize * 1.0 / MAX_WORD_SIZE_FOR_WORD_CLOUD;
          scaleLarger = false;
        }
        scaleSizeFlag = false;
      }
      //int scaledSize = (int) Math.round(doubleScaledSize);
      JSONObject cloudObj =
          this.createWordCloudJsonObject(entry.getKey(), wordSize, doubleScaledSize, scaleLarger);
      cloudArray.add(cloudObj);

      JSONObject barChartObj =
          this.createJsonObjectForBarChart(entry.getKey(), entry.getValue());
      barChartArray.add(barChartObj);
    }
    JSONObject barChartToReturnObj = new JSONObject();
    barChartToReturnObj.put("key", "Words Number ");
    barChartToReturnObj.put("values", barChartArray);

    JSONObject wordCloudBarChartObj = new JSONObject();
    wordCloudBarChartObj.put("wordCloud", cloudArray);
    wordCloudBarChartObj.put("barChart", barChartToReturnObj);
    System.err.println(wordCloudBarChartObj.toString());
    return wordCloudBarChartObj.toString();
  }

  private List<String> createDatesForSlider(List<WordCloudModel> allWordClouds, int dividePeriods) {
    long firstDateMillis = allWordClouds.get(0).getDateTime().getTime();
    long lastDateMillis = allWordClouds.get(allWordClouds.size() - 1).getDateTime().getTime();
    long differenceBetweenDates = lastDateMillis - firstDateMillis;
    long dividedPeriodMillis = differenceBetweenDates / dividePeriods;
    //dividedPeriodMillis = (long) Math.round(dividedPeriodMillis);
    System.err.println(dividedPeriodMillis);

    List<String> datesForSlider = new ArrayList<>();
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    for (int i = 0; i < dividePeriods - 1; i++) {

      String dateFormatted = formatter.format(new Timestamp(firstDateMillis + i * dividedPeriodMillis));
      datesForSlider.add(dateFormatted);
    }
    String lastPeriod = formatter.format(new Timestamp(lastDateMillis));
    datesForSlider.add(lastPeriod);
    return datesForSlider;
  }
}
