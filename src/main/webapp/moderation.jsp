<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Модерация</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 0;
        }
        h1 {
            background-color: #f2f2f2 ;
            color: black;
            padding: 20px;
            text-align: center;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .user-list {
            list-style: none;
            padding: 0;
        }
        .user-list li {
            padding: 10px;
            border-bottom: 1px solid #ccc;
        }
        .user-list li:last-child {
            border: none;
        }
    </style>
</head>
<body>
<h1>Модерация</h1>
<div class="container">

    <h2>Список пользователей</h2>
    <div>
        <a href="/create-user" style="text-decoration: none; background-color: black; color: white; padding: 10px 20px; border-radius: 10px;">Добавить</a>
    </div>
    <ul class="user-list">
        <c:forEach items="${userList}" var="username">
            <li>${username}</li>
        </c:forEach>
    </ul>
</div>
</body>
</html>
