import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextArea zoneMessages;
    private JTextField champMessage;
    private JButton btnEnvoyer;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientGUI() {
        setTitle("Client - Chat TCP");
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

        connecterServeur();
    }

    private void connecterServeur() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 5000);
                zoneMessages.append("Connecté au serveur.\n");

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String msg;
                while ((msg = in.readLine()) != null) {
                    zoneMessages.append("Serveur: " + msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void envoyerMessage() {
        String msg = champMessage.getText();
        zoneMessages.append("Client: " + msg + "\n");
        out.println(msg);
        champMessage.setText("");
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
} 