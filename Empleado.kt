class Empleado(
    val rut: String,
    val nombre: String,
    val sueldoBase: Double,
    val afp: AFP,
    val direccion: Direccion,
    val bonosImponibles: Double = 0.0,
    val bonosNoImponibles: Double = 0.0
) {
    fun sueldoImponible(): Double {
        return sueldoBase + bonosImponibles
    }

    override fun toString(): String {
        return "RUT: $rut, Nombre: $nombre, Sueldo Base: $${sueldoBase}, AFP: ${afp.nombre}"
    }
}