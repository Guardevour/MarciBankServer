package org.guardevour.models

import kotlinx.serialization.Serializable
import org.guardevour.models.Departments
import org.jetbrains.exposed.sql.Table

data class Employee(
    val id: Int,
    val surname: String,
    val name: String,
    val fathername: String,
    val login: String,
    val password: String,
    val accessLevel: String,
    val bonusMoney: Double,
    val departmentId: Int
){
    fun toJson() : EmployeeJson = EmployeeJson(
        id,
        surname,
        name,
        fathername,
        login,
        password,
        accessLevel,
        bonusMoney,
        departmentId
    )
}

@Serializable
data class EmployeeJson(
    val id: Int,
    val surname: String,
    val name: String,
    val fathername: String,
    val login: String,
    val password: String,
    val accessLevel: String,
    val bonusMoney: Double,
    val departmentId: Int
)


object Employees: Table(){
    val id = integer("id").autoIncrement()
    val surname = varchar("surname", 50)
    val name = varchar("name", 50)
    val fathername = varchar("fathername", 50)
    val login = varchar("login", 10).uniqueIndex()
    val password = varchar("password", 10)
    val accessLevel = varchar("accessLevel", 70)
    val bonusMoney = double("bonusMoney")
    val departmentId = reference("departmentId", Departments.id)

    override val primaryKey = PrimaryKey(id)
}