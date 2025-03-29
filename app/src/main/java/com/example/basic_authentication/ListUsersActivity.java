package com.example.basic_authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ListUsersActivity extends AppCompatActivity {

    // ListView para mostrar los usuarios
    private ListView listViewUsers;
    // Listas para almacenar los nombres de usuario y sus respectivos IDs
    private ArrayList<String> userList;
    private ArrayList<String> userIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        // Inicialización de la interfaz
        listViewUsers = findViewById(R.id.listViewUsers);
        userList = new ArrayList<>();
        userIdList = new ArrayList<>();

        // Cargar usuarios desde el servidor
        loadUsers();

        // Configurar evento de clic en la lista para editar un usuario
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ListUsersActivity.this, EditUserActivity.class);
            intent.putExtra("userId", userIdList.get(position)); // Enviar el ID del usuario seleccionado
            startActivity(intent);
        });
    }

    /**
     * Método para obtener la lista de usuarios desde el servidor
     */
    private void loadUsers() {
        new Thread(() -> {
            try {
                // Configurar autenticación básica (reemplazar con credenciales reales)
                String auth = "admin:password";
                String encodedAuth = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);

                // URL del servidor donde se encuentra el script para obtener usuarios
                URL url = new URL("http://localhost/proyecto-BA/config/get_all_users.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configurar la solicitud como GET y agregar autenticación
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

                // Obtener código de respuesta del servidor
                int responseCode = connection.getResponseCode();

                // Leer la respuesta del servidor
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Si la respuesta es exitosa, procesar los datos JSON
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        userIdList.add(user.getString("id")); // Guardar el ID del usuario
                        userList.add(user.getString("username")); // Guardar el nombre de usuario
                    }
                }

                // Actualizar la interfaz en el hilo principal
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
                    listViewUsers.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
