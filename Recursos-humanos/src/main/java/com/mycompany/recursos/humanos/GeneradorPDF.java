package com.mycompany.recursos.humanos;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.File;

public class GeneradorPDF {
    public String crearBoleta(String empleado, double salario) {
        String ruta = "Boleta_" + empleado.replace(" ", "_") + ".pdf";
        try {
            PdfWriter writer = new PdfWriter(ruta);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            // 1. Instanciar paleta de colores personalizada
            Color color1 = new DeviceRgb(0x14, 0x0F, 0x07); // Texto base (Casi negro)
            Color color2 = new DeviceRgb(0x1A, 0x1D, 0x36); // Títulos principales (Azul noche)
            Color color3 = new DeviceRgb(0x3E, 0x49, 0x7E); // Subtítulos
            Color color4 = new DeviceRgb(0x6A, 0x76, 0xCC); // Líneas y acentos (Lavanda dinámico)
            Color color5 = new DeviceRgb(0x9F, 0xAB, 0xFA); // Fondo de tablas (Pastel suave)

            // 2. Encabezado principal estilizado
            Paragraph titulo = new Paragraph("BOLETA DE PAGO")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(color2)
                    .setMarginBottom(2);
            doc.add(titulo);

            Paragraph subtitulo = new Paragraph("Sistema de Gestión de Recursos Humanos")
                    .setFontSize(10)
                    .setFontColor(color3)
                    .setMarginBottom(12);
            doc.add(subtitulo);

            // 3. Línea divisoria de diseño corporativo
            SolidLine lineaDecorativa = new SolidLine(2f);
            lineaDecorativa.setColor(color4);
            LineSeparator separador = new LineSeparator(lineaDecorativa);
            separador.setMarginBottom(20);
            doc.add(separador);

            // 4. Bloque de metadatos del colaborador
            Paragraph infoBloque = new Paragraph().setMarginBottom(25);
            infoBloque.add(new Paragraph("Colaborador: ").setBold().setFontColor(color3).setFontSize(11));
            infoBloque.add(new Paragraph(empleado + "\n").setFontColor(color1).setFontSize(12));
            infoBloque.add(new Paragraph("Fecha de Emisión: ").setBold().setFontColor(color3).setFontSize(11));
            infoBloque.add(new Paragraph("05/06/2026\n").setFontColor(color1).setFontSize(11));
            doc.add(infoBloque);

            // 5. Tabla de Liquidación Financiera Estructurada
            float[] anchosColumnas = {4f, 2f}; // Relación de aspecto 4:2 para descripción y monto
            Table tablaNómina = new Table(UnitValue.createPercentArray(anchosColumnas));
            tablaNómina.setWidth(UnitValue.createPercentValue(100));

            // Encabezados de tabla estilizados con color5
            Cell headerConcepto = new Cell().add(new Paragraph("Concepto / Descripción").setBold().setFontColor(color2))
                    .setBackgroundColor(color5)
                    .setPadding(8);
            Cell headerMonto = new Cell().add(new Paragraph("Total").setBold().setFontColor(color2))
                    .setBackgroundColor(color5)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setPadding(8);
            tablaNómina.addHeaderCell(headerConcepto);
            tablaNómina.addHeaderCell(headerMonto);

            // Fila de Salario Base
            tablaNómina.addCell(new Cell().add(new Paragraph("Salario Mensual Base").setFontColor(color1)).setPadding(8));
            tablaNómina.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", salario)).setFontColor(color1))
                    .setTextAlignment(TextAlignment.RIGHT).setPadding(8));

            // Fila de Cierre para el Total Neto
            Cell celdaTotalTexto = new Cell().add(new Paragraph("TOTAL NETO PERCIBIDO").setBold().setFontColor(color2))
                    .setBackgroundColor(color5)
                    .setPadding(10);
            Cell celdaTotalValor = new Cell().add(new Paragraph("$" + String.format("%.2f", salario)).setBold().setFontColor(color2))
                    .setBackgroundColor(color5)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setPadding(10);
            tablaNómina.addCell(celdaTotalTexto);
            tablaNómina.addCell(celdaTotalValor);

            doc.add(tablaNómina);

            // 6. Pie de página institucional discreto
            Paragraph piePagina = new Paragraph("\n\n\nEste documento sirve como comprobante oficial de acreditación de haberes.")
                    .setFontSize(8)
                    .setFontColor(color3)
                    .setTextAlignment(TextAlignment.CENTER);
            doc.add(piePagina);

            doc.close();
            System.out.println("PDF corporativo guardado en: " + new File(ruta).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ruta;
    }
}