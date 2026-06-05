-- ============================================
-- FASE 4: DATOS DE PRUEBA
-- ============================================

USE recursos_humanos;

-- ============================================
-- INSERTAR DATOS EN: PARAMETROS_LEY
-- ============================================
INSERT INTO parametros_ley (nombre_retencion, porcentaje_descuento, descripcion, estado) VALUES
('ISSS', 0.0725, 'Instituto Salvadoreño del Seguro Social', TRUE),
('AFP', 0.0325, 'Administradora de Fondos de Pensiones', TRUE),
('Renta', 0.0875, 'Impuesto sobre la Renta', TRUE),
('Descuento_Administrativo', 0.0050, 'Descuento administrativo general', TRUE);

-- ============================================
-- INSERTAR DATOS EN: EMPLEADOS
-- ============================================
INSERT INTO empleados (documento_dui, nombre_completo, salario_base, estado) VALUES
('12345678-9', 'Juan Carlos López García', 1500.00, TRUE),
('87654321-0', 'María Elena Rodríguez Flores', 2000.00, TRUE),
('11223344-5', 'Carlos Alberto Martínez Pérez', 1800.00, TRUE),
('55667788-9', 'Ana Patricia González Ruiz', 1600.00, TRUE),
('99887766-1', 'Roberto Francisco Hernández López', 2200.00, TRUE),
('44556677-2', 'Laura Gabriela Sánchez Morales', 1700.00, FALSE);

-- ============================================
-- INSERTAR DATOS EN: MARCACIONES
-- ============================================
INSERT INTO marcaciones (id_empleado, fecha_hora_entrada, fecha_hora_salida) VALUES
(1, '2026-06-05 08:00:00', '2026-06-05 17:00:00'),
(1, '2026-06-04 08:15:00', '2026-06-04 17:30:00'),
(2, '2026-06-05 08:30:00', '2026-06-05 17:45:00'),
(2, '2026-06-04 08:00:00', '2026-06-04 17:00:00'),
(3, '2026-06-05 07:45:00', '2026-06-05 16:45:00'),
(4, '2026-06-05 08:45:00', '2026-06-05 18:00:00'),
(5, '2026-06-05 08:00:00', '2026-06-05 17:00:00');

-- ============================================
-- INSERTAR DATOS EN: PAGOS_NOMINA
-- ============================================
INSERT INTO pagos_nomina (id_empleado, fecha_pago, total_ingresos, total_deducciones, salario_neto, descripcion_nomina) VALUES
(1, '2026-05-30', 1500.00, 168.75, 1331.25, 'Pago quincena 1-15 mayo 2026'),
(2, '2026-05-30', 2000.00, 225.00, 1775.00, 'Pago quincena 1-15 mayo 2026'),
(3, '2026-05-30', 1800.00, 202.50, 1597.50, 'Pago quincena 1-15 mayo 2026'),
(4, '2026-05-30', 1600.00, 180.00, 1420.00, 'Pago quincena 1-15 mayo 2026'),
(5, '2026-05-30', 2200.00, 247.50, 1952.50, 'Pago quincena 1-15 mayo 2026'),
(1, '2026-06-05', 1500.00, 168.75, 1331.25, 'Pago quincena 16-30 mayo 2026');

-- ============================================
-- CONFIRMACIÓN Y VERIFICACIÓN
-- ============================================
SELECT '✓ Datos de prueba insertados exitosamente' AS estado;
SELECT COUNT(*) as total_empleados FROM empleados;
SELECT COUNT(*) as total_marcaciones FROM marcaciones;
SELECT COUNT(*) as parametros FROM parametros_ley;
SELECT COUNT(*) as pagos FROM pagos_nomina;
