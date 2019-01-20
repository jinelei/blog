package cn.jinelei.rainbow.blog.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author jinelei
 */
public class FileUtils {

    public static String fileRead(File file) throws Exception {
        FileReader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String s = "";
        while ((s =bReader.readLine()) != null) {
            sb.append(s + "\n");
        }
        bReader.close();
        String str = sb.toString();
        return str;
    }

}
