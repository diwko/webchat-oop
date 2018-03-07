package webchat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class Chat {
    private Map<Session, User> users = new HashMap<>();
    private Map<String, Channel> channels = new HashMap<>();

    public Chat() {
        channels.put("webchat.ChatBot", new ChatBot());
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        sendChannelList(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        removeUser(users.get(session));
    }

    @OnWebSocketMessage
    public void message(Session session, String json) throws IOException {
        parseRequest(session, json);
    }

    private void parseRequest(Session session, String json) {
        try {
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();

            switch (type) {
                case "user":
                    createUser(session, jsonObject);
                    break;
                case "channel":
                    switch (jsonObject.get("action").getAsString()) {
                        case "follow":
                            followChannel(session, jsonObject);
                            break;
                        case "unfollow":
                            unfollowChannel(session, jsonObject);
                            break;
                        case "new":
                            createChannel(session, jsonObject);
                            break;
                        default:
                    }
                    break;
                case "message":
                    Message message = createMessage(session, jsonObject);
                    message.send();
                    break;
                case "keepAlive":
                    break;
                default:
                    System.out.println("Nieznany parametr!");
            }

        } catch (NullPointerException e) {
            System.out.println("UPS! Bład danych");
        }
    }

    private boolean createUser(Session session, JsonObject jsonObject) {
        if (users.containsKey(session))
            return false;

        String name = jsonObject.get("name").getAsString();
        User user = new User(session, name);
        users.put(session, user);
        Channel channel = channels.get("webchat.ChatBot");
        user.followChannel(channel);
        channel.addFollower(user);

        return true;
    }

    private boolean removeUser(User user) {
        if (!users.containsKey(user.getSession()))
            return false;
        unfollowAllChanels(user);
        users.remove(user.getSession());
        return true;
    }

    private boolean createChannel(Session session, JsonObject jsonObject) {
        String channelName = jsonObject.get("name").getAsString();
        if (channels.containsKey(channelName)) {
            return false;
        }
        Channel channel = new Channel(channelName, users.get(session));
        channels.put(channelName, channel);
        sendNewChannel(channel);
        return true;
    }

    private void followChannel(Session session, JsonObject jsonObject) {
        String channelName = jsonObject.get("name").getAsString();
        Channel channelToFollow = channels.get(channelName);
        User follower = users.get(session);

        unfollowAllChanels(follower);

        channelToFollow.addFollower(follower);
        follower.followChannel(channelToFollow);
    }

    private void unfollowChannel(Session session, JsonObject jsonObject) {
        String channelName = jsonObject.get("name").getAsString();
        Channel channelToUnfollow = channels.get(channelName);
        User follower = users.get(session);

        channelToUnfollow.removeFollower(follower);
        follower.unfollowChannel(channelToUnfollow);
    }

    private void unfollowAllChanels(User user) {
        user.getFollowedChannels().forEach(channel -> channel.removeFollower(user));
        user.unfollowAllChannels();
    }

    private Message createMessage(Session session, JsonObject jsonObject) {
        String date = new SimpleDateFormat("HH:mm").format(new Date());
        String text = jsonObject.get("text").getAsString();
        return new Message(users.get(session), date, text);
    }

    //Send to user response with chnnel list
    private void sendChannelList(Session session) {
        JsonResponseCreator creator = new JsonResponseCreator();
        try {
            session.getRemote().sendString(creator.channelList(channels.values()).toString());
        } catch (IOException e) {
            System.out.println("UPS! Bład połączenia");
        }
    }

    //send to all users response with new channel
    private void sendNewChannel(Channel channel) {
        JsonResponseCreator creator = new JsonResponseCreator();
        users.keySet().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.getRemote().sendString(creator.newChannel(channel).toString());
                }
            } catch (IOException e) {
                System.out.println("UPS! Bład połączenia");
            }
        });
    }

}
