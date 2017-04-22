package finki.gm.diplomska.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import finki.gm.diplomska.model.WordCloudModel;
import finki.gm.diplomska.repository.WordCloudRepository;
import finki.gm.diplomska.service.WordCloudService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/word_cloud", produces = "application/json")
public class WordCloudResource {

  public static final int SLIDER_VALUES_NUMBER = 20;

  @Autowired
  WordCloudService wordCloudService;

  @Autowired
  WordCloudRepository wordCloudRepository;

  @RequestMapping(method = RequestMethod.GET)
  public String getTweetsForCountry() {
    List<WordCloudModel> allWordClouds = wordCloudRepository.findAll();
    List<Map<String, String>> listMaps = allWordClouds.stream().map(wc -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(wc.getTweetsPerCountry(), new TypeReference<Map<String, String>>() {
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
      return new HashMap<String, String>();
    }).collect(Collectors.toList());

    JSONArray cloudArray = new JSONArray();
    JSONArray barChartArray = new JSONArray();
    boolean scaleSizeFlag = true;
    double doubleScaledSize = 0;
    boolean scaleLarger = false;
    for (Map.Entry<String, String> entry : listMaps.get(0).entrySet()) {
      int wordSize = Integer.parseInt(entry.getValue());
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
      JSONObject cloudObj = wordCloudService.createWordJsonObject(entry.getKey(), wordSize, scaledSize, scaleLarger);
      cloudArray.add(cloudObj);

      JSONObject barChartObj =
          wordCloudService.createJsonObjectForBarChart(entry.getKey(), Integer.parseInt(entry.getValue()));
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
  @RequestMapping(value = "/get_dates_for_slider", method = RequestMethod.GET)
  public String getDatesForSlider() {
    return wordCloudService.getDatesForSlider(SLIDER_VALUES_NUMBER);
  }

}
