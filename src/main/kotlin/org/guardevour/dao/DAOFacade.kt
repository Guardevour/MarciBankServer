package org.guardevour.dao

import org.guardevour.dao.Database.dbQuery
import org.guardevour.models.*
import java.time.LocalDateTime

interface DAOFacade {

    //CRUD Отделов
    suspend fun allDepartments(): List<Department>
    suspend fun department(id: Int): Department?
    suspend fun addNewDepartment(name: String): Department?
    suspend fun editDepartment(id: Int, name: String): Boolean
    suspend fun deleteDepartment(id: Int): Boolean

    //CRUD Сотрудников
    suspend fun allEmployees(): List<Employee>
    suspend fun employee(id: Int): Employee?
    suspend fun addNewEmployee(
          surname: String,
          name: String,
          fathername: String,
          login: String,
          password: String,
          accessLevel: String,
          bonusMoney: Double,
          departmentId: Int
    ): Employee?
    suspend fun editEmployee(
           id: Int,
           surname: String,
           name: String,
           fathername: String,
           login: String,
           password: String,
           accessLevel: String,
           bonusMoney: Double,
           departmentId: Int
    ): Boolean
    suspend fun deleteEmployee(id: Int): Boolean

    //CRUD Выполнений
    suspend fun allCompletings(): List<Completing>

    suspend fun completing(id: Int): Completing?

    suspend fun addNewCompleting(
         employeeId: Int,
         taskId: Int,
         status: String,
         completingDate: LocalDateTime
    ): Completing?

    suspend fun editCompleting(
         id: Int,
         employeeId: Int,
         taskId: Int,
         status: String,
         completingDate: LocalDateTime
    ): Boolean

    suspend fun getAllDepartmentAudition(id: Int): List<Completing>

    suspend fun changeMoney(employeeId: Int, moneyValue: Double): Boolean

    suspend fun deleteCompleting(id : Int): Boolean

    suspend fun getLastStatus(id : Int) : String

    //CRUD Штрафов
    suspend fun allPenalties(): List<Penalty>

    suspend fun allPenalties(filter: String): List<Penalty>

    suspend fun penalty(id: Int): Penalty?
    suspend fun addNewPenalty(
         name: String,
         description: String,
         amount: Double
    ): Penalty?
    suspend fun editPenalty(
         id: Int,
         name: String,
         description: String,
         amount: Double
    ): Boolean
    suspend fun deletePenalty(id: Int): Boolean

    //CRUD Штрафований
    suspend fun allPenaltings(): List<Penalting>
    suspend fun penalting(id: Int): Penalting?
    suspend fun addNewPenalting(
         employeeId: Int,
         penaltyId: Int,
         penaltyDate: LocalDateTime
    ): Penalting?
    suspend fun editPenalting(
         id: Int,
         employeeId: Int,
         penaltyId: Int,
         penaltyDate: LocalDateTime
    ): Boolean
    suspend fun deletePenalting(id: Int): Boolean

    //CRUD Задач
    suspend fun allTasks(): List<Task>

    suspend fun task(id: Int): Task?

    suspend fun tasks(ids : List<Int>): List<Pair<Int, String>>

    suspend fun getLastTaskId(): Int

    suspend fun getAllDepartmentTasks(id : Int) : List<Task>

    suspend fun addNewTask(
         name: String,
         description: String,
         employeeId: Int,
         amount: Double,
         creatingDate: LocalDateTime,
         endingDate: LocalDateTime
    ): Task?
    suspend fun editTask(
        id: Int,
        name: String,
        description: String,
        employeeId: Int,
        amount: Double,
        creatingDate: LocalDateTime,
        endingDate: LocalDateTime
    ): Boolean
    suspend fun deleteTask(id: Int): Boolean

    //Спец. Операции
    suspend fun auth(login: String, password: String) : Employee?

    suspend fun getAllEmployeeTasks(id: Int) : List<Completing>

    suspend fun getDepCompInfoLastMonth(): List<String>

    suspend fun getDepCompInfoOfAllTime(): List<String>

    suspend fun salary() : Int
}