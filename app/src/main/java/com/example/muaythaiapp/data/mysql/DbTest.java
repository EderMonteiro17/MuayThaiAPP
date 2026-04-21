package com.example.muaythaiapp.data.mysql;

import android.util.Log;

import androidx.annotation.NonNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JDBC connectivity test for a physical Android device connected over USB.
 * This should be used only as a temporary diagnostic helper.
 */
public final class DbTest {

    private static final String SUCCESS_TAG = "SUCESSO";
    private static final String ERROR_TAG = "DB_ERRO";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL =
            "jdbc:mysql://192.169.1.80:3306/muaythaiapp"
                    + "?useUnicode=true"
                    + "&characterEncoding=UTF-8"
                    + "&serverTimezone=UTC"
                    + "&useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&connectTimeout=5000"
                    + "&socketTimeout=5000";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private final ExecutorService executorService;

    public DbTest() {
        this(Executors.newSingleThreadExecutor());
    }

    DbTest(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void testConnection() {
        executorService.execute(() -> {
            try {
                Class.forName(MYSQL_DRIVER);

                try (Connection connection = DriverManager.getConnection(
                        DB_URL,
                        DB_USER,
                        DB_PASSWORD
                )) {
                    if (connection != null && !connection.isClosed()) {
                        Log.d(SUCCESS_TAG, "Conectado ao MuayThaiApp!");
                    } else {
                        Log.e(ERROR_TAG, "Conexao retornou nula ou fechada.");
                    }
                }
            } catch (Exception exception) {
                Log.e(ERROR_TAG, "Falha ao conectar ao MySQL: " + exception.getMessage(), exception);
            }
        });
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}
