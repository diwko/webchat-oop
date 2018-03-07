package webchat;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class User {
    private Session session;
    private String name;
    private Map<String, Channel> followedChannels = new HashMap<>();

    public User(Session session, String name) {
        this.session = session;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Session getSession() {
        return session;
    }

    public Collection<Channel> getFollowedChannels() {
        return followedChannels.values();
    }

    public void followChannel(Channel channel) {
        if (!followedChannels.containsKey(channel.getName())) {
            followedChannels.put(channel.getName(), channel);
            sendFollowChannel(channel);
        }
    }

    public void unfollowChannel(Channel channel) {
        if (followedChannels.containsKey(channel.getName())) {
            followedChannels.remove(channel.getName());
            sendUnfollowChannel(channel);
        }
    }

    public void unfollowAllChannels() {
        followedChannels.values().forEach(channel -> sendUnfollowChannel(channel));
        followedChannels.clear();
    }

    public void receiveMessage(Message message) {
        JsonResponseCreator creator = new JsonResponseCreator();
        try {
            session.getRemote().sendString(creator.message(message).toString());
        } catch (IOException e) {
            System.out.println("Ktoś nie otrzymał wiadomości");
        }
    }

    public void sendMessage(Message message) {
        JsonResponseCreator creator = new JsonResponseCreator();
        followedChannels.values().forEach(channel -> channel.sendMessage(message));
    }

    private void sendFollowChannel(Channel channel) {
        JsonResponseCreator creator = new JsonResponseCreator();
        try {
            session.getRemote().sendString(creator.followChannel(channel).toString());
        } catch (IOException e) {
            System.out.println("UPS! Bład połączenia");
        }
    }

    private void sendUnfollowChannel(Channel channel) {
        JsonResponseCreator creator = new JsonResponseCreator();
        try {
            session.getRemote().sendString(creator.unfollowChannel(channel).toString());
        } catch (IOException e) {
            System.out.println("UPS! Bład połączenia");
        }
    }

    @Override
    public int hashCode() {
        int result = session.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!session.equals(user.session)) return false;
        return name.equals(user.name);
    }
}
