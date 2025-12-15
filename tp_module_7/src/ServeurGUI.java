import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ServeurGUI extends JFrame {
    private JTextArea zoneMessages;
    private JTextField champMessage;
    private JButton btnEnvoyer;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ServeurGUI() {
        setTitle("Serveur - Chat TCP");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        zoneMessages = new JTextArea();
        zoneMessages.setEditable(false);
        JScrollPane scroll = new JScrollPane(zoneMessages);

        champMessage = new JTextField();
        btnEnvoyer = new JButton("Envoyer");

        btnEnvoyer.addActionListener(e -> envoyerMessage());

        add(scroll, BorderLayout.CENTER);
        add(champMessage, BorderLayout.NORTH);
        add(btnEnvoyer, BorderLayout.SOUTH);

        setVisible(true);

        demarrerServeur();
    }

    private void demarrerServeur() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                zoneMessages.append("Serveur en attente...\n");
                clientSocket = serverSocket.accept();
                zoneMessages.append("Client connecté.\n");

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String msg;
                while ((msg = in.readLine()) != null) {
                    zoneMessages.append("Client: " + msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void envoyerMessage() {
        String msg = champMessage.getText();
        zoneMessages.append("Serveur: " + msg + "\n");
        out.println(msg);
        champMessage.setText("");
    }

    public static void main(String[] args) {
        new ServeurGUI();
    }
} 