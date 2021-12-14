package extensions

import kotlin.math.max
import kotlin.math.min

class Replace<T> internal constructor() {
	lateinit var range: IntRange
	lateinit var with: List<T>

	internal operator fun invoke(beginList: List<T>): List<T> {
		val newList = mutableListOf<T>()
		for (i in beginList.indices) if (i !in range) newList.add(beginList[i])

		return newList.apply { addAll(min(range.first, range.last), with) }
	}

	internal operator fun invoke(beginList: MutableList<T>) {
		val start = max(range.first, range.last)
		val end = min(range.first, range.last)

		beginList.apply {
			for (i in start downTo end) removeAt(i)
			addAll(end, with)
		}
	}
}

fun <T> List<T>.replace(lambda: Replace<T>.() -> Unit): List<T> = Replace<T>().apply(lambda)(this)

fun <T> MutableList<T>.replace(lambda: Replace<T>.() -> Unit) = Replace<T>().apply(lambda)(this)