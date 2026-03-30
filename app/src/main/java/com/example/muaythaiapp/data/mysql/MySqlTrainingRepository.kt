package com.example.muaythaiapp.data.mysql

import com.example.muaythaiapp.domain.training.TrainingPhase
import com.example.muaythaiapp.domain.training.TrainingRepository
import com.example.muaythaiapp.domain.training.Treino
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.sql.DriverManager
import java.sql.ResultSet
import javax.inject.Inject

/**
 * Implementation of [TrainingRepository] that uses a remote MySQL database via JDBC.
 * Note: Direct JDBC from Android is generally for internal testing.
 */
class MySqlTrainingRepository @Inject constructor(
    private val databaseHelper: DatabaseHelper
) : TrainingRepository {

    override fun observeTreinos(): Flow<List<Treino>> = flow {
        // Simple polling/one-shot for JDBC as it doesn't support native flows
        val config = DatabaseHelper.DatabaseConfig.localMysql("")
        val query = "SELECT * FROM treinos ORDER BY completedAtMillis DESC"
        
        val treinos = mutableListOf<Treino>()
        
        withContext(Dispatchers.IO) {
            try {
                DriverManager.getConnection(
                    config.toJdbcUrl(),
                    config.getUsername(),
                    config.getPassword()
                ).use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery(query).use { resultSet ->
                            while (resultSet.next()) {
                                treinos.add(resultSet.toDomain())
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        emit(treinos)
    }

    override suspend fun saveTreino(treino: Treino): Long = withContext(Dispatchers.IO) {
        val config = DatabaseHelper.DatabaseConfig.localMysql("")
        val sql = """
            INSERT INTO treinos (completedAtMillis, roundNumber, phase, durationSeconds, completedRounds)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        try {
            DriverManager.getConnection(
                config.toJdbcUrl(),
                config.getUsername(),
                config.getPassword()
            ).use { connection ->
                connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { pstmt ->
                    pstmt.setLong(1, treino.completedAtMillis)
                    pstmt.setInt(2, treino.roundNumber)
                    pstmt.setString(3, treino.phase.name)
                    pstmt.setInt(4, treino.durationSeconds)
                    pstmt.setInt(5, treino.completedRounds)
                    
                    pstmt.executeUpdate()
                    
                    pstmt.generatedKeys.use { rs ->
                        if (rs.next()) rs.getLong(1) else -1L
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }

    private fun ResultSet.toDomain(): Treino {
        return Treino(
            id = getLong("id"),
            completedAtMillis = getLong("completedAtMillis"),
            roundNumber = getInt("roundNumber"),
            phase = TrainingPhase.valueOf(getString("phase")),
            durationSeconds = getInt("durationSeconds"),
            completedRounds = getInt("completedRounds")
        )
    }
}
