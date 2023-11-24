package run.strive.configcenter.dao

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

class DataAccessBase(jdbcUrl: String, jdbcUsername: String, jdbcPassw: String) {

    private var connection: Connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassw)

    fun getSqlPrepareStatement(sql: String, columnNames: Array<Any>): PreparedStatement {
        val preparedStatement = connection.prepareStatement(sql)
        for (i in columnNames.indices) {
            preparedStatement.setObject(i + 1, columnNames[i])
        }
        return preparedStatement
    }
}
