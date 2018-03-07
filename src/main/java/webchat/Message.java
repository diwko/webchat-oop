package webchat;

public class Message {
    private User user;
    private String date;
    private String text;

    public Message(User user, String date, String text) {
        this.user = user;
        this.date = date;
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public void send() {
        user.sendMessage(this);
    }
}