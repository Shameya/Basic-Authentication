<?php
// Incluir el archivo de autenticación para verificar la validez de las credenciales
include 'auth.php';
authenticate(); // Llamada a la función authenticate para validar el usuario

// Obtener los datos enviados en la solicitud (JSON) y decodificarlos
$data = json_decode(file_get_contents("php://input"), true);

// Extraer los valores de ID, username y password de los datos decodificados
$id = $data['id'];
$username = $data['username'];
$password = $data['password'];

// Conexión a la base de datos MySQL
$conn = new mysqli('localhost', 'root', '', 'crud_usuarios');

// Preparar la consulta SQL para actualizar el usuario en la base de datos
$stmt = $conn->prepare("UPDATE users SET username = ?, password = ? WHERE id = ?");
$stmt->bind_param("ssi", $username, $password, $id); // Vincular los parámetros para la consulta
$stmt->execute(); // Ejecutar la consulta

// Enviar una respuesta en formato JSON indicando que la operación fue exitosa
echo json_encode(["message" => "Usuario actualizado con éxito"]);

// Cerrar la conexión a la base de datos
$conn->close();
?>

