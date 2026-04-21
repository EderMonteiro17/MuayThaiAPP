package com.example.muaythaiapp.data.mysql;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Direct JDBC access from Android is useful only for local testing.
 * For production, the app should talk to an API instead of MySQL directly.
 */
public final class DatabaseHelper {

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 5000;
    private static final int DEFAULT_SOCKET_TIMEOUT_MS = 5000;

    private final DatabaseConfig config;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public DatabaseHelper() {
        this(DatabaseConfig.localMysql(""));
    }

    public DatabaseHelper(@NonNull DatabaseConfig config) {
        this(config, Executors.newSingleThreadExecutor());
    }

    DatabaseHelper(
            @NonNull DatabaseConfig config,
            @NonNull ExecutorService executorService
    ) {
        this.config = config;
        this.executorService = executorService;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void testConnection(@NonNull ConnectionCallback callback) {
        executorService.execute(() -> {
            ConnectionResult result = connectInternal();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    @NonNull
    public ConnectionResult testConnectionBlocking() {
        return connectInternal();
    }

    public void shutdown() {
        executorService.shutdownNow();
    }

    @NonNull
    private ConnectionResult connectInternal() {
        try {
            Class.forName(MYSQL_DRIVER);

            try (Connection connection = DriverManager.getConnection(
                    config.toJdbcUrl(),
                    config.getUsername(),
                    config.getPassword()
            )) {
                if (connection != null && !connection.isClosed()) {
                    String successMessage = String.format(
                            Locale.US,
                            "Conexao com o banco '%s' estabelecida com sucesso em %s:%d.",
                            config.getDatabase(),
                            config.getHost(),
                            config.getPort()
                    );
                    return ConnectionResult.success(successMessage);
                }

                return ConnectionResult.failure(
                        "A conexao retornou um estado invalido. Verifique o driver e os parametros."
                );
            }
        } catch (ClassNotFoundException exception) {
            return ConnectionResult.failure(
                    "Driver MySQL nao encontrado. Confirme a dependencia Connector/J no Gradle."
            );
        } catch (SQLException exception) {
            String failureMessage = String.format(
                    Locale.US,
                    "Falha ao acessar '%s' em %s:%d. Detalhe: %s",
                    config.getDatabase(),
                    config.getHost(),
                    config.getPort(),
                    exception.getMessage()
            );
            return ConnectionResult.failure(failureMessage);
        } catch (Exception exception) {
            return ConnectionResult.failure(
                    "Erro inesperado ao testar a conexao MySQL: " + exception.getMessage()
            );
        }
    }

    public interface ConnectionCallback {
        void onResult(@NonNull ConnectionResult result);
    }

    public static final class DatabaseConfig {
        private final String host;
        private final int port;
        private final String database;
        private final String username;
        private final String password;

        public DatabaseConfig(
                @NonNull String host,
                int port,
                @NonNull String database,
                @NonNull String username,
                @Nullable String password
        ) {
            this.host = host;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password == null ? "" : password;
        }

        @NonNull
        public static DatabaseConfig localMysql(@Nullable String password) {
            return new DatabaseConfig(
                    "10.0.2.2",
                    3306,
                    "muaythaiapp",
                    "root",
                    password
            );
        }

        @NonNull
        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        @NonNull
        public String getDatabase() {
            return database;
        }

        @NonNull
        public String getUsername() {
            return username;
        }

        @NonNull
        public String getPassword() {
            return password;
        }

        @NonNull
        String toJdbcUrl() {
            return String.format(
                    Locale.US,
                    "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8"
                            + "&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
                            + "&connectTimeout=%d&socketTimeout=%d",
                    host,
                    port,
                    database,
                    DEFAULT_CONNECT_TIMEOUT_MS,
                    DEFAULT_SOCKET_TIMEOUT_MS
            );
        }
    }

    public static final class ConnectionResult {
        private final boolean success;
        private final String message;

        private ConnectionResult(boolean success, @NonNull String message) {
            this.success = success;
            this.message = message;
        }

        @NonNull
        public static ConnectionResult success(@NonNull String message) {
            return new ConnectionResult(true, message);
        }

        @NonNull
        public static ConnectionResult failure(@NonNull String message) {
            return new ConnectionResult(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        @NonNull
        public String getMessage() {
            return message;
        }
    }
}
