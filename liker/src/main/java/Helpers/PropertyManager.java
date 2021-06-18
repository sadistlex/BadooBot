package Helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyManager {

    public static String getProperty(String key) {
        FileInputStream fis;
        Properties property = new Properties();
        String result = "";
        try {
            fis = new FileInputStream("config.properties");
            property.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
        result = property.getProperty(key);
        return result;
    }

    public static void setProperty(String key, String value){
        FileInputStream fis;
        Properties property = new Properties();
        String result = "";
        try {
            fis = new FileInputStream("config.properties");
            property.load(fis);
            fis.close();
            property.setProperty(key, value);
            property.store(new FileOutputStream("config.properties"), null);
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
    }
}
