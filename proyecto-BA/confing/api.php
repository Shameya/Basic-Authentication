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

// Obtener el método HTTP de la solicitud
$method = $_SERVER['REQUEST_METHOD'];

// Si el método es POST, realizar una operación de creación (crear usuario)
if ($method == "POST") { 
    // Obtener los datos JSON de la solicitud
    $data = json_decode(file_get_contents("php://input"), true);
    $username = $data["username"];
    $password = password_hash($data["password"], PASSWORD_DEFAULT); // Encriptar la contraseña

    // Preparar la consulta para insertar el nuevo usuario en la base de datos
    $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?, ?)");
    $stmt->bind_param("ss", $username, $password);
    if ($stmt->execute()) {
        // Si se ejecuta correctamente, devolver el mensaje de éxito con el ID del nuevo usuario
        echo json_encode(["message" => "Usuario creado", "id" => $stmt->insert_id]);
    } else {
        // Si ocurre un error, devolver el mensaje de error
        echo json_encode(["error" => "Error al crear usuario"]);
    }
    $stmt->close();
}

// Si el método es GET, realizar una operación de lectura (leer usuarios)
elseif ($method == "GET") { 
    // Consultar todos los usuarios de la base de datos
    $result = $conn->query("SELECT id, username FROM users");
    $users = [];
    while ($row = $result->fetch_assoc()) {
        // Agregar los usuarios al array de usuarios
        $users[] = $row;
    }
    // Devolver la lista de usuarios en formato JSON
    echo json_encode($users);
}

// Si el método es PUT, realizar una operación de actualización (actualizar usuario)
elseif ($method == "PUT") { 
    // Obtener los datos JSON de la solicitud
    $data = json_decode(file_get_contents("php://input"), true);
    $id = $data["id"];
    $username = $data["username"];
    $password = password_hash($data["password"], PASSWORD_DEFAULT); // Encriptar la contraseña

    // Preparar la consulta para actualizar el usuario en la base de datos
    $stmt = $conn->prepare("UPDATE users SET username=?, password=? WHERE id=?");
    $stmt->bind_param("ssi", $username, $password, $id);
    if ($stmt->execute()) {
        // Si la operación es exitosa, devolver el mensaje de éxito
        echo json_encode(["message" => "Usuario actualizado"]);
    } else {
        // Si ocurre un error, devolver el mensaje de error
        echo json_encode(["error" => "Error al actualizar usuario"]);
    }
    $stmt->close();
}

// Si el método es DELETE, realizar una operación de eliminación (eliminar usuario)
elseif ($method == "DELETE") { 
    // Obtener los datos JSON de la solicitud
    $data = json_decode(file_get_contents("php://input"), true);
    $id = $data["id"];

    // Preparar la consulta para eliminar el usuario de la base de datos
    $stmt = $conn->prepare("DELETE FROM users WHERE id=?");
    $stmt->bind_param("i", $id);
    if ($stmt->execute()) {
        // Si la operación es exitosa, devolver el mensaje de éxito
        echo json_encode(["message" => "Usuario eliminado"]);
    } else {
        // Si ocurre un error, devolver el mensaje de error
        echo json_encode(["error" => "Error al eliminar usuario"]);
    }
    $stmt->close();
}

// Cerrar la conexión a la base de datos
$conn->close();
?>

