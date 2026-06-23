/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.recursos.humanos;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Motor de cálculos financieros.
 * Utiliza BigDecimal para evitar pérdida de centavos en la nómina masiva.
 */
public class CalculadorFinanciero {

    public static BigDecimal redondear(BigDecimal monto) {
        // Aplica exactamente 2 decimales y el redondeo bancario exigido
        return monto.setScale(2, RoundingMode.HALF_EVEN);
    }
}