package finki.gm.diplomska.service;

import org.json.simple.JSONObject;

public interface WordCloudService {

  JSONObject createWordJsonObject(String word, int size, int scaledSize, boolean scaleLarger);

  JSONObject createJsonObjectForBarChart(String key, int i);

  String getDatesForSlider(int dividePeriods);
}
