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

    JSONArray arr = new JSONArray();
    for (Map.Entry<String, String> entry : listMaps.get(0).entrySet()) {
      JSONObject jobj = new JSONObject();
      jobj.put("text", entry.getKey());
      jobj.put("size", Integer.parseInt(entry.getValue()) * 5);
      arr.add(jobj);
    }
    System.err.println(arr.toString());
    return arr.toString();
  }
}
