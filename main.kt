import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    cargarDatosDePrueba()
    while (true) {
        mostrarMenu()
        when (readlnOrNull()) {
            "1" -> listarEmpleados()
            "2" -> agregarEmpleado()
            "3" -> generarLiquidacion()
            "4" -> listarLiquidaciones()
            "5" -> filtrarYOrdenarEmpleadosPorAfp()
            "6" -> eliminarEmpleado()
            "7" -> mostrarTotalDescuentosNomina()
            "8" -> {
                println("Saliendo del sistema...")
                return
            }
            else -> println("Opción no válida. Inténtalo de nuevo.")
        }
        ConsoleUtils.pausa()
    }
}

fun mostrarMenu() {
    println("\n===== MENÚ PRINCIPAL =====")
    println("1. Listar empleados")
    println("2. Agregar empleado")
    println("3. Generar liquidación por RUT")
    println("4. Listar liquidaciones")
    println("5. Filtrar empleados por AFP y ordenar")
    println("6. Eliminar empleado")
    println("7. Mostrar total de descuentos de la nómina")
    println("8. Salir")
    print("Elige una opción: ")
}

fun listarEmpleados() {
    println("\n--- Lista de Empleados ---")
    if (Repositorio.empleados.isEmpty()) {
        println("No hay empleados registrados.")
    } else {
        Repositorio.empleados.forEach { println(it) }
    }
}

fun agregarEmpleado() {
    println("\n--- Agregar Nuevo Empleado ---")
    val rut = ConsoleUtils.leerLineaNoVacia("RUT:")
    //Verificamos si el empleado ya existe
    if (Repositorio.empleados.any { it.rut == rut }) {
        println("Error: Ya existe un empleado con ese RUT.")
        return
    }
    val nombre = ConsoleUtils.leerLineaNoVacia("Nombre:")
    val sueldoBase = ConsoleUtils.leerDouble("Sueldo Base:")

    println("Selecciona una AFP:")
    Repositorio.afps.forEachIndexed { index, afp ->
        println("${index + 1}. ${afp.nombre}")
    }
    var afpIndex: Int
    do {
        afpIndex = ConsoleUtils.leerInt("Número de la AFP:") - 1
    } while (afpIndex !in Repositorio.afps.indices)
    val afpSeleccionada = Repositorio.afps[afpIndex]

    println("Dirección del empleado:")
    val calle = ConsoleUtils.leerLineaNoVacia("Calle:")
    val numero = ConsoleUtils.leerInt("Número:")
    val ciudad = ConsoleUtils.leerLineaNoVacia("Ciudad:")
    val region = ConsoleUtils.leerLineaNoVacia("Región:")
    val direccion = Direccion(calle, numero, ciudad, region)

    val bonosImponibles = ConsoleUtils.leerDouble("Bonos Imponibles (0 si no tiene):")
    val bonosNoImponibles = ConsoleUtils.leerDouble("Bonos No Imponibles (0 si no tiene):")

    val nuevoEmpleado = Empleado(rut, nombre, sueldoBase, afpSeleccionada, direccion, bonosImponibles, bonosNoImponibles)
    Repositorio.empleados.add(nuevoEmpleado)
    println("¡Empleado agregado con éxito!")
}

fun generarLiquidacion() {
    println("\n--- Generar Liquidación de Sueldo ---")
    val rut = ConsoleUtils.leerLineaNoVacia("RUT del empleado:")

    //Usamos 'find' para buscar al empleado
    val empleado = Repositorio.empleados.find { it.rut == rut }

    if (empleado == null) {
        println("Error: No se encontró un empleado con ese RUT.")
        return
    }

    val periodo = ConsoleUtils.leerLineaNoVacia("Período (formato YYYY-MM):")
    val liquidacion = LiquidacionSueldo.calcular(empleado, periodo)
    Repositorio.liquidaciones.add(liquidacion)

    println("Liquidación generada con éxito.")
    println(liquidacion.resumen())
}

fun listarLiquidaciones() {
    println("\n--- Lista de Liquidaciones Generadas ---")
    if (Repositorio.liquidaciones.isEmpty()) {
        println("No hay liquidaciones generadas.")
    } else {
        Repositorio.liquidaciones.forEach { println(it.resumen()) }
    }
}

fun filtrarYOrdenarEmpleadosPorAfp() {
    println("\n--- Filtrar Empleados por AFP ---")
    println("Selecciona una AFP para filtrar:")
    Repositorio.afps.forEachIndexed { index, afp ->
        println("${index + 1}. ${afp.nombre}")
    }
    var afpIndex: Int
    do {
        afpIndex = ConsoleUtils.leerInt("Número de la AFP:") - 1
    } while (afpIndex !in Repositorio.afps.indices)
    val afpSeleccionada = Repositorio.afps[afpIndex]

    //Usamos 'filter' para obtener solo los empleados de la AFP seleccionada
    val empleadosFiltrados = Repositorio.empleados.filter { it.afp.nombre == afpSeleccionada.nombre }

    if (empleadosFiltrados.isEmpty()) {
        println("No se encontraron empleados en la AFP ${afpSeleccionada.nombre}.")
        return
    }

    //Calculamos la última liquidación para cada uno para poder ordenar por sueldo líquido
    //Usamos map para crear pares de Empleado y su Sueldo Líquido
    val empleadosConSueldoLiquido = empleadosFiltrados.map { emp ->
        val ultimaLiquidacion = Repositorio.liquidaciones
            .filter { it.empleado.rut == emp.rut }
            .lastOrNull() //Tomamos la más reciente si hay varias
        //Si no tiene liquidación, usamos el sueldo base como referencia para el orden
        val sueldoRef = ultimaLiquidacion?.sueldoLiquido ?: emp.sueldoBase
        emp to sueldoRef //Creamos un Par (Empleado, Double)
    }

    //Usamos 'sortedByDescending' para ordenar de mayor a menor sueldo líquido
    val empleadosOrdenados = empleadosConSueldoLiquido.sortedByDescending { it.second }

    println("\n--- Empleados de ${afpSeleccionada.nombre} (ordenados por mayor sueldo líquido) ---")
    empleadosOrdenados.forEach { (empleado, sueldo) ->
        println("${empleado} - Sueldo Líquido de referencia: $${sueldo}")
    }
}


fun eliminarEmpleado() {
    println("\n--- Eliminar Empleado ---")
    val rut = ConsoleUtils.leerLineaNoVacia("RUT del empleado a eliminar:")

    val empleado = Repositorio.empleados.find { it.rut == rut }
    if (empleado == null) {
        println("Error: No se encontró un empleado con ese RUT.")
        return
    }

    Repositorio.empleados.remove(empleado)
    //eliminar también sus liquidaciones asociadas
    Repositorio.liquidaciones.removeAll { it.empleado.rut == rut }
    println("Empleado y sus liquidaciones eliminados con éxito.")
}

fun mostrarTotalDescuentosNomina() {
    if (Repositorio.liquidaciones.isEmpty()) {
        println("No hay liquidaciones generadas para calcular el total.")
        return
    }
    //Sumamos el 'totalDescuentos' de cada liquidación generada
    val total = Repositorio.liquidaciones.sumOf { it.totalDescuentos }
    println("El total de descuentos de todas las liquidaciones generadas es: $${total}")
}

fun cargarDatosDePrueba() {
    val afpModelo = Repositorio.afps.find { it.nombre == "Modelo" }!!
    val afpHabitat = Repositorio.afps.find { it.nombre == "Habitat" }!!

    val emp1 = Empleado(
        rut = "21962832-3", nombre = "Felipe Jara", sueldoBase = 800000.0, afp = afpModelo,
        direccion = Direccion("13 norte", 3675, "Talca", "Maule")
    )
    val emp2 = Empleado(
        rut = "10101010-K", nombre = "Lionel Messi", sueldoBase = 2500000.0, afp = afpHabitat,
        direccion = Direccion("Mejor del mundo", 10, "Talca", "Maule"),
        bonosImponibles = 500000.0
    )
    val emp3 = Empleado(
        rut = "22034846-6", nombre = "Nicolas Galarce", sueldoBase = 5000000.0, afp = afpModelo,
        direccion = Direccion("2 Norte", 1, "Talca", "Maule"),
        bonosNoImponibles = 1000000.0
    )

    Repositorio.empleados.addAll(listOf(emp1, emp2, emp3))

    //Generar liquidaciones de prueba
    val periodoActual = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM"))
    Repositorio.liquidaciones.add(LiquidacionSueldo.calcular(emp1, periodoActual))
    Repositorio.liquidaciones.add(LiquidacionSueldo.calcular(emp2, periodoActual))
    Repositorio.liquidaciones.add(LiquidacionSueldo.calcular(emp3, periodoActual))
}