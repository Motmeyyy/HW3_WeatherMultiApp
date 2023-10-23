<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Редактирование пользователя</title>
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
        form {
            margin: 20px;
        }
        label {
            display: block;
            margin-bottom: 10px;
        }
        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        input[type="submit"],
        input[type="button"] {
            background-color: black;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
        }
        input[type="submit"]:hover,
        input[type="button"]:hover {
            background-color: #333;
        }
    </style>
</head>
<body>
<h1>Редактирование пользователя</h1>
<div class="container">
    <form action="/edit-user" method="post">
        <input type="hidden" name="userId" value="${userId}">
        <label for="username">Имя пользователя:</label>
        <input type="text" name="username" id="username" value="${username}">
        <br>
        <label for="password">Пароль:</label>
        <input type="password" name="password" id="password" value="${password}">
        <br>
        <input type="submit" value="Сохранить изменения">
    </form>
    <form action="/delete-user" method="post">
        <input type="hidden" name="userId" value="${userId}">
        <input type="button" value="Удалить пользователя" onclick="confirmDelete()">
    </form>
    <form action="/history" method="get">
        <input type="hidden" name="userId" value="${userId}">
        <input type="submit" value="История запросов">
    </form>
</div>
<script>
    function confirmDelete() {
        if (confirm("Вы уверены, что хотите удалить пользователя?")) {
            document.forms[1].submit();
        }
    }
</script>
</body>
</html>
