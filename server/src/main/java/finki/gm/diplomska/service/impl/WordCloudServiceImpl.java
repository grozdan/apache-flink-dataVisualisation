package finki.gm.diplomska.service.impl;

import finki.gm.diplomska.model.WordCloudModel;
import finki.gm.diplomska.repository.WordCloudRepository;
import finki.gm.diplomska.service.WordCloudService;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordCloudServiceImpl implements WordCloudService {

  @Autowired
  WordCloudRepository wordCloudRepository;

  @Override
  public JSONObject createWordJsonObject(String word, int size, int scaledSize, boolean scaleLarger) {
    int scale;
    if (scaleLarger) {
      scale = size * scaledSize;
    } else {
      scale = size / scaledSize;
    }

    JSONObject cloudObj = new JSONObject();
    cloudObj.put("text", word);
    cloudObj.put("size", size * scaledSize);
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
      Timestamp period = new Timestamp(firstDateMillis + i * dividedPeriodMillis);
      datesForSlider.add(dateFormatted);
    }
    String lastPeriod = formatter.format(new Timestamp(lastDateMillis));
    datesForSlider.add(lastPeriod);
    return datesForSlider;
  }
}
