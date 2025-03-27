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

            // Benutzername abfragen
            System.out.println(in.readLine()); // Nachricht vom Server, um den Benutzernamen einzugeben
            String benutzername = stdIn.readLine(); // Benutzername vom Benutzer einlesen
            out.println(benutzername); // Benutzername an den Server senden

            // Thread zum Empfangen von Nachrichten
            new Thread(() -> {
                try {
                    String nachricht;
                    while ((nachricht = in.readLine()) != null) {
                        System.out.println(nachricht); // Nachricht auf der Konsole ausgeben
                    }
                } catch (IOException e) {
                    System.out.println("Fehler beim Lesen von Nachrichten: " + e.getMessage());
                }
            }).start();

            // Hauptschleife zum Senden von Nachrichten
            String nachricht;
            while (true) {
                System.out.print(benutzername + ": ");
                nachricht = stdIn.readLine();
                if (nachricht == null || nachricht.trim().isEmpty()) {
                    continue; // Leere Nachrichten ignorieren
                } else {
                    out.println(nachricht); // Nachricht an den Server senden
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Verbinden zum Server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MultiChatClient client = new MultiChatClient("localhost", 5001); // Serveradresse und Port
        client.start();
    }
}