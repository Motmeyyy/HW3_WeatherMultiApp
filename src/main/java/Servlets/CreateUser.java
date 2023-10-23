package Servlets;

import Database.DatabaseManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет, предназначенный для обработки создания нового пользователя и регистрации.
 * Этот сервлет обрабатывает GET-запросы для отображения страницы создания пользователя или регистрации.
 * Он также обрабатывает POST-запросы для создания нового пользователя или регистрации с введенными данными.
 * Результат зависит от URL-пути запроса:
 * - Если URL-путь равен "/create-user", будет отображена страница создания пользователя.
 * - Если URL-путь равен "/registration", будет отображена страница регистрации.
 * В случае POST-запроса пользователь будет создан и зарегистрирован в базе данных.
 */
@WebServlet({"/create-user", "/registration"})
public class CreateUser extends HttpServlet {
    /**
     * Обрабатывает GET-запросы для отображения страницы создания пользователя или регистрации.
     * @param request   объект запроса от клиента
     *
     * @param response  объект ответа клиенту
     *
     * @throws ServletException если произошла ошибка в сервлете
     * @throws IOException если произошла ошибка ввода/вывода
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getServletPath().equals("/create-user")) {
            response.sendRedirect("create_user.jsp");
        } else if (request.getServletPath().equals("/registration")) {
            response.sendRedirect("registration.jsp");
        }
    }

    /**
     * Обрабатывает POST-запросы для создания нового пользователя или регистрации с введенными данными.
     * @param request   объект запроса от клиента
     * @param response  объект ответа клиенту
     *
     * @throws ServletException если произошла ошибка в сервлете
     * @throws IOException если произошла ошибка ввода/вывода
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Connection connection = null;
        try {
            connection = DatabaseManager.getConnection();

            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        if (request.getServletPath().equals("/create-user")) {
            response.sendRedirect("moderation");
        } else if (request.getServletPath().equals("/registration")) {
            response.sendRedirect("/registration-success.jsp");
        }
    }
}
