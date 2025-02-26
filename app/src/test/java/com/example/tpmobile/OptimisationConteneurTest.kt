package com.example.tpmobile

import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur
import org.junit.Test
import org.junit.Assert.*

class OptimisationConteneurTest {
    // Données de test réutilisables
    private val conteneurStandard = Conteneur(1, poidsMax = 100.0, volumeMax = 100.0)

    private val commandes = listOf(
        Commande(1, poids = 20.0, volume = 30.0, prix = 100.0, priorite = "NORMALE", fragile = false),
        Commande(2, poids = 30.0, volume = 20.0, prix = 150.0, priorite = "HAUTE", fragile = true),
        Commande(3, poids = 50.0, volume = 40.0, prix = 200.0, priorite = "BASSE", fragile = false),
        Commande(4, poids = 10.0, volume = 10.0, prix = 80.0, priorite = "NORMALE", fragile = false),
        Commande(5, poids = 40.0, volume = 50.0, prix = 300.0, priorite = "HAUTE", fragile = true)
    )

    @Test
    fun `test conteneur vide retourne liste vide`() {
        val conteneurVide = Conteneur(1, poidsMax = 0.0, volumeMax = 0.0)
        val resultat = optimiserConteneur(conteneurVide, commandes, mutableSetOf())
        assertTrue("Un conteneur vide devrait retourner une liste vide", resultat.isEmpty())
    }

    @Test
    fun `test commandes déjà affectées sont exclues`() {
        val commandesAffectees = mutableSetOf(5, 3)
        val commandesAffecteesCopy = mutableSetOf(5, 3)
        val resultat = optimiserConteneur(conteneurStandard, commandes, commandesAffectees)


        resultat.forEach { commande ->
            assertFalse(
                "Les commandes déjà affectées ne devraient pas être sélectionnées",
                commandesAffecteesCopy.contains(commande.numero)
            )
        }
    }

    @Test
    fun `test commandes bien ajoutées à commandesAffectees`() {
        val commandesAffectees = mutableSetOf(5, 3)
        val expetedOutpout = mutableSetOf(4, 2, 1)
        val resultat = optimiserConteneur(conteneurStandard, commandes, commandesAffectees)

        resultat.forEach { commande ->
            assertTrue(
                "Les commandes affectées doivent être ajouté à commandesAffectes",
                commandesAffectees.contains(commande.numero)
            )
        }
    }

    @Test
    fun `test respect des contraintes de poids et volume`() {
        val resultat = optimiserConteneur(conteneurStandard, commandes, mutableSetOf())

        val poidsTotal = resultat.sumOf { it.poids }
        val volumeTotal = resultat.sumOf { it.volume }

        assertTrue("Le poids total ne doit pas dépasser la capacité", poidsTotal <= conteneurStandard.poidsMax)
        assertTrue("Le volume total ne doit pas dépasser la capacité", volumeTotal <= conteneurStandard.volumeMax)
    }

    @Test
    fun `test optimisation du prix total`() {
        // Cas simple avec un seul choix optimal évident
        val petitConteneur = Conteneur(1, poidsMax = 30.0, volumeMax = 30.0)
        val petitesCommandes = listOf(
            Commande(1, poids = 20.0, volume = 20.0, prix = 100.0, priorite = "NORMALE", fragile = false),
            Commande(2, poids = 20.0, volume = 20.0, prix = 50.0, priorite = "NORMALE", fragile = false)
        )

        val resultat = optimiserConteneur(petitConteneur, petitesCommandes, mutableSetOf())

        assertEquals("Devrait choisir la commande avec le meilleur prix", 100.0,
            resultat.sumOf { it.prix }, 0.01)
    }

    @Test
    fun `test solution vide quand aucune commande ne rentre`() {
        val toutPetitConteneur = Conteneur(1, poidsMax = 5.0, volumeMax = 5.0)
        val resultat = optimiserConteneur(toutPetitConteneur, commandes, mutableSetOf())

        assertTrue("Devrait retourner une liste vide si aucune commande ne peut rentrer",
            resultat.isEmpty())
    }

    @Test
    fun `test meilleure combinaison possible`() {
        // Cas où on peut placer exactement 2 commandes pour un prix optimal
        val conteneurMoyen = Conteneur(1, poidsMax = 50.0, volumeMax = 50.0)
        val commandesSimples = listOf(
            Commande(1, poids = 20.0, volume = 20.0, prix = 100.0, priorite = "NORMALE", fragile = false),
            Commande(2, poids = 20.0, volume = 20.0, prix = 150.0, priorite = "NORMALE", fragile = false),
            Commande(3, poids = 45.0, volume = 45.0, prix = 200.0, priorite = "NORMALE", fragile = false)
        )

        val resultat = optimiserConteneur(conteneurMoyen, commandesSimples, mutableSetOf())

        assertEquals("Devrait choisir la combinaison optimale de commandes", 250.0,
            resultat.sumOf { it.prix }, 0.01)
    }

    @Test
    fun `test meilleure combinaison possible contrainte sur volume`() {
        // Cas où on peut placer exactement 2 commandes pour un prix optimal
        val conteneurMoyen = Conteneur(1, poidsMax = 50.0, volumeMax = 50.0)
        val commandesSimples = listOf(
            Commande(1, poids = 10.0, volume = 20.0, prix = 100.0, priorite = "NORMALE", fragile = false),
            Commande(2, poids = 5.0,  volume = 20.0, prix = 150.0, priorite = "NORMALE", fragile = false),
            Commande(3, poids = 10.0, volume = 45.0, prix = 200.0, priorite = "NORMALE", fragile = false)
        )

        val resultat = optimiserConteneur(conteneurMoyen, commandesSimples, mutableSetOf())

        assertEquals("Devrait choisir la combinaison optimale de commandes", 250.0,
            resultat.sumOf { it.prix }, 0.01)
    }

    @Test
    fun `test echec methode gloutonne`() {
        // Cas où la méthode gloutonne échoue
        val conteneur = Conteneur(1, poidsMax = 15.0, volumeMax = 15.0)
        val commandes = listOf(
            Commande(1, poids = 9.0,  volume = 9.0,  prix = 10.0, priorite = "NORMALE", fragile = false),
            Commande(2, poids = 12.0, volume = 12.0, prix = 7.0, priorite = "NORMALE", fragile = false),
            Commande(3, poids = 2.0,  volume = 2.0,  prix = 1.0, priorite = "NORMALE", fragile = false),
            Commande(4, poids = 7.0,  volume = 7.0,  prix = 3.0, priorite = "NORMALE", fragile = false),
            Commande(5, poids = 5.0,  volume = 5.0,  prix = 2.0, priorite = "NORMALE", fragile = false),
        )

        val resultat = optimiserConteneur(conteneur, commandes, mutableSetOf())

        assertEquals("Devrait choisir la combinaison optimale de commandes", 12.0,
            resultat.sumOf { it.prix }, 0.01)
    }


    @Test
    fun `test floting number acceptance`() {
        // Cas où on peut placer exactement 2 commandes pour un prix optimal
        val conteneurMoyen = Conteneur(1, poidsMax = 50.866, volumeMax = 50.636)
        val commandesSimples = listOf(
            Commande(1, poids = 09.853, volume = 19.635, prix = 100.895, priorite = "NORMALE", fragile = false),
            Commande(2, poids = 4.8620,  volume = 19.560, prix = 150.561, priorite = "NORMALE", fragile = false),
            Commande(3, poids = 9.753, volume = 44.856, prix = 200.560, priorite = "NORMALE", fragile = false)
        )

        val resultat = optimiserConteneur(conteneurMoyen, commandesSimples, mutableSetOf())

        assertEquals("Devrait choisir la combinaison optimale de commandes", 251.45600000000002,
            resultat.sumOf { it.prix }, 0.01)
    }
}

