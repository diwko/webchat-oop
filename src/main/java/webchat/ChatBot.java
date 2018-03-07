package webchat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChatBot extends Channel {
    public ChatBot() {
        super("webchat.ChatBot");
    }

    @Override
    public void sendMessage(Message message) {
        JsonResponseCreator creator = new JsonResponseCreator();
        try {
            message.getUser().getSession().getRemote().sendString(creator.message(message).toString());
            message.getUser().getSession().getRemote().sendString(
                    creator.message("webchat.ChatBot", message.getDate(), parseMessage(message)).toString()
            );
        } catch (IOException e) {
            System.out.println("UPS! Bład połączenia");
        }
    }

    @Override
    public void addFollower(User user) {
        sendInitialMessage(user);
    }

    private String parseMessage(Message message) throws IOException {
        String question = message.getText().toLowerCase();

        if (question.contains("godzina"))
            return getCurrentTime();
        else if (question.contains("dzień"))
            return getCurrentDayOfWeek();
        else if (question.contains("pogoda") && question.contains("krakowie"))
            return getWeather("kraków");
        else if (question.contains("pogoda"))
            return getWeather(question);
        else
            return "Nie wiem :(";
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    private String getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(calendar.getTime());
    }

    private String getWeather(String question) {
        question = question.replaceAll("[^\\w\\sżźćńółęąś]", "");
        String[] words = question.split(" ");

        try {
            Weather weather = new Weather(words[words.length - 1]);
            return weather.toString();
        } catch (IOException e) {
            return "Nie wiem :(";
        }
    }

    private void sendInitialMessage(User user) {
        JsonResponseCreator creator = new JsonResponseCreator();
        try {
            user.getSession().getRemote().sendString(
                    creator.message(
                            name, new SimpleDateFormat("HH:mm").format(new Date()), initialMessageText()
                    ).toString());
        } catch (IOException e) {
            System.out.println("UPS! Bład połączenia");
        }
    }

    private String initialMessageText() {
        return "Cześć! Jestem webchat.ChatBot</br></br>" +
                "Możesz zapytać o:</br>" +
                "- godzinę ('godzina'),</br>" +
                "- dzień tygodnia ('dzień'),</br>" +
                "- pogodę w danym mieście ('pogoda' 'miasto')";
    }
}



