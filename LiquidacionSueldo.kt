import java.time.LocalDate
import java.time.format.DateTimeFormatter

//Representa la liquidación de sueldo de un empleado para un período específico.
class LiquidacionSueldo(
    val empleado: Empleado,
    val periodo: String, // Formato "YYYY-MM"
    val sueldoImponible: Double,
    val montoDescAFP: Double,
    val montoDescSalud: Double,
    val montoDescCesantia: Double,
    val bonosNoImponibles: Double,
) {
    val totalDescuentos: Double = montoDescAFP + montoDescSalud + montoDescCesantia
    val sueldoLiquido: Double = sueldoImponible - totalDescuentos + bonosNoImponibles

    //Genera un resumen legible de la liquidación.

    fun resumen(): String {
        return """
        ----------------------------------------------------
        Liquidación de Sueldo: ${empleado.nombre} ($periodo)
        ----------------------------------------------------
        Sueldo Base:         $${empleado.sueldoBase}
        Bonos Imponibles:    $${empleado.bonosImponibles}
        Sueldo Imponible:    $${this.sueldoImponible}
        
        Descuentos:
          - AFP (${empleado.afp.nombre}):    $${this.montoDescAFP}
          - Salud (7%):        $${this.montoDescSalud}
          - Seg. Cesantía (0.6%): $${this.montoDescCesantia}
        Total Descuentos:      $${this.totalDescuentos}
        
        Bonos No Imponibles: $${this.bonosNoImponibles}
        ----------------------------------------------------
        Sueldo Líquido:      $${this.sueldoLiquido}
        ----------------------------------------------------
        """.trimIndent()
    }

    //'companion object' permite llamar a la función 'calcular' sin necesidad de crear una instancia de LiquidacionSueldo.
    companion object {
        //Calcula y crea una nueva instancia de LiquidacionSueldo.

        fun calcular(empleado: Empleado, periodo: String): LiquidacionSueldo {
            val imponible = empleado.sueldoImponible()

            val descAFP = imponible * empleado.afp.tasa
            val descSalud = imponible * 0.07 // 7%
            val descCesantia = imponible * 0.006 // 0.6%

            return LiquidacionSueldo(
                empleado = empleado,
                periodo = periodo,
                sueldoImponible = imponible,
                montoDescAFP = descAFP,
                montoDescSalud = descSalud,
                montoDescCesantia = descCesantia,
                bonosNoImponibles = empleado.bonosNoImponibles
            )
        }
    }

}
