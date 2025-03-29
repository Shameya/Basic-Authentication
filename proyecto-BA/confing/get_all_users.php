<?php
// Establecer el tipo de contenido como JSON
header("Content-Type: application/json");

// Lista de usuarios permitidos para autenticación (usuario:password)
$usuarios_permitidos = [
    "admin" => "password" // Usuario y contraseña que deben coincidir con Android
];

// Verificar si las credenciales de autenticación básica están presentes
if (!isset($_SERVER['PHP_AUTH_USER']) || !isset($_SERVER['PHP_AUTH_PW'])) {
    // Si no están presentes, enviar un error de autenticación requerida
    header("HTTP/1.1 401 Unauthorized");
    echo json_encode(["error" => "Autenticación requerida"]);
    exit;
}

// Obtener el nombre de usuario y la contraseña proporcionados en la autenticación
$username = $_SERVER['PHP_AUTH_USER'];
$password = $_SERVER['PHP_AUTH_PW'];

// Verificar si las credenciales son correctas
if (!isset($usuarios_permitidos[$username]) || $usuarios_permitidos[$username] !== $password) {
    // Si las credenciales no son correctas, devolver un error de "Forbidden"
    header("HTTP/1.1 403 Forbidden");
    echo json_encode(["error" => "Credenciales incorrectas"]);
    exit;
}

// Conexión a la base de datos (ajusta tus credenciales si es necesario)
$conn = new mysqli("localhost", "root", "", "crud_usuarios");
if ($conn->connect_error) {
    // Si hay error en la conexión a la base de datos, devolver un mensaje de error
    die(json_encode(["error" => "Error de conexión a la base de datos"]));
}

// Consultar los usuarios de la base de datos
$query = "SELECT id, username FROM users";
$result = $conn->query($query);

// Verificar si hay usuarios disponibles
if ($result->num_rows > 0) {
    $users = [];
    while ($row = $result->fetch_assoc()) {
        // Agregar cada usuario al array de usuarios
        $users[] = $row;
    }
    // Devolver la lista de usuarios en formato JSON
    echo json_encode($users);
} else {
    // Si no hay usuarios, devolver un mensaje indicando que no hay usuarios
    echo json_encode(["message" => "No hay usuarios"]);
}

// Cerrar la conexión a la base de datos
$conn->close();
?>
