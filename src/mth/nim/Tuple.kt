package mth.nim

class Tuple(vararg n: Int) {

    companion object {
        @JvmStatic
        fun parse(tuple: String): Tuple {
            val numbers = mutableListOf<Int>()

            "([+-]?\\d+)".toPattern().matcher(tuple).results().forEach { n ->
                numbers.add(n.group().toInt())
            }

            return Tuple(*numbers.toIntArray())
        }
    }

    val elements: IntArray = IntArray(n.size)
    val size: Int
        get() = elements.size

    init {
        n.forEachIndexed { i, number -> elements[i] = number }
    }

    // override fun iterator(): Iterator<Int> {
    //     return elements.iterator()
    // }

    override fun equals(other: Any?): Boolean {
        if (other !is Tuple) return true

        if (other.size != this.size) return false

        this.elements.forEachIndexed { i, value -> if (value != other.elements[i]) return false }
        return true
    }

    override fun hashCode(): Int {
        val b = StringBuilder(elements.size)

        elements.forEach { b.append(it) }
        return b.toString().hashCode()
    }

    override fun toString(): String {
        if (elements.isEmpty()) return ""

        val b = StringBuilder()
        b.append("(").append(elements[0])

        for (i in 1 until elements.size)
            b.append(" ").append(elements[i])

        b.append(")")

        return b.toString()
    }

    operator fun get(i: Int): Int = elements[i]

    operator fun set(i: Int, value: Int) {
        elements[i] = value
    }
}
