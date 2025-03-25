import java.net.*;
import java.io.*;

public class MultiChatClient {
    private String hostname;
    private int port;
    private PrintWriter out;
    private BufferedReader in;

    public MultiChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void start() {
        try (Socket socket = new Socket(hostname, port)) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            String benutzername = in.readLine(); // Benutzername vom Server abfragen
            System.out.println(benutzername); // Benutzername anzeigen

            new Thread(() -> {
                try {
                    String nachricht;
                    while ((nachricht = in.readLine()) != null) {
                        System.out.println(nachricht);
                    }
                } catch (IOException e) {
                    System.out.println("Fehler beim Lesen von Nachrichten: " + e.getMessage());
                }
            }).start();

            String nachricht;
            while ((nachricht = stdIn.readLine()) != null) {
                out.println(nachricht); // Nachricht an den Server senden
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Verbinden zum Server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MultiChatClient client = new MultiChatClient("localhost", 5001);
        client.start();
    }
}