package org.guardevour.models

import kotlinx.serialization.Serializable
import org.guardevour.LocalDateSerializer
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.sql.Date
import java.time.LocalDateTime


data class Penalting(
    val id: Int,
    val employeeId: Int,
    val penaltyId: Int,

    val penaltyDate: LocalDateTime
){
    fun toJson() : PenaltingJson = PenaltingJson(
        id,
        employeeId,
        penaltyId,
        penaltyDate
    )
}
@Serializable
data class PenaltingJson(
    val id: Int,
    val employeeId: Int,
    val penaltyId: Int,
    @Serializable(with = LocalDateSerializer::class)
    val penaltyDate: LocalDateTime
)

object Penaltings : Table(){
    val id = integer("id").autoIncrement()
    val employeeId = reference("employeeId", Employees.id)
    val penaltyId = reference("penaltyId", Penalties.id)
    val penaltyDate = datetime("penaltyDate")

    override val primaryKey = PrimaryKey(id)
}
