package com.example.basic_authentication;

import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditUserActivity extends AppCompatActivity {

    // Campos de entrada para editar usuario
    private EditText usernameField, passwordField;
    // Botones para actualizar y eliminar usuario
    private Button btnUpdate, btnDelete;
    // ID del usuario que se está editando
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Vinculación de los elementos de la interfaz con el código
        usernameField = findViewById(R.id.txtEditCorreo);
        passwordField = findViewById(R.id.txtEditarContra);
        btnUpdate = findViewById(R.id.btnEditar2);
        btnDelete = findViewById(R.id.btnEliminar);

        // Obtener el ID del usuario desde el intent
        userId = getIntent().getStringExtra("userId");

        // Configurar botones para actualizar y eliminar usuario
        btnUpdate.setOnClickListener(v -> performRequest("PUT"));
        btnDelete.setOnClickListener(v -> performRequest("DELETE"));
    }

    /**
     * Método para realizar una solicitud HTTP (actualizar o eliminar un usuario)
     * @param method Método HTTP a utilizar ("PUT" para actualizar, "DELETE" para eliminar)
     */
    private void performRequest(String method) {
        new Thread(() -> {
            try {
                // Obtener los valores ingresados en los campos de texto
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                // Configurar autenticación básica (reemplazar con credenciales reales)
                String auth = "admin:password";
                String encodedAuth = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);

                // Definir la URL de la API para actualizar o eliminar usuario
                URL url = new URL("http://localhost/proyecto-BA/config/update.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configurar la solicitud HTTP
                connection.setRequestMethod(method);
                connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Crear el JSON con los datos del usuario
                String jsonData = "{ \"id\": \"" + userId + "\", \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
                OutputStream os = connection.getOutputStream();
                os.write(jsonData.getBytes());
                os.flush();
                os.close();

                // Obtener la respuesta del servidor
                int responseCode = connection.getResponseCode();
                runOnUiThread(() -> Toast.makeText(this, responseCode == 200 ? "Operación exitosa" : "Error", Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
