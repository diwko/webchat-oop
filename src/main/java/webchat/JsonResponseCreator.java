package webchat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Collection;

public class JsonResponseCreator {
    public JsonObject newChannel(Channel channel) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "channel");
        jsonObject.addProperty("action", "new");
        jsonObject.addProperty("name", channel.getName());
        return jsonObject;
    }

    public JsonObject channelList(Collection<Channel> channels) {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        channels.forEach(channel -> jsonArray.add(channel.getName()));
        jsonObject.addProperty("type", "channels");
        jsonObject.add("names", jsonArray);
        return jsonObject;
    }

    public JsonObject followChannel(Channel channel) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "channel");
        jsonObject.addProperty("action", "follow");
        jsonObject.addProperty("name", channel.getName());
        return jsonObject;
    }

    public JsonObject unfollowChannel(Channel channel) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "channel");
        jsonObject.addProperty("action", "unfollow");
        jsonObject.addProperty("name", channel.getName());
        return jsonObject;
    }

    public JsonObject message(Message message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "message");
        jsonObject.addProperty("user", message.getUser().getName());
        jsonObject.addProperty("date", message.getDate());
        jsonObject.addProperty("text", message.getText());
        return jsonObject;
    }

    public JsonObject message(String user, String date, String text) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "message");
        jsonObject.addProperty("user", user);
        jsonObject.addProperty("date", date);
        jsonObject.addProperty("text", text);
        return jsonObject;
    }

    public JsonObject messagesChannel(Collection<Message> messages) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "messages");
        JsonArray jsonArray = new JsonArray();
        messages.forEach(mes -> jsonArray.add(message(mes).toString()));
        jsonObject.add("messages", jsonArray);
        return jsonObject;
    }
}
