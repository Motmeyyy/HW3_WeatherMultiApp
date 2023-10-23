package Servlets;

import Database.DatabaseManager;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * Сервлет для отображения данных о погоде в выбранном городе.
 */
@WebServlet("/weather")
public class Weather extends HttpServlet {
    /**
     * Метод обработки GET-запроса. Получает название города из параметров запроса и, если город указан,
     * вызывает метод `showWeatherForCity`, в противном случае вызывает `showCityList`.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException в случае ошибки при обработке запроса
     * @throws IOException      в случае ошибки ввода/вывода
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // API-ключ Яндекс.Погоды
        String apiKey = "a055ede3-ddd9-43c1-b9d3-08bf71504984";

        String cityName = request.getParameter("cityName");

        if (cityName != null) {
            showWeatherForCity(cityName, request, response, apiKey);
        } else {
            showCityList(request, response);
        }
    }
    /**
     * Отображает данные о погоде для указанного города и передает их на JSP-страницу для отображения.
     * Также сохраняет информацию о запросе в истории пользователя.
     *
     * @param cityName  имя города
     * @param request   HTTP-запрос
     * @param response  HTTP-ответ
     * @param apiKey    API-ключ для запросов к сервису Яндекс.Погоды
     * @throws ServletException в случае ошибки при обработке запроса
     * @throws IOException      в случае ошибки ввода/вывода
     */
    private void showWeatherForCity(String cityName, HttpServletRequest request, HttpServletResponse response, String apiKey)
            throws ServletException, IOException {
        List<String> cities = getCityListFromDatabase();
        request.setAttribute("cities", cities);

        String lat = "";
        String lon = "";
        Map<String, String> cityData = getCityData(cityName);

        if (cityData != null) {
            lat = cityData.get("lat");
            lon = cityData.get("lon");
        } else {
            response.getWriter().write("Ошибка при получении данных о городе");
            return;
        }

        String apiUrl = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon + "&hours=true&limit=1&extra=false";
        JSONObject jsonObject = sendRequest(apiUrl, apiKey);

        if (jsonObject != null) {
            setWeatherAttributes(jsonObject, cityName, request);

            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("username");

            if (username != null) {
                saveWeatherRequestToHistory(username, cityName, jsonObject);
                saveLastSelectedCity(username, cityName); // Добавляем сохранение последнего выбранного города
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher("/weather.jsp");
            dispatcher.forward(request, response);
        } else {
            response.getWriter().write("Ошибка при запросе данных о погоде");
        }
    }
    /**
     * Сохраняет информацию о запросе погоды в истории пользователя в базе данных.
     *
     * @param username   имя пользователя
     * @param cityName   название города
     * @param jsonObject JSON-объект с данными о погоде
     */
    private void saveWeatherRequestToHistory(String username, String cityName, JSONObject jsonObject) {
        Connection connection = null;

        try {
            connection = DatabaseManager.getConnection();
            String getUserIdSQL = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement getUserIdStatement = connection.prepareStatement(getUserIdSQL);
            getUserIdStatement.setString(1, username);
            ResultSet userResult = getUserIdStatement.executeQuery();

            if (userResult.next()) {
                int userId = userResult.getInt("user_id");
                String conditionTEMP = jsonObject.getJSONObject("fact").getString("condition");
                String condition = getWeatherCondition(conditionTEMP);
                int feelsLike = jsonObject.getJSONObject("fact").getInt("feels_like");
                int temp = jsonObject.getJSONObject("fact").getInt("temp");

                String insertHistorySQL = "INSERT INTO weather (user_id, city_name, condition, feelsLike, temp, timestamp) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement insertHistoryStatement = connection.prepareStatement(insertHistorySQL);
                insertHistoryStatement.setInt(1, userId);
                insertHistoryStatement.setString(2, cityName);
                insertHistoryStatement.setString(3, condition);
                insertHistoryStatement.setInt(4, feelsLike);
                insertHistoryStatement.setInt(5, temp);

                insertHistoryStatement.executeUpdate();
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
    }
    /**
     * Отображает список городов на JSP-странице.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException в случае ошибки при обработке запроса
     * @throws IOException      в случае ошибки ввода/вывода
     */
    private void showCityList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String> cities = getCityListFromDatabase();
        request.setAttribute("cities", cities);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/weather.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Получает список городов из базы данных.
     *
     * @return список городов
     */
    private List<String> getCityListFromDatabase() {
        List<String> cities = new ArrayList<>();
        Connection connectionDB = null;

        try {
            connectionDB = DatabaseManager.getConnection();
            String sql = "SELECT city_name FROM city";
            PreparedStatement preparedStatement = connectionDB.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String city = resultSet.getString("city_name");
                cities.add(city);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connectionDB != null) {
                try {
                    connectionDB.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return cities;
    }
    /**
     * Получает координаты (широту и долготу) для указанного города из базы данных.
     *
     * @param cityName название города
     * @return данные о координатах города (широта и долгота)
     */
    private Map<String, String> getCityData(String cityName) {
        Map<String, String> cityData = new HashMap<>();
        Connection DBconnection = null;

        try {
            DBconnection = DatabaseManager.getConnection();
            String sql = "SELECT lat, lon FROM city WHERE city_name = ?";
            PreparedStatement preparedStatement = DBconnection.prepareStatement(sql);
            preparedStatement.setString(1, cityName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                cityData.put("lat", resultSet.getString("lat"));
                cityData.put("lon", resultSet.getString("lon"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (DBconnection != null) {
                try {
                    DBconnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return cityData;
    }
    /**
     * Отправляет GET-запрос к сервису Яндекс.Погоды и возвращает JSON-ответ.
     *
     * @param apiUrl  URL-адрес для запроса
     * @param apiKey  API-ключ для аутентификации
     * @return JSON-объект с данными о погоде
     */
    private JSONObject sendRequest(String apiUrl, String apiKey) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-API-Key", apiKey);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer responseBuffer = new StringBuffer();

                while ((inputLine = reader.readLine()) != null) {
                    responseBuffer.append(inputLine);
                }
                reader.close();

                return new JSONObject(responseBuffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Устанавливает атрибуты запроса с данными о погоде, чтобы они могли быть отображены на JSP-странице.
     *
     * @param jsonObject JSON-объект с данными о погоде
     * @param cityName    название города
     * @param request     HTTP-запрос
     */
    private void setWeatherAttributes(JSONObject jsonObject, String cityName, HttpServletRequest request) {
        JSONObject fact = jsonObject.getJSONObject("fact");
        JSONObject info = jsonObject.getJSONObject("info");

        int feelsLike = fact.getInt("feels_like");
        int temp = fact.getInt("temp");
        String GETcondition = fact.getString("condition");
        String urlW = info.getString("url");
        String condition = getWeatherCondition(GETcondition);

        request.setAttribute("cityName", cityName);
        request.setAttribute("condition", condition);
        request.setAttribute("feelsLike", feelsLike);
        request.setAttribute("temp", temp);
        request.setAttribute("urlW", urlW);
    }

    /**
     * Переводит состояние погоды с английского на русский
     */
    public static String getWeatherCondition(String conditionCode) {
        Map<String, String> conditionMap = new HashMap<>();
        conditionMap.put("clear", "ясно");
        conditionMap.put("partly-cloudy", "малооблачно");
        conditionMap.put("cloudy", "облачно с прояснениями");
        conditionMap.put("overcast", "пасмурно");
        conditionMap.put("light-rain", "небольшой дождь");
        conditionMap.put("rain", "дождь");
        conditionMap.put("heavy-rain", "сильный дождь");
        conditionMap.put("showers", "ливень");
        conditionMap.put("wet-snow", "дождь со снегом");
        conditionMap.put("light-snow", "небольшой снег");
        conditionMap.put("snow", "снег");
        conditionMap.put("snow-showers", "снегопад");
        conditionMap.put("hail", "град");
        conditionMap.put("thunderstorm", "гроза");
        conditionMap.put("thunderstorm-with-rain", "дождь с грозой");
        conditionMap.put("thunderstorm-with-hail", "гроза с градом");

        return conditionMap.getOrDefault(conditionCode, "Неизвестно");
    }
    /**
     * Сохраняет последний запрошенный пользователем город в базе данных.
     *
     * @param username Имя пользователя
     * @param cityName Название города, который пользователь выбрал
     */
    private void saveLastSelectedCity(String username, String cityName) {
        Connection connection = null;

        try {
            connection = DatabaseManager.getConnection();
            String getUserIdSQL = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement getUserIdStatement = connection.prepareStatement(getUserIdSQL);
            getUserIdStatement.setString(1, username);
            ResultSet userResult = getUserIdStatement.executeQuery();
            if (userResult.next()) {
                int userId = userResult.getInt("user_id");

                String saveCitySQL = "INSERT INTO user_city (user_id, city_name) VALUES (?, ?) " +
                        "ON CONFLICT (user_id) DO UPDATE SET city_name = EXCLUDED.city_name";
                PreparedStatement saveCityStatement = connection.prepareStatement(saveCitySQL);
                saveCityStatement.setInt(1, userId);
                saveCityStatement.setString(2, cityName);
                saveCityStatement.executeUpdate();
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
    }
}




