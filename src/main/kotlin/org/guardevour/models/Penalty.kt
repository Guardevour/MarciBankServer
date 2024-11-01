package org.guardevour.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


data class Penalty(
    val id: Int,
    val name: String,
    val description: String,
    val amount: Double
){
    fun toJson() : PenaltyJson = PenaltyJson(
        id,
        name,
        description,
        amount
    )
}

@Serializable
data class PenaltyJson(
    val id: Int,
    val name: String,
    val description: String,
    val amount: Double
)

object Penalties : Table(){
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val description = varchar("description", 300)
    val amount = double("amount")

    override val primaryKey = PrimaryKey(id)
}
