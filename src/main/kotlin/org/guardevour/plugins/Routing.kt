package org.guardevour.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.guardevour.Statuses
import org.guardevour.Titles
import org.guardevour.dao.dao
import java.time.LocalDateTime
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

fun Application.configureRouting() {
    routing {
        get("/employees") {
            call.respond(dao.allEmployees().map {
                it.toJson()
            })
        }
        post("/auth") {
            val formParameters = call.receiveParameters()
            val login = formParameters["login"]
            val pass = formParameters["password"]

            login?.let {
                pass?.let {
                    dao.auth(login, pass)?.let {
                        call.respond(it.toJson())
                    }
                }
            }
        }
        get("/employee/delete/{id}"){
            call.parameters["id"]?.let {
                dao.deleteEmployee(it.toInt())
            }
        }
        get("/department/{id}") {
            call.parameters["id"]?.toInt()
                ?.let { id -> dao.department(id)?.name?.let { depName -> call.respondText(depName) } }
        }
        get("/completings/{employeeId}") {
            call.parameters["employeeId"]?.toInt()?.let { employeeId ->
                val completingJsonList = dao.getAllEmployeeTasks(employeeId).map {
                    it.toJson()
                }
                val tasks = dao.tasks(completingJsonList.map { it.taskId })
                val jsons = completingJsonList.associateBy { completingJson ->
                    tasks.find {
                        it.first == completingJson.taskId
                    }?.second
                }
                call.respond(jsons)
            }
        }
        post("/completings/edit") {
            val taskParameters = call.receiveParameters()
            dao.editCompleting(
                taskParameters["id"]!!.toInt(),
                taskParameters["employeeId"]!!.toInt(),
                taskParameters["taskId"]!!.toInt(),
                taskParameters["status"]!!.toString(),
                LocalDateTime.now()
            )
        }
        get("/tasks/all/{departmentId}") {
            call.parameters["departmentId"]?.toInt()?.let {id->
                call.respond(
                    dao.getAllDepartmentTasks(id)
                        .map { it.toJson() }
                        .associateBy { json ->
                        dao.getLastStatus(json.id)
                    }
                        .filter {
                            it.key == Statuses.NotInProgress.text || it.key == Statuses.Aborted.text
                        }
                )
            }
        }
        post("/tasks/edit") {
            call.receiveParameters().let {taskParameters->
                dao.editTask(
                    id = taskParameters["id"]?.toInt() ?: 0,
                    name = taskParameters["name"] ?: "",
                    amount = taskParameters["amount"]?.toDouble() ?: 0.0,
                    description = taskParameters["description"] ?: "",
                    employeeId = taskParameters["employeeId"]?.toInt() ?: 0,
                    creatingDate =  LocalDateTime.parse(taskParameters["creatingDate"]),
                    endingDate = LocalDateTime.parse(taskParameters["endingDate"])
                )
                call.respond("Успешно")
            }
        }

        post("/employee/edit"){
            call.receiveParameters().let {taskParameters->
                dao.editEmployee(
                    id = taskParameters["id"]?.toInt() ?: 0,
                    name = taskParameters["name"] ?: "",
                    surname = taskParameters["surname"] ?: "",
                    fathername = taskParameters["fathername"] ?: "",
                    accessLevel = taskParameters["accessLevel"] ?: "",
                    bonusMoney = taskParameters["bonusMoney"]?.toDouble() ?: 0.0,
                    departmentId =taskParameters["departmentId"]?.toInt() ?: 0,
                    login = taskParameters["login"] ?: "",
                    password = taskParameters["password"] ?: ""
                )
                call.respond("Успешно")
            }
        }
        post("/employee/add"){
            call.receiveParameters().let {taskParameters->
                dao.addNewEmployee(
                    name = taskParameters["name"] ?: "",
                    surname = taskParameters["surname"] ?: "",
                    fathername = taskParameters["fathername"] ?: "",
                    accessLevel = taskParameters["accessLevel"] ?: "",
                    bonusMoney = taskParameters["bonusMoney"]?.toDouble() ?: 0.0,
                    departmentId =taskParameters["departmentId"]?.toInt() ?: 0,
                    login = taskParameters["login"] ?: "",
                    password = taskParameters["password"] ?: ""
                )
                call.respond("Успешно")
            }
        }

        get("/penalty/{filter}"){
            call.parameters["filter"]?.let {
                call.respond(
                   dao.allPenalties(
                       it
                   ).map { penalty->
                       penalty.toJson()
                   }
                )
            }
        }
        get("/penalty/"){
                call.respond(
                    dao.allPenalties().map { penalty ->
                        penalty.toJson()
                    }
                )
        }

        post("/tasks/create") {
            call.receiveParameters().let {taskParameters->
                dao.addNewTask(
                    name = taskParameters["name"] ?: "",
                    amount = taskParameters["amount"]?.toDouble() ?: 0.0,
                    description = taskParameters["description"] ?: "",
                    employeeId = taskParameters["employeeId"]?.toInt() ?: 0,
                    creatingDate =  LocalDateTime.parse(taskParameters["creatingDate"]),
                    endingDate = LocalDateTime.parse(taskParameters["endingDate"])
                )
                dao.addNewCompleting(
                    taskId = dao.getLastTaskId(),
                    employeeId = taskParameters["employeeId"]?.toInt() ?: 0,
                    completingDate = LocalDateTime.now(),
                    status = Statuses.NotInProgress.text
                )

                call.respond("Успешно")
            }

        }
        post("/tasks/delete") {
            call.receiveParameters().let{taskParameters->
                dao.deleteTask(
                    id = taskParameters["id"]?.toInt() ?: 0,
                )
                call.respond("Успешно")
            }
        }
        post("/completing/add"){
            call.receiveParameters().let{taskParameters->
                dao.addNewCompleting(
                    taskId = taskParameters["taskId"]?.toInt() ?: 0,
                    completingDate = LocalDateTime.now(),
                    employeeId = taskParameters["employeeId"]?.toInt() ?: 0,
                    status = Statuses.InCompleting.text
                )
            }
        }
        post("/completing/audition"){
            call.receiveParameters().let {taskParameters->
                call.respond(
                    dao.getAllDepartmentAudition(taskParameters["departmentId"]?.toInt() ?: 0)
                        .map { it.toJson() }
                        .associateBy { json ->
                            dao.task(json.taskId).let {
                                "${ it!!.name }|${ it.amount }"
                            }
                        }
                )
            }
        }

        get("/employee/{id}/changemoney/{value}") {
            call.parameters["id"].let { id->
                call.parameters["value"].let {value->
                    if (id != null && value != null) {
                        dao.changeMoney(
                            id.toInt(),
                            value.toDouble()
                        )
                    }
                }
            }
        }

        get("/departments/results/{islastmonth}"){
            call.parameters["islastmonth"]?.toBoolean()?.let {isLastMonth->
                if (isLastMonth){
                    call.respond(dao.getDepCompInfoLastMonth())
                }
                else{
                    call.respond(dao.getDepCompInfoOfAllTime())
                }
            }
        }

        get("/employee/{id}"){
            call.parameters["id"].let {
                it?.toInt()?.let {id->
                    dao.employee(id)?.let { employee ->
                        call.respond(
                            employee.toJson()
                        )
                    }
                }
            }
        }

        get("/salary"){
            dao.salary()
            call.respond("Зарплата успешно выплачена")
        }

        get("/departments"){
            call.respond(dao.allDepartments().map {
                it.toJson()
            })
        }

        post("/penalties/new"){
            call.receiveParameters().let {taskParameters->
                dao.addNewPenalty(
                    name = taskParameters["name"] ?: "",
                    description = taskParameters["desc"] ?: "",
                    amount = taskParameters["amount"]?.toDouble() ?: 0.0,
                )
            }
        }

        get("/penaltings/all/{id}"){
            val penaltings =  dao.allPenaltings().map {
                it.toJson()
            }
            val penalties = dao.allPenalties()
            val result = penaltings.associateBy {penalting->
                penalties.first {
                    it.id == penalting.penaltyId && dao.employee(penalting.employeeId)?.departmentId == (call.parameters["id"]?.toInt()
                        ?: 0)
                }.name + "|" + dao.employee(penalting.employeeId)!!.surname  + " " + dao.employee(penalting.employeeId)!!.name  + " " + dao.employee(penalting.employeeId)!!.fathername
            }
            call.respond(
                result
            )
        }
        get("/penaltings/personal/{id}"){
            val penaltings =  dao.allPenaltings().map {
               it.toJson()
            }
            val penalties = dao.allPenalties()
            penaltings.associateBy {penalting->
                penalties.first {
                    it.id == penalting.penaltyId && penalting.employeeId == call.parameters["id"]!!.toInt()
                }.name
            }.let {
                call.respond(
                    it
                )
            }

        }

        get("/employee/all/{id}"){
            call.respond(dao.allEmployees().filter {
                it.departmentId == (call.parameters["id"]?.toInt() ?: 0)
            }.map {
                it.toJson()
            })
        }

        post("/penalting"){
            call.receiveParameters().let {taskParameters->
                val penalty = dao.penalty(taskParameters["penaltyId"]!!.toInt())
               if (penalty != null){
                   dao.addNewPenalting(
                       employeeId = taskParameters["employeeId"]!!.toInt(),
                       penaltyId = penalty.id,
                       penaltyDate = LocalDateTime.now(),
                   )
                   dao.changeMoney(
                       taskParameters["employeeId"]!!.toInt(),
                       -(penalty.amount)
                   )
               }
            }
        }
    }
}
