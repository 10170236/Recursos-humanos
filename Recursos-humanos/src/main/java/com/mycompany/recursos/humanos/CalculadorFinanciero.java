/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.recursos.humanos;
import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 *
 * @author Zavaleta
 */
public class CalculadorFinanciero {

    public static double redondear(double monto) {
        // Convertimos el double a BigDecimal para máxima precisión
        BigDecimal bd = new BigDecimal(Double.toString(monto));
        // Aplicamos 2 decimales y el redondeo bancario
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
