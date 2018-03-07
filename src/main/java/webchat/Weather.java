package webchat;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class Weather {
    //this link contain deleted key, you have to insert own from openweathermap.org
    private String link = "http://api.openweathermap.org/data/2.5/weather?q=^city&appid=a3a4cdb94a3fbc977cd04b65d1b41812&units=metric";
    private String city;
    private String main;
    private double temperature;
    private double pressure;
    private double humidity;
    private double windSpeed;

    public Weather(String city) throws IOException {
        URL url = new URL(link.replace("^city", city));

        try (InputStreamReader reader = new InputStreamReader(url.openStream(),
                Charsets.UTF_8)) {
            String wheatherJson = CharStreams.toString(reader);
            parseWheather(wheatherJson);
        }
    }

    private void parseWheather(String wheatherJson) {
        JsonObject jsonObject = new JsonParser().parse(wheatherJson).getAsJsonObject();

        city = jsonObject.get("name").getAsString();

        JsonArray wheatherArray = jsonObject.get("weather").getAsJsonArray();
        main = wheatherArray.get(0).getAsJsonObject().get("main").getAsString();

        JsonObject mainValues = jsonObject.get("main").getAsJsonObject();
        temperature = mainValues.get("temp").getAsDouble();
        pressure = mainValues.get("pressure").getAsDouble();
        humidity = mainValues.get("humidity").getAsDouble();

        JsonObject windValues = jsonObject.get("wind").getAsJsonObject();
        windSpeed = windValues.get("speed").getAsDouble();
    }

    @Override
    public String toString() {
        return "Miejsce: " + city + "</br>" +
                "Opis: " + main + "</br>" +
                "Temperatura: " + temperature + " °C" + "</br>" +
                "Ciśnienie: " + pressure + " hPa" + "</br>" +
                "Wilgotność: " + humidity + " %" + "</br>" +
                "Prędkość wiatru: " + windSpeed + " m/s";

    }
}
