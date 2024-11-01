package org.guardevour.models

import kotlinx.serialization.Serializable
import org.guardevour.LocalDateSerializer
import org.guardevour.models.Penaltings.autoIncrement
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.sql.Date
import java.time.LocalDateTime


data class Completing(
    val id: Int,
    val employeeId: Int,
    val taskId: Int,
    val status: String,
    val completingDate: LocalDateTime?
){
    fun toJson(): CompletingJson = CompletingJson(
        id,
        employeeId,
        taskId,
        status,
        completingDate
    )
}
@Serializable
data class CompletingJson(
    val id: Int,
    val employeeId: Int,
    val taskId: Int,
    val status: String,
    @Serializable(with = LocalDateSerializer::class)
    val completingDate: LocalDateTime?
)


object Completings : Table(){
    val id = integer("id").autoIncrement()
    val employeeId = reference("employeeId", Employees.id)
    val taskId = reference("taskId", Tasks.id)
    val status = varchar("status", 20)
    val completingDate = datetime("completingDate").nullable()

    override val primaryKey = PrimaryKey(id)
}