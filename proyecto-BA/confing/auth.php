<?php
/**
 * Función para autenticar usuarios mediante Autenticación Básica
 */
function authenticate() {
    // Verificar si las credenciales fueron enviadas en la solicitud
    if (!isset($_SERVER['PHP_AUTH_USER']) || !isset($_SERVER['PHP_AUTH_PW'])) {
        // Enviar respuesta de error 401 (No autorizado) y solicitar autenticación
        header('HTTP/1.1 401 Unauthorized');
        header('WWW-Authenticate: Basic realm="Access to the API"');
        echo json_encode(["error" => "Authentication required"]);
        exit;
    }

    // Obtener las credenciales enviadas por el usuario
    $username = $_SERVER['PHP_AUTH_USER'];
    $password = $_SERVER['PHP_AUTH_PW'];

    // Conectar con la base de datos
    $conn = new mysqli('localhost', 'root', '', 'crud_usuarios');
    
    // Verificar si la conexión fue exitosa
    if ($conn->connect_error) {
        die(json_encode(["error" => "Database connection failed"]));
    }

    // Preparar la consulta SQL para verificar el usuario y la contraseña
    $stmt = $conn->prepare("SELECT * FROM users WHERE username = ? AND password = ?");
    $stmt->bind_param("ss", $username, $password);
    $stmt->execute();
    $result = $stmt->get_result();

    // Verificar si las credenciales son correctas
    if ($result->num_rows == 0) {
        header('HTTP/1.1 401 Unauthorized');
        echo json_encode(["error" => "Invalid credentials"]);
        exit;
    }
    
    // Cerrar la conexión a la base de datos
    $conn->close();
} 
?>

