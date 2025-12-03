package com.example.web;

import com.example.dao.UserDAO;
import com.example.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/users")

public class UserServlet extends HttpServlet {
    private UserDAO userDAO;
    // regex pour email

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override

    public void init() {
        userDAO = new UserDAO();
    }

    @Override

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "new":
                    showNewForm(req, resp);
                    break;
                case "insert":
                    insertUser(req, resp);
                    break;
                case "delete":
                    deleteUser(req, resp);
                    break;
                case "edit":
                    showEditForm(req, resp);
                    break;
                case "update":
                    updateUser(req, resp);
                    break;
                case "search":
                    searchUser(req, resp);
                    break;
                default:
                    listUser(req, resp);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    // GET and POST both handled via doGet for simplicity; real app would separate

    @Override

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void listUser(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<User> list = userDAO.selectAllUsers();
        req.setAttribute("userList", list);
        req.getRequestDispatcher("/users/list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("action", "insert");
        req.getRequestDispatcher("/users/form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        User existing = userDAO.selectUser(id);
        req.setAttribute("user", existing);
        req.setAttribute("action", "update");
        req.getRequestDispatcher("/users/form.jsp").forward(req, resp);
    }

    private void insertUser(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        String nom = req.getParameter("nom");
        String email = req.getParameter("email");

        String error = validate(nom, email);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("nom", nom);
            req.setAttribute("email", email);
            req.setAttribute("action", "insert");
            req.getRequestDispatcher("/users/form.jsp").forward(req, resp);
            return;
        }

        User newUser = new User(nom, email);
        userDAO.insertUser(newUser);
        resp.sendRedirect(req.getContextPath() + "/users");
    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(req.getParameter("id"));
        String nom = req.getParameter("nom");
        String email = req.getParameter("email");

        String error = validate(nom, email);
        if (error != null) {
            req.setAttribute("error", error);
            User user = new User(id, nom, email);
            req.setAttribute("user", user);
            req.setAttribute("action", "update");
            req.getRequestDispatcher("/users/form.jsp").forward(req, resp);
            return;
        }
        User user = new User(id, nom, email);
        userDAO.updateUser(user);
        resp.sendRedirect(req.getContextPath() + "/users");
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        userDAO.deleteUser(id);
        resp.sendRedirect(req.getContextPath() + "/users");
    }

    private void searchUser(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        String name = req.getParameter("q");
        List<User> list = userDAO.searchByName(name == null ? "" : name);
        req.setAttribute("userList", list);
        req.setAttribute("q", name);
        req.getRequestDispatcher("/users/list.jsp").forward(req, resp);
    }

    private String validate(String nom, String email) {
        if (nom == null || nom.trim().isEmpty()) return "Le nom est obligatoire.";
        if (email == null || email.trim().isEmpty()) return "L'email est obligatoire.";
        if (!EMAIL_PATTERN.matcher(email).matches()) return "Format d'email invalide.";
        return null;
    }
}