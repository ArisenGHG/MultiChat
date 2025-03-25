import java.net.*;
import java.io.*;

/**
 * @author   Jonas, Jason
 * @version  2025-03-25
 */
class ChatClient 
{
    public static void main(String[] args) throws IOException 
    {
        String serverName = null;
        if (args.length > 0) {
            serverName = args[0];
        } else {
            serverName = "localhost"; // oder die IP-Adresse des Servers
        }
        
        System.out.println("Öffne Verbindung zu " + serverName + " auf Port 5001.");
        Socket verbindung = new Socket(serverName, 5001);

        PrintWriter ausgang = new PrintWriter(verbindung.getOutputStream(), true);
        BufferedReader eingang = new BufferedReader(new InputStreamReader(verbindung.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        // Benutzernamen abfragen
        System.out.print("Bitte geben Sie Ihren Benutzernamen ein: ");
        String benutzername = stdIn.readLine();
        
        System.out.println("Verbunden mit " + verbindung.getInetAddress().getHostName() + ".");

        while (true) {
            // Lesen von der Konsole und Schreiben auf den Socket
            System.out.print(benutzername + ": ");
            String nachricht = stdIn.readLine();
            if (nachricht == null) {
                break;
            } else {
                ausgang.println(benutzername + ": " + nachricht); // Benutzername in der Nachricht
            }

            // Lesen vom Socket und Schreiben auf die Konsole
            if ((nachricht = eingang.readLine()) == null) {
                break;
            } else {
                System.out.println(nachricht);
            }
        }
        System.out.println("Verbindung beendet.");
        
        stdIn.close();
        ausgang.close();
        eingang.close();
        verbindung.close();
    }
}