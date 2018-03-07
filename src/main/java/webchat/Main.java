package webchat;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        staticFiles.location("/public");
        webSocket("/chat", Chat.class);
        init();
    }
}
