package com.lebaillyapp.uiwavedeformation.utils

import kotlin.math.pow

/**
 * Fonctions mathématiques utilitaires pour les ondes.
 */

/**
 * Extension pour calculer la puissance avec Float.
 */
fun Float.pow(exp: Float): Float = this.toDouble().pow(exp.toDouble()).toFloat()