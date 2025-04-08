import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;

public class MultiChatClient {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static JTextArea textArea;
    private static JTextField textField;
    private static String username;
    private static final int USERNAME_LIMIT = 20; // Zeichenlimit für den Benutzernamen
    private static final int MESSAGE_LIMIT = 100; // Zeichenlimit für die Nachricht

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

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        textField = new JTextField();
        panel.add(textField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Senden");
        panel.add(sendButton, BorderLayout.EAST);

        frame.add(panel, BorderLayout.SOUTH);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // KeyListener hinzufügen, um die Eingabe zu überwachen
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (textField.getText().length() >= MESSAGE_LIMIT) {
                    e.consume(); // Verhindert weitere Eingaben, wenn das Limit erreicht ist
                }
            }
        });

        frame.setVisible(true);

        try {
            socket = new Socket(serverName, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Benutzername abfragen
            username = JOptionPane.showInputDialog(frame, "Bitte geben Sie Ihren Benutzernamen ein (max. " + USERNAME_LIMIT + " Zeichen):");
            if (username.length() > USERNAME_LIMIT) {
                username = username.substring(0, USERNAME_LIMIT); // Kürze den Benutzernamen, falls er zu lang ist
            }
            out.println(username);

            // Thread zum Empfangen von Nachrichten
            new Thread(new IncomingReader()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        String message = textField.getText();
        if (message.length() > MESSAGE_LIMIT) {
            message = message.substring(0, MESSAGE_LIMIT); // Kürze die Nachricht, falls sie zu lang ist
        }
        if (!message.isEmpty()) {
            out.println(message); // Benutzername wird hier hinzugefügt
            textField.setText("");
        }
    }

    private static class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    // Hier wird die Nachricht direkt angezeigt, ohne den Benutzernamen erneut hinzuzufügen
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