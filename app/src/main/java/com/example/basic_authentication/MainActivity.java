package com.example.basic_authentication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import android.content.Intent;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    // Campos de entrada para ID, usuario y contraseña
    private EditText userIdField, usernameField, passwordField;
    private Button btnCreate, btnRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de los elementos de la interfaz
        userIdField = findViewById(R.id.txtId);
        usernameField = findViewById(R.id.txtCorreo);
        passwordField = findViewById(R.id.txtContraseña);
        btnCreate = findViewById(R.id.btnCrear);
        btnRead = findViewById(R.id.btnLeer);

        // Deshabilitar el campo de ID para que se llene automáticamente
        userIdField.setEnabled(false);

        // Configurar evento de clic para crear usuario
        btnCreate.setOnClickListener(v -> performRequest("POST", true));

        // Configurar evento de clic para leer usuarios y abrir otra actividad
        btnRead.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListUsersActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Método para realizar una solicitud HTTP al servidor
     * @param method Método HTTP a usar ("POST" para crear usuario)
     * @param updateId Indica si se debe actualizar el campo de ID después de la respuesta
     */
    private void performRequest(String method, boolean updateId) {
        new Thread(() -> {
            try {
                // Obtener los valores ingresados en los campos de usuario y contraseña
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                // Configurar autenticación básica (se debe reemplazar con credenciales reales)
                String auth = "admin:password";
                String encodedAuth = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);

                // URL del servidor donde se encuentra la API
                URL url = new URL("http://localhost/proyecto-BA/config/api.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configurar método HTTP (POST en este caso)
                connection.setRequestMethod(method);
                connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Si el método es POST, enviar datos en formato JSON
                if (method.equals("POST")) {
                    String jsonData = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
                    OutputStream os = connection.getOutputStream();
                    os.write(jsonData.getBytes());
                    os.flush();
                    os.close();
                }

                // Obtener el código de respuesta del servidor
                int responseCode = connection.getResponseCode();

                // Leer la respuesta del servidor
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Manejo de respuesta en el hilo principal
                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_LONG).show();

                        // Si se debe actualizar el ID, obtenerlo del JSON de respuesta
                        if (updateId) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response.toString());
                                String newUserId = jsonResponse.getString("id");
                                userIdField.setText(newUserId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
                    }
                });

                // Cerrar la conexión
                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
