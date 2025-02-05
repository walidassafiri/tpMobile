package com.example.tpmobile.model

data class Commande(
    val numero: Int,
    val poids: Double,
    val volume: Double,
    val prix: Double,
    val priorite: String,
    val fragile: Boolean
)
