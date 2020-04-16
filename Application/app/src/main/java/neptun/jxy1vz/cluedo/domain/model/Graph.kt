package neptun.jxy1vz.cluedo.domain.model

import android.os.Build
import androidx.annotation.RequiresApi

class Graph<T> {
    val adjacencyMap: HashMap<T, HashSet<T>> = HashMap()

    @RequiresApi(Build.VERSION_CODES.N)
    fun addEdge(sourceVertex: T, destinationVertex: T) {
        // Add edge to source vertex / node.
        adjacencyMap
            .computeIfAbsent(sourceVertex) { HashSet() }
            .add(destinationVertex)
        // Add edge to destination vertex / node.
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