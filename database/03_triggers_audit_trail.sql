-- ============================================
-- FASE 3: TRIGGERS PARA AUDIT TRAIL
-- ============================================

USE recursos_humanos;

-- ============================================
-- TRIGGER 1: AUDIT INSERT EN EMPLEADOS
-- ============================================
DELIMITER //

CREATE TRIGGER trg_audit_empleados_insert
AFTER INSERT ON empleados
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_nuevo, fecha_operacion)
    VALUES 
    ('empleados', 'INSERT', NEW.id_empleado, USER(), 
     JSON_OBJECT(
        'id_empleado', NEW.id_empleado,
        'documento_dui', NEW.documento_dui,
        'nombre_completo', NEW.nombre_completo,
        'salario_base', NEW.salario_base,
        'estado', NEW.estado
     ), 
     NOW());
END //

-- ============================================
-- TRIGGER 2: AUDIT UPDATE EN EMPLEADOS
-- ============================================
CREATE TRIGGER trg_audit_empleados_update
AFTER UPDATE ON empleados
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_anterior, valor_nuevo, fecha_operacion)
    VALUES 
    ('empleados', 'UPDATE', NEW.id_empleado, USER(),
     JSON_OBJECT(
        'id_empleado', OLD.id_empleado,
        'documento_dui', OLD.documento_dui,
        'nombre_completo', OLD.nombre_completo,
        'salario_base', OLD.salario_base,
        'estado', OLD.estado
     ),
     JSON_OBJECT(
        'id_empleado', NEW.id_empleado,
        'documento_dui', NEW.documento_dui,
        'nombre_completo', NEW.nombre_completo,
        'salario_base', NEW.salario_base,
        'estado', NEW.estado
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 3: AUDIT DELETE EN EMPLEADOS
-- ============================================
CREATE TRIGGER trg_audit_empleados_delete
AFTER DELETE ON empleados
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_anterior, fecha_operacion)
    VALUES 
    ('empleados', 'DELETE', OLD.id_empleado, USER(),
     JSON_OBJECT(
        'id_empleado', OLD.id_empleado,
        'documento_dui', OLD.documento_dui,
        'nombre_completo', OLD.nombre_completo,
        'salario_base', OLD.salario_base,
        'estado', OLD.estado
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 4: AUDIT INSERT EN PAGOS_NOMINA
-- ============================================
CREATE TRIGGER trg_audit_pagos_insert
AFTER INSERT ON pagos_nomina
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_nuevo, fecha_operacion)
    VALUES 
    ('pagos_nomina', 'INSERT', NEW.id_pago, USER(),
     JSON_OBJECT(
        'id_pago', NEW.id_pago,
        'id_empleado', NEW.id_empleado,
        'fecha_pago', NEW.fecha_pago,
        'total_ingresos', NEW.total_ingresos,
        'total_deducciones', NEW.total_deducciones,
        'salario_neto', NEW.salario_neto
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 5: AUDIT UPDATE EN PAGOS_NOMINA
-- ============================================
CREATE TRIGGER trg_audit_pagos_update
AFTER UPDATE ON pagos_nomina
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_anterior, valor_nuevo, fecha_operacion)
    VALUES 
    ('pagos_nomina', 'UPDATE', NEW.id_pago, USER(),
     JSON_OBJECT(
        'id_pago', OLD.id_pago,
        'id_empleado', OLD.id_empleado,
        'fecha_pago', OLD.fecha_pago,
        'total_ingresos', OLD.total_ingresos,
        'total_deducciones', OLD.total_deducciones,
        'salario_neto', OLD.salario_neto
     ),
     JSON_OBJECT(
        'id_pago', NEW.id_pago,
        'id_empleado', NEW.id_empleado,
        'fecha_pago', NEW.fecha_pago,
        'total_ingresos', NEW.total_ingresos,
        'total_deducciones', NEW.total_deducciones,
        'salario_neto', NEW.salario_neto
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 6: AUDIT DELETE EN PAGOS_NOMINA
-- ============================================
CREATE TRIGGER trg_audit_pagos_delete
AFTER DELETE ON pagos_nomina
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_anterior, fecha_operacion)
    VALUES 
    ('pagos_nomina', 'DELETE', OLD.id_pago, USER(),
     JSON_OBJECT(
        'id_pago', OLD.id_pago,
        'id_empleado', OLD.id_empleado,
        'fecha_pago', OLD.fecha_pago,
        'total_ingresos', OLD.total_ingresos,
        'total_deducciones', OLD.total_deducciones,
        'salario_neto', OLD.salario_neto
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 7: AUDIT INSERT EN MARCACIONES
-- ============================================
CREATE TRIGGER trg_audit_marcaciones_insert
AFTER INSERT ON marcaciones
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_nuevo, fecha_operacion)
    VALUES 
    ('marcaciones', 'INSERT', NEW.id_marcacion, USER(),
     JSON_OBJECT(
        'id_marcacion', NEW.id_marcacion,
        'id_empleado', NEW.id_empleado,
        'fecha_hora_entrada', NEW.fecha_hora_entrada,
        'fecha_hora_salida', NEW.fecha_hora_salida
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 8: AUDIT UPDATE EN MARCACIONES
-- ============================================
CREATE TRIGGER trg_audit_marcaciones_update
AFTER UPDATE ON marcaciones
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_anterior, valor_nuevo, fecha_operacion)
    VALUES 
    ('marcaciones', 'UPDATE', NEW.id_marcacion, USER(),
     JSON_OBJECT(
        'id_marcacion', OLD.id_marcacion,
        'id_empleado', OLD.id_empleado,
        'fecha_hora_entrada', OLD.fecha_hora_entrada,
        'fecha_hora_salida', OLD.fecha_hora_salida
     ),
     JSON_OBJECT(
        'id_marcacion', NEW.id_marcacion,
        'id_empleado', NEW.id_empleado,
        'fecha_hora_entrada', NEW.fecha_hora_entrada,
        'fecha_hora_salida', NEW.fecha_hora_salida
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 9: AUDIT DELETE EN MARCACIONES
-- ============================================
CREATE TRIGGER trg_audit_marcaciones_delete
AFTER DELETE ON marcaciones
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_anterior, fecha_operacion)
    VALUES 
    ('marcaciones', 'DELETE', OLD.id_marcacion, USER(),
     JSON_OBJECT(
        'id_marcacion', OLD.id_marcacion,
        'id_empleado', OLD.id_empleado,
        'fecha_hora_entrada', OLD.fecha_hora_entrada,
        'fecha_hora_salida', OLD.fecha_hora_salida
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 10: AUDIT INSERT EN PARAMETROS_LEY
-- ============================================
CREATE TRIGGER trg_audit_parametros_insert
AFTER INSERT ON parametros_ley
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_nuevo, fecha_operacion)
    VALUES 
    ('parametros_ley', 'INSERT', NEW.id_parametro, USER(),
     JSON_OBJECT(
        'id_parametro', NEW.id_parametro,
        'nombre_retencion', NEW.nombre_retencion,
        'porcentaje_descuento', NEW.porcentaje_descuento
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 11: AUDIT UPDATE EN PARAMETROS_LEY
-- ============================================
CREATE TRIGGER trg_audit_parametros_update
AFTER UPDATE ON parametros_ley
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_anterior, valor_nuevo, fecha_operacion)
    VALUES 
    ('parametros_ley', 'UPDATE', NEW.id_parametro, USER(),
     JSON_OBJECT(
        'id_parametro', OLD.id_parametro,
        'nombre_retencion', OLD.nombre_retencion,
        'porcentaje_descuento', OLD.porcentaje_descuento
     ),
     JSON_OBJECT(
        'id_parametro', NEW.id_parametro,
        'nombre_retencion', NEW.nombre_retencion,
        'porcentaje_descuento', NEW.porcentaje_descuento
     ),
     NOW());
END //

-- ============================================
-- TRIGGER 12: AUDIT DELETE EN PARAMETROS_LEY
-- ============================================
CREATE TRIGGER trg_audit_parametros_delete
AFTER DELETE ON parametros_ley
FOR EACH ROW
BEGIN
    INSERT INTO audit_trail 
    (tabla_afectada, tipo_operacion, id_registro, usuario_sistema, valor_anterior, fecha_operacion)
    VALUES 
    ('parametros_ley', 'DELETE', OLD.id_parametro, USER(),
     JSON_OBJECT(
        'id_parametro', OLD.id_parametro,
        'nombre_retencion', OLD.nombre_retencion,
        'porcentaje_descuento', OLD.porcentaje_descuento
     ),
     NOW());
END //

DELIMITER ;

-- ============================================
-- CONFIRMACIÓN
-- ============================================
SELECT '✓ Triggers de Audit Trail creados exitosamente' AS estado;
SHOW TRIGGERS FROM recursos_humanos;
