-- ============================================
-- PROYECTO: SISTEMA DE RECURSOS HUMANOS
-- ROL: ARQUITECTO DE BASE DE DATOS (Data Master)
-- FASE 1: CREACIÓN DE BASE DE DATOS
-- ============================================

-- Eliminar BD si existe (solo en desarrollo)
DROP DATABASE IF EXISTS recursos_humanos;

-- Crear la base de datos
CREATE DATABASE recursos_humanos 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE recursos_humanos;

-- ============================================
-- TABLA 1: EMPLEADOS (Catálogo Principal)
-- ============================================
CREATE TABLE empleados (
    id_empleado INT PRIMARY KEY AUTO_INCREMENT,
    documento_dui VARCHAR(10) NOT NULL UNIQUE,
    nombre_completo VARCHAR(100) NOT NULL,
    salario_base DECIMAL(10,2) NOT NULL CHECK(salario_base >= 0),
    estado BOOLEAN DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_dui (documento_dui),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 2: MARCACIONES (Control de Asistencia)
-- ============================================
CREATE TABLE marcaciones (
    id_marcacion BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_empleado INT NOT NULL,
    fecha_hora_entrada DATETIME NOT NULL,
    fecha_hora_salida DATETIME,
    
    CONSTRAINT fk_marcacion_empleado FOREIGN KEY (id_empleado) 
        REFERENCES empleados(id_empleado) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    INDEX idx_empleado_fecha (id_empleado, fecha_hora_entrada),
    INDEX idx_fecha_entrada (fecha_hora_entrada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 3: PARAMETROS_LEY (Configuración Dinámica)
-- ============================================
CREATE TABLE parametros_ley (
    id_parametro INT PRIMARY KEY AUTO_INCREMENT,
    nombre_retencion VARCHAR(50) NOT NULL UNIQUE,
    porcentaje_descuento DECIMAL(5,4) NOT NULL CHECK(porcentaje_descuento >= 0 AND porcentaje_descuento <= 1),
    descripcion VARCHAR(255),
    estado BOOLEAN DEFAULT TRUE,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_nombre (nombre_retencion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 4: PAGOS_NOMINA (Historial Financiero)
-- ============================================
CREATE TABLE pagos_nomina (
    id_pago INT PRIMARY KEY AUTO_INCREMENT,
    id_empleado INT NOT NULL,
    fecha_pago DATE NOT NULL,
    total_ingresos DECIMAL(10,2) NOT NULL CHECK(total_ingresos >= 0),
    total_deducciones DECIMAL(10,2) NOT NULL CHECK(total_deducciones >= 0),
    salario_neto DECIMAL(10,2) NOT NULL CHECK(salario_neto >= 0),
    descripcion_nomina TEXT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_pago_empleado FOREIGN KEY (id_empleado) 
        REFERENCES empleados(id_empleado) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    INDEX idx_empleado_fecha (id_empleado, fecha_pago),
    INDEX idx_fecha_pago (fecha_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA AUDIT TRAIL (Seguridad y Auditoría)
-- ============================================
CREATE TABLE audit_trail (
    id_auditoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    tabla_afectada VARCHAR(50) NOT NULL,
    tipo_operacion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    id_registro INT NOT NULL,
    usuario_sistema VARCHAR(100) NOT NULL,
    valor_anterior JSON,
    valor_nuevo JSON,
    fecha_operacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_usuario VARCHAR(45),
    
    INDEX idx_tabla_fecha (tabla_afectada, fecha_operacion),
    INDEX idx_usuario (usuario_sistema),
    INDEX idx_tipo_operacion (tipo_operacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- CONFIRMACIÓN
-- ============================================
SELECT '✓ Base de datos creada exitosamente' AS estado;
