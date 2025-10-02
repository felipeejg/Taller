import java.util.Scanner

object ConsoleUtils {
    private val scanner = Scanner(System.`in`)

    fun leerLineaNoVacia(prompt: String): String {
        var input: String
        do {
            print("$prompt ")
            input = scanner.nextLine()
        } while (input.isBlank())
        return input
    }

    fun leerDouble(prompt: String): Double {
        while (true) {
            try {
                print("$prompt ")
                return scanner.nextLine().toDouble()
            } catch (e: NumberFormatException) {
                println("Error: Debes ingresar un número válido.")
            }
        }
    }

    fun leerInt(prompt: String): Int {
        while (true) {
            try {
                print("$prompt ")
                return scanner.nextLine().toInt()
            } catch (e: NumberFormatException) {
                println("Error: Debes ingresar un número entero válido.")
            }
        }
    }

    fun pausa(mensaje: String = "Presiona Enter para continuar") {
        println(mensaje)
        scanner.nextLine()
    }
}