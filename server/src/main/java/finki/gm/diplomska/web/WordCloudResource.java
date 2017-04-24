package finki.gm.diplomska.web;

import finki.gm.diplomska.model.WordCloudModel;
import finki.gm.diplomska.repository.WordCloudRepository;
import finki.gm.diplomska.service.WordCloudService;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/word_cloud", produces = "application/json")
public class WordCloudResource {

  public static final int SLIDER_VALUES_NUMBER = 20;

  @Autowired
  WordCloudService wordCloudService;

  @Autowired
  WordCloudRepository wordCloudRepository;

  //@RequestMapping(method = RequestMethod.GET)
  //public String getWordCloud() {
  //  List<WordCloudModel> allWordClouds = wordCloudRepository.findAll();
  //
  //  List<Map<String, String>> listMaps = wordCloudService.createMapFromWordCloudObjects(allWordClouds);
  //
  //
  //}

  @RequestMapping(value = "/get_dates_for_slider", method = RequestMethod.GET)
  public String getDatesForSlider() {
    return wordCloudService.getDatesForSlider(SLIDER_VALUES_NUMBER);
  }

  @RequestMapping(value = "/slider_values", method = RequestMethod.GET)
  public String getWordCloudsBetweenSliderValues(@RequestParam(value = "fromTime") String fromTime,
      @RequestParam(value = "toTime") String toTime) throws ParseException {

    System.err.println(fromTime);
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date fromDate = formatter.parse(fromTime);
    Timestamp timestampFrom = new Timestamp(fromDate.getTime());
    Date toDate = formatter.parse(toTime);
    Timestamp timestampTo = new Timestamp(toDate.getTime());

    List<WordCloudModel> wordCloudModels = wordCloudService.getWordCloudsBetweenDates(timestampFrom, timestampTo);
    List<Map<String, String>> listMaps = wordCloudService.createMapFromWordCloudObjects(wordCloudModels);
    Map<String, Integer> mergedMapFromMultipleWordClouds =
        wordCloudService.createMergedMapFromMultipleWordClouds(listMaps);

    return wordCloudService.createWordCloudAndBarChartObjects(mergedMapFromMultipleWordClouds);
  }
}
