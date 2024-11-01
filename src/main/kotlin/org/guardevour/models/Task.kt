package org.guardevour.models

import io.ktor.server.http.*
import kotlinx.serialization.Serializable
import org.guardevour.LocalDateSerializer
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.sql.Date
import java.time.LocalDateTime


data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val employeeId: Int,
    val amount: Double,
    val creatingDate: LocalDateTime,
    val endingDate: LocalDateTime
){
    fun toJson() : TaskJson = TaskJson(
        id,
        name,
        description,
        employeeId,
        amount,
        creatingDate,
        endingDate
    )
}


@Serializable
data class TaskJson(
    val id: Int,
    val name: String,
    val description: String,
    val employeeId: Int,
    val amount: Double,
    @Serializable(with = LocalDateSerializer::class)
    val creatingDate: LocalDateTime,
    @Serializable(with = LocalDateSerializer::class)
    val endingDate: LocalDateTime
)

object Tasks : Table(){
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val description = varchar("description", 300)
    val employeeId = reference("employeeId", Employees.id)
    val amount = double("amount")
    val creatingDate =  datetime("creatingDate")
    val endingDate = datetime("endingDate")

    override val primaryKey = PrimaryKey(id)
}