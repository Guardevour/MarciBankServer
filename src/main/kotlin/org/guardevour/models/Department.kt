package org.guardevour.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


data class Department(val id: Int, val name: String){
    fun toJson(): DepartmentJson = DepartmentJson(
        id,
        name
    )


}
@Serializable
data class DepartmentJson(val id: Int, val name: String)

object Departments: Table(){
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)

    override val primaryKey = PrimaryKey(id)
}