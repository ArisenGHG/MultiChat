import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat")
public class MultiChatServer {
    private static final Set<Session> clients = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("Neuer Client verbunden: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Nachricht von " + session.getId() + ": " + message);
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("Client getrennt: " + session.getId());
    }

    private void broadcast(String message) {
        for (Session client : clients) {
            try {
                client.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Starte den WebSocket-Server
        Server server = new Server(8080, "/chat", null, null, new MultiChatServer());
        try {
            server.start();
            System.out.println("Server läuft auf Port 8080...");
            // Halte den Server am Laufen
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}