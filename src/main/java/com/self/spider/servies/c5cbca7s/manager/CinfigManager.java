package com.self.spider.servies.c5cbca7s.manager;

import com.self.spider.toolkits.PropertiesKits;
import org.apache.commons.lang3.StringUtils;

/**
 * Author:  liuyao20
 * Date: Created in 2021/12/28 14:37
 * Description：
 */
public class CinfigManager {

    private String prefix;

    private String dowonLoadMark;

    private int num;

    private int startNum;

    public String getPrefix() {
        if(StringUtils.isBlank(prefix)){
            prefix = "http://z11.ef7d6a2b557.rocks/pw/";
        }
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPrefix(String prefix,boolean initAction) {
        this.prefix = prefix;
        if(initAction){
            PropertiesKits.SET_PROPERTIES_NAME.accept(prefix);
        }
    }

    public String getDowonLoadMark() {
        if(StringUtils.isBlank(dowonLoadMark)){
            dowonLoadMark = "91dfgh";
        }
        return dowonLoadMark;
    }

    public void setDowonLoadMark(String dowonLoadMark) {
        this.dowonLoadMark = dowonLoadMark;
    }

    public int getNum() {
        if(num<=0){
            num = 2;
        }
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getStartNum() {
        if(startNum <= 0){
            startNum = 1;
        }
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    private static CinfigManager config = new CinfigManager();

    public static CinfigManager getInstons(){
        return config;
    }


}
