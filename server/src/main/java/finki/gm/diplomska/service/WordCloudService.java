package finki.gm.diplomska.service;

import finki.gm.diplomska.model.WordCloudModel;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;

public interface WordCloudService {

  JSONObject createWordCloudJsonObject(String word, int size, double scaledSize, boolean scaleLarger);

  JSONObject createJsonObjectForBarChart(String key, int i);

  String getDatesForSlider(int dividePeriods);

  List<Map<String,String>> createMapFromWordCloudObjects(List<WordCloudModel> allWordClouds);

  List<WordCloudModel> getWordCloudsBetweenDates(Timestamp timestampFrom, Timestamp timestampTo);

  Map<String,Integer> createMergedMapFromMultipleWordClouds(List<Map<String,String>> listMaps);

  String createWordCloudAndBarChartObjects(Map<String, Integer> mergedMapFromMultipleWordClouds);
}
