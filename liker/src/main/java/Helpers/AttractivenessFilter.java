package Helpers;

import io.restassured.response.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class AttractivenessFilter {

    public static String sendImgForRating(byte[] image){
        System.out.println("Sending POST request with image");
        Response response = given()
                .body(image)
                .post("https://api.haystack.ai/api/image/analyze?apikey=bcc555e7f0a8f53f04f3f2abeae71611&output=json&model=attractiveness")
                .then()
                .extract().response();
        return response.asPrettyString();
    }

    public static float getAttractivenessRating(byte[] image){
        String response = sendImgForRating(image);
        float attractiveness = 0;
        Pattern p = Pattern.compile("\"attractiveness\": (.*?),");
        Matcher m = p.matcher(response);
        if (m.find()) {
            String value = m.group(1);
            attractiveness = Float.parseFloat(value);
        }
        System.out.println(attractiveness);
        return attractiveness;
    }

}
