package Servlets;

import Database.DatabaseManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Сервлет для отображения истории запросов погоды пользователя.
 */
@WebServlet("/history")
public class UserHistory extends HttpServlet {
    /**
     * Метод обработки GET-запроса. Получает историю запросов погоды для данного пользователя из базы данных,
     * сохраняет ее в виде списка и передает на JSP-страницу для отображения.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException в случае ошибки при обработке запроса
     * @throws IOException      в случае ошибки ввода/вывода
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));

        // Список для хранения истории запросов
        List<Map<String, Object>> history = new ArrayList<>();


        Connection connection = null;
        try {
            connection = DatabaseManager.getConnection();

            String sql = "SELECT city_name, condition, feelsLike, temp, timestamp FROM weather WHERE user_id = ? ORDER BY timestamp DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String city = resultSet.getString("city_name");
                String condition = resultSet.getString("condition");
                int feelsLike = resultSet.getInt("feelsLike");
                int temp = resultSet.getInt("temp");
                String timestamp = resultSet.getString("timestamp");

                Map<String, Object> entry = new HashMap<>();
                entry.put("city", city);
                entry.put("condition", condition);
                entry.put("feelsLike", feelsLike);
                entry.put("temp", temp);
                entry.put("timestamp", timestamp);

                history.add(entry);
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

        // Устанавливаем атрибут "history" для передачи в JSP
        request.setAttribute("history", history);

        // Перенаправляем запрос на JSP-страницу "history.jsp" для отображения истории запросов
        request.getRequestDispatcher("user-history.jsp").forward(request, response);
    }
}
