package com.mycompany.recursos.humanos;

import com.mycompany.recursos.humanos.CalculadorFinanciero;

public class ProcesadorNomina {

    // Porcentajes de retención de El Salvador (AFP: 7.25%, ISSS: 3%)
    private static final double DESCUENTO_AFP = 0.0725;
    private static final double DESCUENTO_ISSS = 0.03;

    public static double obtenerSalarioNeto(double horasTrabajadas, double pagoPorHora) {
        // 1. Calculamos el sueldo base (Salario Bruto)
        double salarioBruto = horasTrabajadas * pagoPorHora;
        
        // 2. Calculamos cuánto se le va a quitar por ley
        double retencionAFP = salarioBruto * DESCUENTO_AFP;
        double retencionISSS = salarioBruto * DESCUENTO_ISSS;
        
        // 3. Restamos los descuentos al salario bruto
        double salarioNeto = salarioBruto - retencionAFP - retencionISSS;
        
        // 4. Conectamos con el Paso 1: Redondeamos el resultado final con el método bancario
        return CalculadorFinanciero.redondear(salarioNeto);
    }

    // Este método "main" nos servirá para probar que el cálculo descuenta correctamente
    public static void main(String[] args) {
        // Simulamos un empleado que trabajó 40 horas en la semana a $5.00 la hora
        double horasTrabajadas = 40.0;
        double pagoPorHora = 5.0;
        
        double salarioFinal = obtenerSalarioNeto(horasTrabajadas, pagoPorHora);
        
        System.out.println("====== PRUEBA DE CÁLCULO DE NÓMINA ======");
        System.out.println("Salario Bruto (40h x $5): $" + (horasTrabajadas * pagoPorHora));
        System.out.println("Salario Neto (Ya con descuentos y redondeo): $" + salarioFinal);
    }
}