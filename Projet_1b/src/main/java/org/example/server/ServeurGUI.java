package org.example.server;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServeurGUI extends JFrame {
    private JTextArea zoneLogs;
    private ServerSocket serverSocket;

    private static Map<String, PrintWriter> connectedClients = new ConcurrentHashMap<>();
    private Database db;

    public ServeurGUI() {
        db = new Database();
        setTitle("Serveur Chat - Logs");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        zoneLogs = new JTextArea();
        zoneLogs.setEditable(false);
        add(new JScrollPane(zoneLogs), BorderLayout.CENTER);

        setVisible(true);
        demarrerServeur();
    }

    private void demarrerServeur() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                zoneLogs.append("Serveur démarré sur le port 5000...\n");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new ClientHandler(clientSocket).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String ligne;
                while ((ligne = in.readLine()) != null) {
                    if (ligne.startsWith("LOGIN:")) {
                        String[] parts = ligne.split(":");
                        String user = parts[1];
                        String pass = parts[2];

                        if (db.checkConnection(user, pass)) {
                            this.username = user;
                            connectedClients.put(username, out);
                            out.println("LOGIN_OK");
                            zoneLogs.append("Client connecté : " + username + "\n");
                            broadcastUserList();
                        } else {
                            out.println("LOGIN_FAIL");
                        }
                    }
                    else if (ligne.startsWith("MSG:")) {
                        String[] parts = ligne.split(":", 3);
                        String destinataire = parts[1];
                        String contenu = parts[2];

                        db.saveMessage(this.username, destinataire, contenu);

                        PrintWriter destOut = connectedClients.get(destinataire);
                        if (destOut != null) {
                            destOut.println("MSG:" + this.username + ":" + contenu);
                        }
                    }
                    else if (ligne.startsWith("HISTORY:")) {
                        String contact = ligne.split(":")[1];
                        String history = db.getHistory(this.username, contact);
                        out.println("HISTORY_DATA:" + history.replace("\n", "<br>")); // Astuce simple pour le multiligne
                    }
                }
            } catch (Exception e) {
                if (username != null) {
                    connectedClients.remove(username);
                    broadcastUserList();
                    zoneLogs.append(username + " déconnecté.\n");
                }
            }
        }

        private void broadcastUserList() {
            StringBuilder sb = new StringBuilder("LIST:");
            for (String u : connectedClients.keySet()) {
                sb.append(u).append(",");
            }
            for (PrintWriter writer : connectedClients.values()) {
                writer.println(sb.toString());
            }
        }
    }

    public static void main(String[] args) {
        new ServeurGUI();
    }
}