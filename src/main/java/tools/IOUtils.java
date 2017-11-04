package tools;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IOUtils {
    public static String readString(File file) {
        StringBuffer str=new StringBuffer("");
        try {
            FileReader fr=new FileReader(file);
            int ch;
            while((ch = fr.read())!=-1 ) {
                str.append((char)ch);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File reader出错");
        }

        return str.toString();

    }
    public static <T> T getObjectData(String jsonString, Class<T> type) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return t;
    }
}
