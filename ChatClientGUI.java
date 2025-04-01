import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClientGUI {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static JTextArea textArea;
    private static JTextField textField;
    private static String username;

    public static void main(String[] args) {
        String serverName = args.length > 0 ? args[0] : "localhost";
        int port = 5001;

        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        textField = new JTextField();
        frame.add(textField, BorderLayout.SOUTH);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setVisible(true);

        try {
            socket = new Socket(serverName, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Benutzername abfragen
            username = JOptionPane.showInputDialog(frame, "Bitte geben Sie Ihren Benutzernamen ein:");
            out.println(username);

            // Thread zum Empfangen von Nachrichten
            new Thread(new IncomingReader()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        String message = textField.getText();
        if (!message.isEmpty()) {
            out.println(username + ": " + message); // Benutzername wird hier hinzugefuegt
            textField.setText("");
        }
    }

    private static class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    // Hier wird die Nachricht direkt angezeigt, ohne den Benutzernamen erneut hinzuzufuegen
                    textArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}