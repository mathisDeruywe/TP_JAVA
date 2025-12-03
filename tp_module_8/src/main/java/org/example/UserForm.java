package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class UserForm extends JFrame {
    private JTextField txtNom, txtEmail;
    private JTable table;
    private DefaultTableModel model;
    private UserDAO dao = new UserDAO();

    private int selectedId = -1;

    public UserForm() {
        setTitle("Gestion des Utilisateurs - CRUD Swing/JDBC");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // -------- FORMULAIRE -------- 

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Nom :"));
        txtNom = new JTextField();
        form.add(txtNom);

        form.add(new JLabel("Email :"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        JButton btnAdd = new JButton("Ajouter");
        JButton btnUpdate = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");

        form.add(btnAdd);
        form.add(btnUpdate);
        form.add(btnDelete);

        // -------- TABLEAU --------

        model = new DefaultTableModel(new String[]{"ID", "Nom", "Email"}, 0);
        table = new JTable(model);
        loadUsers();

        // Ajouter Listener pour sélectionner une ligne
        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
                txtNom.setText(model.getValueAt(row, 1).toString());
                txtEmail.setText(model.getValueAt(row, 2).toString());
            }

        });

        // -------- ACTIONS BOUTONS -------- 

        btnAdd.addActionListener(e -> {
            User u = new User(0, txtNom.getText(), txtEmail.getText());
            if (dao.insertUser(u)) {
                JOptionPane.showMessageDialog(null, "Ajout réussi !");
                loadUsers();
            }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedId != -1) {
                User u = new User(selectedId, txtNom.getText(), txtEmail.getText());
                if (dao.updateUser(u)) {
                    JOptionPane.showMessageDialog(null, "Modification réussie !");
                    loadUsers();
                }
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedId != -1) {
                if (dao.deleteUser(selectedId)) {
                    JOptionPane.showMessageDialog(null, "Suppression réussie !");
                    loadUsers();
                }
            }
        });

        // -------- ASSEMBLAGE --------

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadUsers() {
        model.setRowCount(0);
        for (User u : dao.getAllUsers()) {
            model.addRow(new Object[]{u.getId(), u.getNom(), u.getEmail()});
        }
    }

    public static void main(String[] args) {
        new UserForm().setVisible(true);
    }
}