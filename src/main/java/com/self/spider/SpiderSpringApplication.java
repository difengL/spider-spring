package com.self.spider;

import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import com.self.spider.toolkits.PropertiesKits;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpiderSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpiderSpringApplication.class, args);
        CinfigManager.getInstons().setPrefix(PropertiesKits.GET_PROPERTIES_NAME.get(),false);
    }

}
