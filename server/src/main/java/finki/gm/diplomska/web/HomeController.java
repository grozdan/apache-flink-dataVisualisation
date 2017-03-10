package finki.gm.diplomska.web;

import java.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ristes on 12/2/16.
 */
@RestController
@RequestMapping(value = "/api/flink", produces = "application/json")
public class HomeController {

  @RequestMapping(method = RequestMethod.GET)
  public void getFlinkData() throws IOException {
   System.err.println("FLINK DATA");

    Reciever cc = new Reciever();
    cc.getdata();
  }

}