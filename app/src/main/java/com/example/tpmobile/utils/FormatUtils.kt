package com.example.tpmobile.utils

fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
