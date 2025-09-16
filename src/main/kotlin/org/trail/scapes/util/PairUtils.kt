package org.trail.scapes.util

data class PairOrdered(val lowId: Long, val highId: Long)

fun orderPair(a: Long, b: Long): PairOrdered =
    if (a < b) PairOrdered(a, b) else PairOrdered(b, a)