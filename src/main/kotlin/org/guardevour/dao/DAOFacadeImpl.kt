package org.guardevour.dao

import kotlinx.coroutines.runBlocking
import org.guardevour.Statuses
import org.guardevour.dao.Database.dbQuery
import org.guardevour.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToDepartment(row: ResultRow) = Department(
        id = row[Departments.id],
        name = row[Departments.name]
    )

    private fun resultRowToEmployee(row: ResultRow) = Employee(
        id = row[Employees.id],
        surname = row[Employees.surname],
        name = row[Employees.name],
        fathername = row[Employees.fathername],
        login = row[Employees.login],
        password = row[Employees.password],
        accessLevel = row[Employees.accessLevel],
        bonusMoney = row[Employees.bonusMoney],
        departmentId = row[Employees.departmentId],
    )

    private fun resultRowToCompleting(row: ResultRow) = Completing(
        id = row[Completings.id],
        employeeId = row[Completings.employeeId],
        taskId = row[Completings.taskId],
        status = row[Completings.status],
        completingDate = row[Completings.completingDate],
    )

    private fun resultRowToPenalty(row: ResultRow) = Penalty(
        id = row[Penalties.id],
        name = row[Penalties.name],
        description = row[Penalties.description],
        amount = row[Penalties.amount],
    )

    private fun resultRowToPenalting(row: ResultRow) = Penalting(
        id = row[Penaltings.id],
        employeeId = row[Penaltings.employeeId],
        penaltyId = row[Penaltings.penaltyId],
        penaltyDate = row[Penaltings.penaltyDate],
    )

    private fun resultRowToTask(row: ResultRow) = Task(
        id = row[Tasks.id],
        name = row[Tasks.name],
        description = row[Tasks.description],
        employeeId = row[Tasks.employeeId],
        amount = row[Tasks.amount],
        creatingDate = row[Tasks.creatingDate],
        endingDate = row[Tasks.endingDate],
    )



    override suspend fun allDepartments(): List<Department> = dbQuery {   Departments.selectAll().map(::resultRowToDepartment) }



    override suspend fun department(id: Int): Department? = dbQuery {
        Departments
            .select { Departments.id eq id }
            .map(::resultRowToDepartment)
            .singleOrNull()
    }

    override suspend fun addNewDepartment(name: String): Department? = dbQuery {
        val insertStatement = Departments.insert {
            it[Departments.name] = name
        }
       insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToDepartment)
    }

    override suspend fun editDepartment(id: Int, name: String): Boolean = dbQuery {
        Departments.update({ Departments.id eq id }) {
            it[Departments.name] = name
        } > 0
    }

    override suspend fun deleteDepartment(id: Int): Boolean = dbQuery {
        Departments.deleteWhere { Departments.id eq id } > 0
    }


    override suspend fun allEmployees(): List<Employee> = dbQuery {
        Employees.selectAll().map(::resultRowToEmployee)
    }

    override suspend fun employee(id: Int): Employee?  = dbQuery {
        Employees
            .select { Employees.id eq id }
            .map(::resultRowToEmployee)
            .singleOrNull()
    }


    override suspend fun addNewEmployee(
        surname: String,
        name: String,
        fathername: String,
        login: String,
        password: String,
        accessLevel: String,
        bonusMoney: Double,
        departmentId: Int
    ): Employee? =  dbQuery {
        val insertStatement = Employees.insert {
            it[Employees.surname] = surname
            it[Employees.name] = name
            it[Employees.fathername] = fathername
            it[Employees.login] = login
            it[Employees.password] = password
            it[Employees.accessLevel] = accessLevel
            it[Employees.bonusMoney] = bonusMoney
            it[Employees.departmentId] = departmentId
        }
      insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEmployee)
    }

    override suspend fun editEmployee(
        id: Int,
        surname: String,
        name: String,
        fathername: String,
        login: String,
        password: String,
        accessLevel: String,
        bonusMoney: Double,
        departmentId: Int
    ): Boolean = dbQuery {
        Employees.update({ Employees.id eq id }) {
            it[Employees.surname] =  surname
            it[Employees.name] = name
            it[Employees.fathername] = fathername
            it[Employees.login] = login
            it[Employees.password] = password
            it[Employees.accessLevel] = accessLevel
            it[Employees.bonusMoney] = bonusMoney
            it[Employees.departmentId] = departmentId
        } > 0
    }

    override suspend fun deleteEmployee(id: Int): Boolean = dbQuery {
        Employees.deleteWhere { Employees.id eq id } > 0
    }

    override suspend fun allCompletings(): List<Completing> =  dbQuery {
        Completings.selectAll().map(::resultRowToCompleting)
    }

    override suspend fun completing(id: Int): Completing?  = dbQuery {
        Completings
            .select { Completings.id eq id }
            .map(::resultRowToCompleting)
            .singleOrNull()
    }

    override suspend fun addNewCompleting(
        employeeId: Int,
        taskId: Int,
        status: String,
        completingDate: LocalDateTime
    ): Completing? =  dbQuery {
        val insertStatement = Completings.insert {
            it[Completings.employeeId] = employeeId
            it[Completings.taskId] = taskId
            it[Completings.status] = status
            it[Completings.completingDate] = completingDate
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToCompleting)
    }

    override suspend fun editCompleting(
        id: Int,
        employeeId: Int,
        taskId: Int,
        status: String,
        completingDate: LocalDateTime
    ): Boolean = dbQuery {
        Completings.update({ Completings.id eq id }) {
            it[Completings.employeeId] = employeeId
            it[Completings.taskId] = taskId
            it[Completings.status] = status
            it[Completings.completingDate] = completingDate
        } > 0
    }

    override suspend fun getAllDepartmentAudition(id: Int): List<Completing> = dbQuery{
        Join(
            Completings,
            Employees,
            onColumn = Completings.employeeId,
            otherColumn = Employees.id
        ).slice(
            Completings.id,
            Completings.status,
            Completings.taskId,
            Completings.completingDate,
            Completings.employeeId,
        )
            .select{
                Employees.departmentId eq id and (Completings.status eq Statuses.InAudition.text)
            }
            .map(::resultRowToCompleting)
    }

    override suspend fun changeMoney(employeeId: Int, moneyValue: Double): Boolean = dbQuery{
        Employees.update(
            { Employees.id eq employeeId }
        ) {
            it[Employees.bonusMoney] = Employees.bonusMoney + moneyValue
        } >0
    }

    override suspend fun deleteCompleting(id: Int): Boolean = dbQuery {
        Completings.deleteWhere { Completings.id eq id } > 0
    }

    override suspend fun getLastStatus(id : Int): String = dbQuery {
        Completings
            .select{
                Completings.taskId eq id
            }
            .orderBy(Completings.id, SortOrder.DESC)
            .map(::resultRowToCompleting)
            .first()
            .status
}

    override suspend fun allPenalties(): List<Penalty> =  dbQuery {
        Penalties.selectAll().map(::resultRowToPenalty)
    }

    override suspend fun allPenalties(filter: String): List<Penalty> =  dbQuery {
        Penalties.select{
          Penalties.name.lowerCase() like "%${filter.lowercase()}%" or (Penalties.description.lowerCase() like "%${filter.lowercase()}%")
        }.map(::resultRowToPenalty)
    }

    override suspend fun penalty(id: Int): Penalty?  = dbQuery {
        Penalties
            .select { Penalties.id eq id }
            .map(::resultRowToPenalty)
            .singleOrNull()
    }

    override suspend fun addNewPenalty(name: String, description: String, amount: Double): Penalty? = dbQuery  {
        val insertStatement = Penalties.insert {
            it[Penalties.name] = name
            it[Penalties.description] = description
            it[Penalties.amount] = amount
        }
       insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToPenalty)
    }

    override suspend fun editPenalty(id: Int, name: String, description: String, amount: Double): Boolean = dbQuery {
        Penalties.update({ Penalties.id eq id }) {
            it[Penalties.name] = name
            it[Penalties.description] = description
            it[Penalties.amount] = amount
        } > 0
    }

    override suspend fun deletePenalty(id: Int): Boolean = dbQuery {
        Penalties.deleteWhere { Penalties.id eq id } > 0
    }

    override suspend fun allPenaltings(): List<Penalting> =  dbQuery {
        Penaltings.selectAll().map(::resultRowToPenalting)
    }

    override suspend fun penalting(id: Int): Penalting?  = dbQuery {
        Penaltings
            .select { Penaltings.id eq id }
            .map(::resultRowToPenalting)
            .singleOrNull()
    }

    override suspend fun addNewPenalting(employeeId: Int, penaltyId: Int, penaltyDate: LocalDateTime): Penalting? =  dbQuery  {
        val insertStatement = Penaltings.insert {
            it[Penaltings.employeeId] = employeeId
            it[Penaltings.penaltyId] = penaltyId
            it[Penaltings.penaltyDate] = penaltyDate
        }
       insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToPenalting)
    }

    override suspend fun editPenalting(id: Int, employeeId: Int, penaltyId: Int, penaltyDate: LocalDateTime): Boolean = dbQuery {
        Penaltings.update({ Penaltings.id eq id }) {
            it[Penaltings.employeeId] = employeeId
            it[Penaltings.penaltyId] = penaltyId
            it[Penaltings.penaltyDate] = penaltyDate

        } > 0
    }

    override suspend fun deletePenalting(id: Int): Boolean = dbQuery {
        Penaltings.deleteWhere { Penaltings.id eq id } > 0
    }

    override suspend fun allTasks(): List<Task> =
        Tasks.selectAll().map(::resultRowToTask)


    override suspend fun task(id: Int): Task?  = dbQuery {
        Tasks
            .select { Tasks.id eq id }
            .map(::resultRowToTask)
            .singleOrNull()
    }

    override suspend fun tasks(ids: List<Int>):
            List<Pair<Int, String>> = dbQuery {
        Tasks.slice(Tasks.id, Tasks.name)
            .select { Tasks.id inList(ids) }
            .map{ it[Tasks.id] to it[Tasks.name]}
    }

    override suspend fun getAllDepartmentTasks(id: Int): List<Task> = dbQuery {
        Join(
            Tasks,
            Employees,
            onColumn = Tasks.employeeId,
            otherColumn = Employees.id
        ).slice(
            Tasks.id,
            Tasks.name,
            Tasks.description,
            Tasks.employeeId,
            Tasks.amount,
            Tasks.creatingDate,
            Tasks.endingDate
            )
            .select{
                Employees.departmentId eq id
            }
            .map(::resultRowToTask)
    }

    override suspend fun addNewTask(
        name: String,
        description: String,
        employeeId: Int,
        amount: Double,
        creatingDate: LocalDateTime,
        endingDate: LocalDateTime
    ): Task?  =  dbQuery {
        val insertStatement = Tasks.insert {
            it[Tasks.name] = name
            it[Tasks.description] = description
            it[Tasks.employeeId] = employeeId
            it[Tasks.amount] = amount
            it[Tasks.creatingDate] = creatingDate
            it[Tasks.endingDate] = endingDate
        }
      insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToTask)
    }

    override suspend fun getLastTaskId(): Int = dbQuery{
        Tasks.selectAll().map(::resultRowToTask).last().id
    }

    override suspend fun editTask(
        id: Int,
        name: String,
        description: String,
        employeeId: Int,
        amount: Double,
        creatingDate: LocalDateTime,
        endingDate: LocalDateTime
    ): Boolean = dbQuery {
        Tasks.update({ Tasks.id eq id }) {
            it[Tasks.name] = name
            it[Tasks.description] = description
            it[Tasks.employeeId] = employeeId
            it[Tasks.amount] = amount
            it[Tasks.creatingDate] = creatingDate
            it[Tasks.endingDate] = endingDate
        } > 0
    }

    override suspend fun deleteTask(id: Int): Boolean = dbQuery {
        Tasks.deleteWhere { Tasks.id eq id } > 0
    }

    override suspend fun auth(login: String, password: String): Employee? = dbQuery {
        Employees.select{
            Employees.login eq login
        }.andWhere {
            Employees.password eq password
        }.map(::resultRowToEmployee).singleOrNull()
    }

    override suspend fun getAllEmployeeTasks(id : Int): List<Completing> = dbQuery{
        Completings.select{
            (Completings.employeeId eq id) and (Completings.status eq Statuses.InCompleting.text)
        }.map(::resultRowToCompleting)
    }

    override suspend fun getDepCompInfoLastMonth(): List<String> = dbQuery{
        Completings
            .innerJoin(Employees, onColumn = { employeeId }, otherColumn = { id })
            .innerJoin(Departments, onColumn = { Employees.departmentId }, otherColumn = { id })
            .slice(
                Departments.name
            )
            .select{
                Completings.status eq Statuses.Completed.text and(Completings.completingDate greater (LocalDateTime.now().minusMonths(1)))
            }
            .groupBy{
                it[Departments.name]
            }
            .map{
                "${it.key} : ${it.value.count()}  задач выполнено"
            }
    }

    override suspend fun getDepCompInfoOfAllTime(): List<String> = dbQuery{
        Completings
            .innerJoin(Employees, onColumn = { employeeId }, otherColumn = { id })
            .innerJoin(Departments, onColumn = { Employees.departmentId }, otherColumn = { id })
            .slice(Departments.name)
            .select{
                Completings.status eq Statuses.Completed.text
            }
            .groupBy{
                it[Departments.name]
            }
            .map{
                "${it.key} : ${it.value.count()} задач выполнено"
            }
    }

    override suspend fun salary(): Int = dbQuery {
        Employees.update({ Employees.id greaterEq 0 }){
            it[Employees.bonusMoney] = 0.0
        }
    }


}
val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
        if(allDepartments().isEmpty()) {
            addNewDepartment("Администраторы")
        }
    }
}