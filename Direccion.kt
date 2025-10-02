data class Direccion(
    val calle: String,
    val numero: Int,
    val ciudad: String,
    val region: String
) {
    //Sobrescribimos la función toString para que se muestre de forma legible.
    override fun toString(): String {
        return "$calle $numero, $ciudad, $region"
    }
}