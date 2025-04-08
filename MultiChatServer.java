import java.io.*;
import java.net.*;
import java.util.concurrent.*;

class MultiChatServer {
    private static final int PORT = 5001;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final ConcurrentHashMap<Socket, PrintWriter> clientWriters = new ConcurrentHashMap<>();
    private static final String EXPECTED_VERSION = "1.0"; // Erwartete Version
    private static final int USERNAME_LIMIT = 20; // Zeichenlimit für den Benutzernamen
    private static final int MESSAGE_LIMIT = 1000; // Zeichenlimit für die Nachricht

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Warte auf Verbindung auf Port " + PORT + ".");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Verbunden mit " + clientSocket.getInetAddress().getHostName() + ".");
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username; // Benutzername des Clients

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Version vom Client empfangen
                String clientVersion = in.readLine();
                if (!clientVersion.equals(EXPECTED_VERSION)) {
                    out.println("Version nicht kompatibel. Erforderlich: " + EXPECTED_VERSION);
                    clientSocket.close();
                    return;
                }

                // Benutzername abfragen
                out.println("Bitte geben Sie Ihren Benutzernamen ein (max. " + USERNAME_LIMIT + " Zeichen): ");
                username = in.readLine();
                
                // Benutzername auf das Limit beschränken
                if (username.length() > USERNAME_LIMIT) {
                    username = username.substring(0, USERNAME_LIMIT); // Kürze den Benutzernamen
                }

                // Benutzername speichern
                synchronized (clientWriters) {
                    clientWriters.put(clientSocket, out);
                }

                System.out.println("Benutzername gesetzt: " + username);
                broadcast(username + " hat den Chat betreten."); // Nachricht, dass der Benutzer beigetreten ist

                String message;
                while ((message = in.readLine()) != null) {
                    // Nachricht auf das Limit beschränken
                    if (message.length() > MESSAGE_LIMIT) {
                        message = message.substring(0, MESSAGE_LIMIT); // Kürze die Nachricht
                    }
                    System.out.println("Nachricht von " + username + ": " + message);
                    broadcast(username + ": " + message); // Benutzername in der Nachricht
                }
            } catch (IOException e) {
                System.out.println("Fehler bei der Kommunikation mit dem Client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(clientSocket);
                }
                broadcast(username + " hat den Chat verlassen."); // Nachricht, dass der Benutzer den Chat verlassen hat
                System.out.println("Verbindung zu " + username + " beendet.");
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters.values()) {
                    writer.println(message);
                }
            }
        }
    }
}