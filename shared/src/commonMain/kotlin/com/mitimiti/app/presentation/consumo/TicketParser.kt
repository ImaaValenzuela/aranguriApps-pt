package com.mitimiti.app.presentation.consumo

import kotlinx.serialization.Serializable

@Serializable
data class ParsedExpenseItem(
    val id: String = getRandomId(),
    val name: String,
    val cost: Double,
    val selected: Boolean = true,
) {
    companion object {
        private fun getRandomId(): String = (1..1000000).random().toString()
    }
}

/**
 * Parser especializado en tickets argentinos.
 *
 * Formato común en Argentina:
 *   1 MONTADITO DE VACIO         $165,00 A
 *   2 x CERVEZA QUILMES 473ML    330,00
 *   PIZZA MUZARELA               $ 1.250,00
 *   ENTRADA                        800,00 B
 *
 * Características:
 * - Precio al final, precedido por $ opcional
 * - Separador decimal: coma (,) — separador de miles: punto (.)
 * - Alicuota de IVA al final: A, B, C, E, I (una sola letra)
 * - Cantidad al inicio: "1 ", "2 ", "1x", "2 x", etc.
 * - Palabras de encabezado/pie a ignorar
 */
object TicketParser {
    // Palabras que indican que la línea NO es un producto
    private val IGNORED_KEYWORDS =
        setOf(
            "total", "subtotal", "iva", "tax", "cuit", "fecha", "hora", "caja", "cajero",
            "efectivo", "tarjeta", "visa", "master", "debito", "credito", "cambio", "pago",
            "duplicado", "original", "control", "no fiscal", "factura", "ticket", "descuento",
            "desc.", "ahorro", "neto", "bruto", "vuelto", "su vuelto", "efctivo", "efec",
            "gracias", "visita", "pagado", "saldo", "cbu", "alias", "transferencia", "mercado",
            "mp", "consumidor", "final", "responsable", "monotributo", "exento", "c.u.i.t",
            "tel", "telefono", "código", "sucursal", "dirección", "correo", "email", "web",
            "mesa", "mozo", "comanda", "propina", "servicios", "cubiertos", "servicio",
            "items", "cantidad", "precio", "importe", "adeuda", "debe", "pagó",
        )

    // Regex principal: captura "nombre ... [$ ][precio][,][2decimales] [letra?]"
    // Soporta: $1.250,00 | 1250,00 | 1250.00 | $ 165,00 | 165 (sin centavos)
    private val PRICE_AT_END_REGEX =
        Regex(
            """^(.*?)\s+\$?\s*([\d.,]+)\s*([ABCEI])?\s*$""",
            RegexOption.IGNORE_CASE,
        )

    // Regex alternativa: precio con $ explícito en cualquier posición al final
    private val PRICE_EXPLICIT_REGEX =
        Regex(
            """^(.*?)\s+\$\s*([\d.,]+)\s*([ABCEI])?\s*$""",
            RegexOption.IGNORE_CASE,
        )

    fun parse(text: String): List<ParsedExpenseItem> {
        val lines = text.split("\n", "\r")
        val items = mutableListOf<ParsedExpenseItem>()
        val seenNames = mutableSetOf<String>()

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.length < 4) continue

            val lowerLine = line.lowercase()
            if (IGNORED_KEYWORDS.any { lowerLine.contains(it) }) continue

            val result = parseLine(line)
            if (result != null) {
                // Deduplicar por nombre normalizado
                val key = result.name.uppercase().replace(Regex("\\s+"), " ")
                if (!seenNames.contains(key)) {
                    seenNames.add(key)
                    items.add(result)
                }
            }
        }
        return items
    }

    private fun parseLine(line: String): ParsedExpenseItem? {
        // Intentar con regex principal
        val match = PRICE_AT_END_REGEX.find(line) ?: return null

        val rawName = match.groupValues[1].trim()
        val rawPrice = match.groupValues[2].trim()

        val cost = parseArgentinePrice(rawPrice) ?: return null
        if (cost <= 0.0) return null

        // El precio debe ser > 1 y < 999999 (razonable para ticket)
        if (cost < 1.0 || cost > 999_999.0) return null

        // Limpiar nombre: quitar cantidad inicial (1, 2x, 1 x, etc.)
        var name =
            rawName
                .replace(Regex("^\\d+\\s*[xX*]?\\s+"), "")
                .replace(Regex("^\\d+\\s*$"), "")
                .trim()

        // El nombre debe tener al menos 2 chars y no ser solo números/símbolos
        if (name.length < 2) return null
        if (name.all { it.isDigit() || it == '.' || it == ',' || it == ' ' || it == '$' || it == '-' }) return null

        // Limpiar símbolo de $ que pueda haber quedado en el nombre
        name = name.removePrefix("$").trim()

        return ParsedExpenseItem(name = name.take(60), cost = cost)
    }

    /**
     * Parsea precios en formato argentino:
     * - 1.250,00  → 1250.0
     * - 1250,00   → 1250.0
     * - 165,00    → 165.0
     * - 165       → 165.0
     * - 1.250     → 1250.0  (si no hay centavos)
     * - 1250.00   → 1250.0  (OCR usa punto como decimal)
     */
    private fun parseArgentinePrice(raw: String): Double? {
        val s = raw.trim()
        if (s.isEmpty()) return null

        return when {
            // Formato: 1.250,00 o 1.250,5 → punto=miles, coma=decimal
            s.contains(".") && s.contains(",") -> {
                s.replace(".", "").replace(",", ".").toDoubleOrNull()
            }
            // Solo coma: 250,00 o 1250,00
            s.contains(",") -> {
                val idx = s.lastIndexOf(',')
                val afterComma = s.length - 1 - idx
                if (afterComma <= 2) {
                    s.replace(",", ".").toDoubleOrNull()
                } else {
                    // Coma como separador de miles: 1,250 → 1250
                    s.replace(",", "").toDoubleOrNull()
                }
            }
            // Solo punto: puede ser decimal (OCR) o separador de miles
            s.contains(".") -> {
                val idx = s.lastIndexOf('.')
                val afterDot = s.length - 1 - idx
                if (afterDot <= 2) {
                    // Punto decimal: 165.00
                    s.toDoubleOrNull()
                } else {
                    // Punto como miles: 1.250 → 1250
                    s.replace(".", "").toDoubleOrNull()
                }
            }
            // Sin separadores: entero puro
            else -> s.toDoubleOrNull()
        }
    }
}
