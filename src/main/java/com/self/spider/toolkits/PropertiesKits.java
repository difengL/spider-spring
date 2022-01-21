package com.self.spider.toolkits;


import com.self.spider.servies.c5cbca7s.manager.CinfigManager;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author:  liuyao20
 * Date: Created in 2022/1/21 11:28
 * Description：
 */
public class PropertiesKits {


    private static String path = null;

    static {
        try {
            path = System.getProperty("user.dir")+"/sourcepath.properties";
            System.out.println(path);
            File file = new File(path);
            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException ignored) {}
    }


    public static final Supplier<String> GET_PROPERTIES_NAME = () -> {
        String result = null;
        try {
            final Properties prop = new Properties();
            final InputStream in = new BufferedInputStream(new FileInputStream(path));
            prop.load(in);
            result = prop.getProperty("basePath");
            in.close();
            if(StringUtils.isBlank(result)){
                prop.setProperty("basePath", CinfigManager.getInstons().getPrefix());
                FileOutputStream oFile = new FileOutputStream(path);
                prop.store(oFile, null);
                oFile.close();
                result = CinfigManager.getInstons().getPrefix();
            }
        }catch (Exception ignored){}
        return result;
    };


    public static final Consumer<String> SET_PROPERTIES_NAME = basePath -> {
        try {
            final Properties prop = new Properties();
            prop.setProperty("basePath",basePath);
            FileOutputStream oFile = new FileOutputStream(path);
            prop.store(oFile, null);
            oFile.close();
        } catch (IOException ignored) {}
    };


}
