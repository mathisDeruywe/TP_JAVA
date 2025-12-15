package org.example.client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextArea zoneMessages;
    private JTextField champMessage;
    private JButton btnEnvoyer;
    private JList<String> listUsers;
    private DefaultListModel<String> listModel;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String myUsername;
    private String currentRecipient = null;

    public ClientGUI() {
        if (!authentifier()) {
            System.exit(0);
        }

        setTitle("Chat - Connecté en tant que : " + myUsername);
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        zoneMessages = new JTextArea();
        zoneMessages.setEditable(false);
        add(new JScrollPane(zoneMessages), BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        listUsers = new JList<>(listModel);
        listUsers.setBorder(BorderFactory.createTitledBorder("En ligne"));
        listUsers.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                currentRecipient = listUsers.getSelectedValue();
                if (currentRecipient != null && !currentRecipient.equals(myUsername)) {
                    zoneMessages.setText(""); // Effacer l'écran
                    out.println("HISTORY:" + currentRecipient); // Demander l'historique
                    zoneMessages.append("--- Discussion avec " + currentRecipient + " ---\n");
                }
            }
        });

        JScrollPane scrollList = new JScrollPane(listUsers);
        scrollList.setPreferredSize(new Dimension(150, 0));
        add(scrollList, BorderLayout.EAST);

        JPanel panelBas = new JPanel(new BorderLayout());
        champMessage = new JTextField();
        btnEnvoyer = new JButton("Envoyer");
        btnEnvoyer.addActionListener(e -> envoyerMessage());

        panelBas.add(champMessage, BorderLayout.CENTER);
        panelBas.add(btnEnvoyer, BorderLayout.EAST);
        add(panelBas, BorderLayout.SOUTH);

        setVisible(true);

        ecouterServeur();
    }

    private boolean authentifier() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        Object[] message = {"Nom utilisateur:", userField, "Mot de passe:", passField};

        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                socket = new Socket("localhost", 5000);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("LOGIN:" + userField.getText() + ":" + new String(passField.getPassword()));

                String response = in.readLine();
                if ("LOGIN_OK".equals(response)) {
                    myUsername = userField.getText();
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Erreur Login");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void ecouterServeur() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.startsWith("LIST:")) {
                        String[] users = msg.substring(5).split(",");
                        SwingUtilities.invokeLater(() -> {
                            listModel.clear();
                            for (String u : users) listModel.addElement(u);
                        });
                    }
                    else if (msg.startsWith("MSG:")) {
                        String[] parts = msg.split(":", 3);
                        String sender = parts[1];
                        String content = parts[2];

                        if (sender.equals(currentRecipient)) {
                            zoneMessages.append(sender + ": " + content + "\n");
                        }
                    }
                    else if (msg.startsWith("HISTORY_DATA:")) {
                        String content = msg.substring(13).replace("<br>", "\n");
                        zoneMessages.append(content);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void envoyerMessage() {
        String msg = champMessage.getText();
        if (msg.isEmpty() || currentRecipient == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur et tapez un message.");
            return;
        }

        zoneMessages.append("Moi: " + msg + "\n");
        out.println("MSG:" + currentRecipient + ":" + msg);
        champMessage.setText("");
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}