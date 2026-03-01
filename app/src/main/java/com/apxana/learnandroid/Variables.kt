package com.apxana.learnandroid

//VARIABLES
fun variables () {
    //INT
    val name:Int = 30

    //LONG
    val numeroGrande:Long = 7297389193089289

    getMonth(8)

    getTrimester(13)

    getRange(4)

    getCantNotification(220)

    getPriceTicket()

}

fun getPriceTicket(){
    val child:Int = 5
    val adult:Int = 28
    val senior:Int = 87
    val ageNotValid:Int = 101

    println("The movie ticket price for a person aged $child is $${ticketPrice(child)} ")
    println("The movie ticket price for a person aged $adult is $${ticketPrice(adult)} ")
    println("The movie ticket price for a person aged $senior is $${ticketPrice(senior)} ")
    println("The movie ticket price for a person aged $ageNotValid is $${ticketPrice(ageNotValid)} ")

}

fun ticketPrice(age: Int):Int{
    return when(age){
        in 0..12 ->  15
        in 13..60 ->  30
        in 61..100 ->  20
        else ->  -1
    }
}

fun getCantNotification(cant:Int){
    when(cant){
        in 1..100 -> println("You have $cant notification")
        !in 1..100 -> println("Your phone is blowing up! you have 99+ notification")
    }
}

fun getRange(value:Int){
    when(value){
        in 1..3 -> println("Estamos en el rango valido $value")
        else -> println("$value Rango no valido")
    }
}

fun getMonth(month:Int){
    when(month){
        1 -> println("Enero")
        2 -> println("Febrero")
        3 -> println("Marzo")
        4 -> println("Abril")
        5 -> println("Junio")
        else -> println("No es un mes valido")
    }
}

fun getTrimester(month: Int){
    when(month){
        1,2,3  -> println("Primer trimestre")
        4,5,6 -> println("Segundo trimestre")
        7,8,9  -> println("Tercer trimestre")
        10,11,12  -> println("Cuarto trimestre")
        else -> println("Trimestre no valido")
    }
}