<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>История запросов</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 0;
        }
        h1 {
            background-color: #f2f2f2;
            color: black;
            padding: 20px;
            text-align: center;
            font-size: 24px;
        }
        table {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        th, td {
            padding: 10px;
            text-align: center;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        table, th, td {
            border: 1px solid #ccc;
            border-collapse: collapse;
        }
    </style>
</head>
<body>
<h1>История запросов погоды</h1>
<table>
    <tr>
        <th>Город</th>
        <th>Состояние</th>
        <th>Ощущается как</th>
        <th>Температура</th>
        <th>Дата и время запроса</th>
    </tr>
    <c:forEach items="${history}" var="request">
        <tr>
            <td>${request.city}</td>
            <td>${request.condition}</td>
            <td>${request.feelsLike} &deg;C</td>
            <td>${request.temp} &deg;C</td>
            <td>${request.timestamp}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
