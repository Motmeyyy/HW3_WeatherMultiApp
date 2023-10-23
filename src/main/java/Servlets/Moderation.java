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

/**
 * Сервлет для модерации пользователей. На страницу выводится список всех пользователей
 * При нажатии на пользователя можно отредактировать его данные, удалить его из базы, посмотреть историю запросов погоды
 */
@WebServlet("/moderation")
public class Moderation extends HttpServlet {
    /**
     * Метод обработки GET-запроса. Получает список пользователей из базы данных и перенаправляет на страницу модерации.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException в случае ошибки при обработке запроса
     * @throws IOException      в случае ошибки ввода/вывода
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Connection connection = null;
        try {
            connection = DatabaseManager.getConnection();

            String sql = "SELECT user_id, username FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder userList = new StringBuilder();
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                userList.append("<li><a href=\"/edit-user?userId=").append(userId).append("\">").append(username).append("</a></li>");
            }

            request.setAttribute("userList", userList.toString());
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

        // Переходим на страницу модерации с данными о пользователях
        request.getRequestDispatcher("moderation.jsp").forward(request, response);
    }


}
