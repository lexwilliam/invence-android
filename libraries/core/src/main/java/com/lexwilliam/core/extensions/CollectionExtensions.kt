package com.lexwilliam.core.extensions

fun <T> List<T>.addOrUpdateDuplicate(
    newValue: T,
    condition: (existingItem: T, newValue: T) -> Boolean
): List<T> {
    val mutableList = this.toMutableList()

    val existingItem = mutableList.find { condition(it, newValue) }

    if (existingItem != null) {
        val index = mutableList.indexOf(existingItem)
        mutableList[index] = newValue
    } else {
        mutableList.add(newValue)
    }

    return mutableList.toList()
}

fun <T> List<T>.updateDuplicate(item: T): List<T> {
    val mutableList = toMutableList()
    val index = indexOf(item)

    return if (index != -1) {
        // Update the existing item
        mutableList[index] = item
        // Convert back to an immutable list
        mutableList.toList()
    } else {
        // Return the original list if item not found
        this
    }
}

fun <T> List<T>.mapIf(
    condition: Boolean,
    transform: (T) -> T
): List<T> {
    val result = mutableListOf<T>()
    if (condition) {
        for (item in this) {
            result.add(transform(item))
        }
    }
    return result.toList()
}

fun <T> List<T>.replace(
    item: T,
    condition: (existingItem: T) -> Boolean
): List<T> {
    val mutableList = this.toMutableList()
    val replacedIndices = mutableList.indices.filter { condition(mutableList[it]) }
    replacedIndices.forEach { index -> mutableList[index] = item }
    return mutableList.toList()
}