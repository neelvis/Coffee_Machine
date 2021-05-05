package machine

import java.lang.Exception


class CoffeeMachine(
    private var moneyReserve: Int = 550,
    private var waterReserve: Int = 400,
    private var milkReserve: Int = 540,
    private var beansReserve: Int = 120,
    private var glassReserve: Int = 9) {

    private enum class CoffeeCup(
        val water: Int = 0,
        val milk: Int = 0,
        val beans: Int = 0,
        val cost: Int = 0){
        ESPRESSO(250, 0, 16, 4),
        LATTE(350, 75, 20, 7),
        CAPPUCCINO(200, 100, 12, 6)
    }
    private enum class State(){
        STANDBY, MAKE_COFFEE, REFILL, RETURN_MONEY, SHOW_INFO
    }
    private var currentState = State.STANDBY

    private fun getReserve(): String {
        return """
            The coffee machine has:
            $waterReserve of water
            $milkReserve of milk
            $beansReserve of coffee beans
            $glassReserve of disposable cups
            $moneyReserve of money
            
        """.trimIndent()
    }
    private fun buy(coffee: CoffeeCup): String {
        if (waterReserve < coffee.water) return "Sorry, not enough water!"
        if (milkReserve < coffee.milk) return "Sorry, not enough milk!"
        if (beansReserve < coffee.beans) return "Sorry, not enough coffee!"
        if (glassReserve < 1) return "Sorry, not enough disposable cups!"
        waterReserve -= coffee.water
        milkReserve -= coffee.milk
        beansReserve -= coffee.beans
        moneyReserve += coffee.cost
        glassReserve--
        return "I have enough resources, making you a coffee!"
    }
    private fun refill(waterToAdd: Int, milkToAdd: Int, beansToAdd: Int, glassesToAdd: Int): String {
        waterReserve += waterToAdd
        milkReserve += milkToAdd
        beansReserve += beansToAdd
        glassReserve += glassesToAdd
        return "refill successful"
    }
    private fun returnMoney(): String {
        val money = moneyReserve
        moneyReserve = 0
        return "I gave you \$$money"
    }
    fun processInput(input: String): String =
         when (currentState) {
            State.MAKE_COFFEE -> {
                when (safeStringToInt(input)) {
                    1 -> buy(CoffeeCup.ESPRESSO)
                    2 -> buy(CoffeeCup.LATTE)
                    3 -> buy(CoffeeCup.CAPPUCCINO)
                    else -> "no such coffee"
                }
            }
            State.RETURN_MONEY -> returnMoney()
            State.REFILL -> {
                val (water, milk, beans, glasses) = input.split(',').map { it.toInt() }
                refill(water, milk, beans, glasses)
            }
            State.SHOW_INFO -> getReserve()
            else -> "PROCESSING ERROR"
        }

    fun readyToCook() {
        currentState = State.MAKE_COFFEE
    }
    fun sleep() {
        currentState = State.STANDBY
    }
    fun readyToRefill() {
        currentState = State.REFILL
    }
    fun readyToReturn() {
        currentState = State.RETURN_MONEY
    }
    fun readyToShow() {
        currentState = State.SHOW_INFO
    }
}

fun safeStringToInt(s: String): Int {
    return try {
        s.toInt()
    }
    catch (e: Exception) {
        0
    }
}
fun main() {
    val nespresso = CoffeeMachine()
    do {
        print("Write action (buy, fill, take):")
        when (readLine()!!) {
            "buy" -> {
                print("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:")
                val userChoice = readLine()!!
                if (userChoice == "back") continue
                nespresso.readyToCook()
                println(nespresso.processInput(userChoice))
                nespresso.sleep()
            }
            "fill" -> {
                println("Write how many ml of water do you want to add: ")
                val water = safeStringToInt(readLine()!!)
                println("Write how many ml of milk do you want to add: ")
                val milk = safeStringToInt(readLine()!!)
                println("Write how many grams of coffee beans do you want to add: ")
                val beans = safeStringToInt(readLine()!!)
                println("Write how many disposable cups of coffee do you want to add: ")
                val glasses = safeStringToInt(readLine()!!)
                nespresso.readyToRefill()
                println(nespresso.processInput("$water,$milk,$beans,$glasses"))
                nespresso.sleep()
            }
            "take" -> {
                nespresso.readyToReturn()
                println(nespresso.processInput("Money back"))
                nespresso.sleep()
            }
            "remaining" -> {
                nespresso.readyToShow()
                println(nespresso.processInput("Show reserve"))
                nespresso.sleep()
            }
            "exit" -> break
        }
    } while (true)
}

/*
enum class MachineState(private val validPattern: String = "") {
    MAIN_MENU("buy|fill|take|remaining|exit"),
    CHOOSE_COFFEE("[1-3]|back"),
    FILL_MACHINE("\\d+"),
    EXIT();

    fun canHandleAction(action: String) = action.matches(Regex(validPattern))
}
 */