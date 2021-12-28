package com.self.spider.toolkits;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class GetToolKit {

        public static String get_https (String url){
            String result = null;
            try {
                HttpClient httpClient  = new SSLClient();
                HttpGet httpGet = new HttpGet(new URI(url));
                httpGet.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
                HttpResponse response = httpClient.execute(httpGet);
                if(response != null){
                    HttpEntity resEntity = response.getEntity();
                    if(resEntity != null){
                        result = EntityUtils.toString(resEntity, "utf-8");
                    }
                }
            }catch (Exception e){

            }
            return result;
        }
}
