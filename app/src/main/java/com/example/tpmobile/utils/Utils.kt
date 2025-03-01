package com.example.tpmobile.utils

// Fonction pour formater les nombres avec 2 décimales
fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)