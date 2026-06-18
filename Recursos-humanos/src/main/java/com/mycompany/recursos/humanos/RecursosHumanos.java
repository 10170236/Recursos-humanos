package com.mycompany.recursos.humanos;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;

/**
 * Clase principal de Recursos Humanos.
 * Inicializa el tema FlatLaf y lanza el Formulario de Expediente.
 */
public class RecursosHumanos {

    public static void main(String[] args) {
        // Inicializar el look and feel moderno (FlatLaf)
        FlatLightLaf.setup();
        
        // Ejecutar el formulario en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new ExpedienteForm().setVisible(true);
        });
    }
}
