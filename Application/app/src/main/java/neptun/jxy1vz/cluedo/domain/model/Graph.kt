package neptun.jxy1vz.cluedo.domain.model

import android.os.Build
import androidx.annotation.RequiresApi

class Graph<T> {
    val adjacencyMap: HashMap<T, HashSet<T>> = HashMap()

    @RequiresApi(Build.VERSION_CODES.N)
    fun addEdge(sourceVertex: T, destinationVertex: T) {
        adjacencyMap
            .computeIfAbsent(sourceVertex) { HashSet() }
            .add(destinationVertex)
        adjacencyMap
            .computeIfAbsent(destinationVertex) { HashSet() }
            .add(sourceVertex)
    }

    override fun toString(): String = StringBuffer().apply {
        for (key in adjacencyMap.keys) {
            append("$key -> ")
            append(adjacencyMap[key]?.joinToString(", ", "[", "]\n"))
        }
    }.toString()
}