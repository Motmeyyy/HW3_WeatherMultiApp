<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Погода в городе</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            background-color: #f2f2f2;
            color: black;
            padding: 20px;
            margin-top: 0;
            text-align: center;
            font-size: 24px;
        }
        form {
            margin: 20px;
        }
        label {
            display: block;
            margin-bottom: 10px;
        }
        select {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 18px;
        }
        input[type="submit"] {
            background-color: black;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            width: 100%;
            font-size: 18px;
        }
        input[type="submit"]:hover {
            background-color: #333;
        }
        .weather-info {
            margin: 20px;
        }
        p {
            margin: 10px 0;
            font-size: 18px;
        }
        a {
            text-decoration: none;
            background-color: black;
            color: white;
            padding: 5px 10px;
            border-radius: 5px;
        }
        a:hover {
            background-color: #333;
        }
    </style>
</head>
<body>
<h1>Погода в городе ${cityName}:</h1>
<div class="container">
    <form action="/weather" method="GET">
        <label for="citySelect">Выберите город:</label>
        <select id="citySelect" name="cityName">
            <c:forEach items="${cities}" var="city">
                <option value="${city}" ${city eq cityName ? 'selected' : ''}>${city}</option>
            </c:forEach>
        </select>
        <input type="submit" value="Показать погоду">
    </form>
    <div class="weather-info">
        <c:if test="${not empty condition}">
            <p>Сейчас: ${condition}</p>
            <p>Температура: ${temp}°C</p>
            <p>Ощущается как: ${feelsLike}°C</p>
            <p><a href="${urlW}" target="_blank">Подробнее</a></p>
        </c:if>
    </div>
</div>
</body>
</html>
