package com.evalify.evalifybackend.quiz.util

object CombinationUtils {
    fun <T> combinations(list: List<T>, k: Int): List<List<T>> {
        val result = mutableListOf<List<T>>()
        fun combine(start: Int, current: MutableList<T>) {
            if (current.size == k) {
                result.add(ArrayList(current))
                return
            }
            for (i in start until list.size) {
                current.add(list[i])
                combine(i + 1, current)
                current.removeAt(current.size - 1)
            }
        }
        combine(0, mutableListOf())
        return result
    }

    fun <T> cartesianProduct(lists: List<List<List<T>>>): List<List<List<T>>> {
        return lists.fold(listOf(listOf())) { acc, list ->
            acc.flatMap { prev -> list.map { prev + listOf(it) } }
        }
    }

    fun factorial(n: Int): Long {
        return (1..n).fold(1L) { acc, i -> acc * i }
    }

    fun <T> countPermutations(items: List<T>): Long {
        val freq = items.groupingBy { it }.eachCount()
        val numerator = factorial(items.size)
        val denominator = freq.values.fold(1L) { acc, f -> acc * factorial(f) }
        return numerator / denominator
    }
}