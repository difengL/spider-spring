package com.self.spider.servies.c5cbca7s.manager;

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

    public String getPrefix() {
        if(StringUtils.isBlank(prefix)){
            prefix = "http://q2.2112ks.link/pw/";
        }
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDowonLoadMark() {
        if(StringUtils.isBlank(dowonLoadMark)){
            dowonLoadMark = "/torrent/";
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

    private static CinfigManager config = new CinfigManager();

    public static CinfigManager getInstons(){
        return config;
    }


}
