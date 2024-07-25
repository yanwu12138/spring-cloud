#### 

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Profile("!monitor")
@RestController
public class DisableMonitorUiController {
  
    /***********************
     * 禁用swagger-ui.html
     **********************/
    @RequestMapping(value = "swagger-ui.html", method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.OPTIONS,
            RequestMethod.PUT,
            RequestMethod.DELETE})
    public void getSwagger(HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
    }


    /***********************
     * 禁用druid监控页面
     **********************/
    @RequestMapping(value = "druid/index.html", method = {RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.OPTIONS,
            RequestMethod.PUT,
            RequestMethod.DELETE})
    public void getDruid(HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
    }

}
```





