package mth.nim

class Q() {

    var alpha: Double = 0.5
    val q: HashMap<Tuple, HashMap<Tuple, Double>> = HashMap()

    fun getQ(state: Tuple, action: Action): Double {
        if (q.containsKey(state)) return q[state]!!.getOrDefault(action, 0.0) else return 0.0
    }

    fun setQ(state: Tuple, action: Tuple, value: Double) {
        if (q.containsKey(state)) {
            q.getValue(state)[action] = value
            // println("$state is present. Updating action value -> ${q[state]}")
        } else {
            q[state] = HashMap()
            q.getValue(state)[action] = value
            // println("ADD ${q[state]}")
        }
    }

    fun maxReward(state: Tuple): Double {
        val actions = Nim.availableActions(state)
        var max = if (actions.isNotEmpty()) -10000.0 else 0.0

        actions.forEach { action ->
            val q = getQ(state, action)
            if (q > max) max = q
        }

        // println("$state r=$max")
        return max
    }

    fun update(state: Tuple, action: Action, newState: Tuple, reward: Double) {
        // println("updating state $state")
        val oldQ = getQ(state, action)
        val newQ = oldQ + alpha * (reward + maxReward(newState) - oldQ)
        setQ(state, action, newQ)
    }

    override fun toString(): String {
        return q.toString()
    }
}
