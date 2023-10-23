package Servlets;

import Database.DatabaseManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/edit-user")
public class EditUser extends HttpServlet {
    /**
     * Обрабатывает GET-запросы для страницы редактирования пользователя.
     *
     * @param request  HTTP-запрос от клиента
     * @param response HTTP-ответ, включая данные о пользователе
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Извлекаем идентификатор пользователя из запроса
        int userId = Integer.parseInt(request.getParameter("userId"));

        try (Connection connection = DatabaseManager.getConnection()) {
            // SQL-запрос для получения данных пользователя по его идентификатору
            String sql = "SELECT username, password FROM users WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");

                        // Устанавливаем атрибуты запроса для передачи данных в JSP
                        request.setAttribute("userId", userId);
                        request.setAttribute("username", username);
                        request.setAttribute("password", password);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Перенаправляем на страницу редактирования пользователя
        request.getRequestDispatcher("edit-user.jsp").forward(request, response);
    }

    /**
     * Обрабатывает POST-запросы для обновления данных пользователя.
     *
     * @param request  HTTP-запрос от клиента, содержащий новые данные пользователя
     * @param response HTTP-ответ
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Получаем данные из запроса
        int userId = Integer.parseInt(request.getParameter("userId"));
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection connection = DatabaseManager.getConnection()) {
            // SQL-запрос для обновления данных пользователя
            String sql = "UPDATE users SET username = ?, password = ? WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setInt(3, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Перенаправляем обратно на страницу модерации после обновления
        response.sendRedirect(request.getContextPath() + "/moderation");
    }

    // Вложенный класс для удаления пользователя
    @WebServlet("/delete-user")
    public static class DeleteUser extends HttpServlet {
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            // Извлекаем идентификатор пользователя из запроса
            int userId = Integer.parseInt(request.getParameter("userId"));

            Connection connection = null;
            try {
                connection = DatabaseManager.getConnection();
                // SQL-запрос для удаления пользователя по его идентификатору
                String sql = "DELETE FROM users WHERE user_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, userId);
                    preparedStatement.executeUpdate();
                }
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

            // Перенаправляем обратно на страницу модерации после удаления
            response.sendRedirect(request.getContextPath() + "/moderation");
        }
    }
}
