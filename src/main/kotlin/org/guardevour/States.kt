package org.guardevour


enum class Titles(val text: String) {
    Employee("Работник"),
    Manager("Руководитель"),
    Director("Директор"),
    Admin("Администратор")
}
enum class Statuses(val text: String){
    InCompleting("Выполняется"),
    NotInProgress("Не назначена"),
    Completed("Выполнена"),
    Aborted("Брошена"),
    InAudition("В обработке"),
}