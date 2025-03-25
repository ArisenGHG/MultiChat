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
            pool.execute(new ClientHandler(clientSocket));         }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static final ConcurrentHashMap<Socket, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriters.put(clientSocket, out); 

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Nachricht von " + clientSocket.getInetAddress().getHostName() + ": " + message);
                broadcast(message);
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
            System.out.println("Verbindung zu " + clientSocket.getInetAddress().getHostName() + " beendet.");
        }
    }

    private void broadcast(String message) {
        for (PrintWriter writer : clientWriters.values()) {
            writer.println(message);
        }
    }
}