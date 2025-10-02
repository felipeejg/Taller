//Almacena las listas de AFPs, empleados y liquidaciones.
object Repositorio {
    val afps: List<AFP> = listOf(
        AFP("Capital", 0.1144),
        AFP("Cuprum", 0.1144),
        AFP("Habitat", 0.1127),
        AFP("Modelo", 0.058),
        AFP("Planvital", 0.1116),
        AFP("Provida", 0.1145),
        AFP("Uno", 0.069)
    )
    val empleados: MutableList<Empleado> = mutableListOf()
    val liquidaciones: MutableList<LiquidacionSueldo> = mutableListOf()

}
