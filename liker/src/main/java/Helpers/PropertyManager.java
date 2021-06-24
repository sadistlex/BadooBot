package Helpers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertyManager {

    public static String getProperty(String key) {
        FileReader fileReader;
        Properties property = new Properties();
        String result = "";
        try {
            fileReader = new FileReader("config.properties");
            property.load(fileReader);
            fileReader.close();
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
        result = property.getProperty(key);
        return result;
    }

    public static void setProperty(String key, String value){
        FileReader fileReader;
        Properties property = new Properties();
        try {
            fileReader = new FileReader("config.properties");
            property.load(fileReader);
            fileReader.close();
            property.setProperty(key, value);
            property.store(new FileWriter("config.properties"), null);
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
    }
}
