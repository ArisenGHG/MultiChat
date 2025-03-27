import java.net.*;
import java.io.*;
import java.util.concurrent.*;

class MultiChatServer {
    private static final int PORT = 5001;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10); 

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Warte auf Verbindung auf Port " + PORT + ".");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Verbunden mit " + clientSocket.getInetAddress().getHostName() + ".");
            pool.execute(new ClientHandler(clientSocket));         
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static final ConcurrentHashMap<Socket, PrintWriter> clientWriters = new ConcurrentHashMap<>();
    private String username; // Benutzername des Clients

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriters.put(clientSocket, out); 

            // Benutzername abfragen
            out.println("Bitte geben Sie Ihren Benutzernamen ein: ");
            username = in.readLine();
            System.out.println("Benutzername gesetzt: " + username);

            String message;
            while ((message = in.readLine()) != null) {
                // Überprüfen, ob die Nachricht leer ist
                if (message.trim().isEmpty()) {
                    continue; // Leere Nachrichten ignorieren
                }
                System.out.println("Nachricht von " + username + ": " + message);
                broadcast(username, message); // Benutzername und Nachricht getrennt
            }
        } catch (IOException e) {
            System.out.println("Fehler bei der Kommunikation mit dem Client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientWriters.remove(clientSocket); 
            System.out.println("Verbindung zu " + username + " beendet.");
        }
    }

    private void broadcast(String username, String message) {
        for (PrintWriter writer : clientWriters.values()) {
            writer.println(username + ": " + message + "."); // Benutzername wird hier hinzugefügt
        }
    }
}