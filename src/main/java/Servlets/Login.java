package Servlets;

import Database.DatabaseManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Сервлет для обработки аутентификации пользователей и перенаправления на страницу с погодой.
 */
@WebServlet("/login")
public class Login extends HttpServlet {
    /**
     * Метод обработки GET-запроса. Отображает страницу входа.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException в случае ошибки при обработке запроса
     * @throws IOException      в случае ошибки ввода/вывода
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    /**
     * Метод обработки POST-запроса. Проверяет аутентификацию пользователя и перенаправляет на страницу с погодой.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException в случае ошибки при обработке запроса
     * @throws IOException      в случае ошибки ввода/вывода
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (isValidLogin(username, password, request)) {
            // Устанавливаем город в атрибуты сессии
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("cityName", getLastSelectedCity(username));

            // Перенаправляем пользователя на страницу погоды на которой отображена погода по послденему городу, если такой есть
            response.sendRedirect("/weather?cityName=" + URLEncoder.encode(session.getAttribute("cityName").toString(), "UTF-8"));
        } else {
            response.sendRedirect("/login.jsp");
        }
    }

    /**
     * Проверяет, авторизацию пользователя.
     *
     * @param username Имя пользователя
     * @param password Пароль пользователя
     * @param request  HTTP-запрос
     * @return true, если аутентификация прошла успешно; в противном случае false
     */
    private boolean isValidLogin(String username, String password, HttpServletRequest request) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Возвращает последний город, по которому пользователь запрашивал погоду.
     *
     * @param username Имя пользователя
     * @return Название города, который был выбран последним пользователем
     */
    private String getLastSelectedCity(String username) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT city_name FROM user_city WHERE user_id = " +
                    "(SELECT user_id FROM users WHERE username = ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("city_name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Москва";
    }
}

