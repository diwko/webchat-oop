package webchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Channel {
    protected String name;
    private User founder;
    private List<User> followers = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public Channel(String name) {
        this.name = name;
        founder = null;
    }

    public Channel(String name, User founder) {
        this.name = name;
        this.founder = founder;
    }

    public String getName() {
        return name;
    }

    public User getFounder() {
        return founder;
    }

    public void addFollower(User user) {
        if (!followers.contains(user)) {
            followers.add(user);
            sendMessages(user);
        }
    }

    public void removeFollower(User user) {
        if (followers.contains(user))
            followers.remove(user);
    }

    //Send message to followers
    public void sendMessage(Message message) {
        messages.add(message);
        followers.forEach(u -> u.receiveMessage(message));
    }

    //Send all messages from channel to user
    private void sendMessages(User user) {
        JsonResponseCreator creator = new JsonResponseCreator();
        try {
            user.getSession().getRemote().sendString(creator.messagesChannel(messages).toString());
        } catch (IOException e) {
            System.out.println("UPS! Bład połączenia");
        }
    }
}
