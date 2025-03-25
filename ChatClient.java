import java.net.*;
import java.io.*;

/**
 * Die Klasse ChatClient realisiert den Client f?r einen einfachen
 * Chat. Der Client verbindet sich mit einem ChatServer und liest dann 
 * wechselweise Eingaben vom Socket und gibt sie auf der Konsole aus
 * und wartet dann auf eine Eingabe von der Konsole und schickt sie
 * an den Server.
 * 
 * @author   Dr. Michael Janneck, Torsten Otto
 * @version  2023-06-28
 */
class ChatClient 
{

    public static void main(String[] args) throws IOException 
    {

        /*  Der Name (Adresse) des Servers kann auf der Kommandozeile
         *  ?bergeben werden. Sonst wird "localhost" verwendet.
         */
        String serverName = null;
        if (args.length > 0) {
            serverName = args[0];
        } else {
            serverName = "localhost"; // oder die IP-Adresse des Servers, z. B. 10.7.69.101 oder 192.168.1.7
        }
        
        /*  Es wird versucht, eine Verbindung zum angegebenen Server
         *  aufzubauen. Dazu wird ein Socket-Objekt erzeugt.
         */
        System.out.println("?ffne Verbindung zu " + serverName + " auf Port 5001.");
        Socket verbindung = new Socket(serverName, 5001);

        /*  Hier werden nun Stream-Objekte f?r die Ein- und Ausgabe
         *  erzeugt und zwei String-Objekte deklariert, die die Ein-
         *  und Ausgabezeilen zwischenspeichern.
         */
        PrintWriter ausgang = new PrintWriter(verbindung.getOutputStream(), true);
        BufferedReader eingang = new BufferedReader(new InputStreamReader(
                verbindung.getInputStream()));
        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String nachricht;
        System.out.println("Verbunden mit " + verbindung.getInetAddress().getHostName() + ".");

        /*  Es wird nun wechselweise auf eine Nachricht vom Server gewartet
         *  und diese auf der Konsole ausgegeben, woraufhin eine Eingabe von
         *  der Konsole gelesen wird, die an den Server geschickt wird.
         *  Mit ^D (Unix) bzw. ^Z (Windows) wird die Verbindung beendet.
         */
        while (true) {
 
            /*  Lesen von der Konsole und Schreiben auf den Socket */
            System.out.print("Client: ");
            if ((nachricht = stdIn.readLine()) == null) {
                break;
            } else {
                ausgang.println("Client: " + nachricht);
            }

            /*  Lesen vom Socket und Schreiben auf die Konsole */
            if ((nachricht = eingang.readLine()) == null) {
                break;
            } else {
                System.out.println(nachricht);
            }
        }
        System.out.println("Verbindung beendet.");
        
        /*  Zum Schluss werden alle ge?ffneten Streams und Sockets wieder
         *  in umgekehrter Reihenfolge geschlossen.
         */
        stdIn.close();
        ausgang.close();
        eingang.close();
        verbindung.close();
    }
}