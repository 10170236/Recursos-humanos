-- ============================================
-- FASE 2: SEGURIDAD - ROLES Y USUARIOS
-- ============================================

USE recursos_humanos;

-- ============================================
-- CREAR ROLES DE SEGURIDAD
-- ============================================

-- 1. ROL: Gerente_RRHH (Acceso Total a Recursos Humanos)
CREATE ROLE IF NOT EXISTS 'Gerente_RRHH'@'localhost';

-- Permisos: Lectura y modificación total
GRANT SELECT, INSERT, UPDATE, DELETE ON recursos_humanos.* TO 'Gerente_RRHH'@'localhost';
GRANT CREATE, ALTER, DROP ON recursos_humanos.* TO 'Gerente_RRHH'@'localhost';

-- 2. ROL: Supervisor (Acceso Limitado - Solo Lectura y Reportes)
CREATE ROLE IF NOT EXISTS 'Supervisor'@'localhost';

-- Permisos: Solo lectura de tablas
GRANT SELECT ON recursos_humanos.empleados TO 'Supervisor'@'localhost';
GRANT SELECT ON recursos_humanos.marcaciones TO 'Supervisor'@'localhost';
GRANT SELECT ON recursos_humanos.pagos_nomina TO 'Supervisor'@'localhost';
GRANT SELECT ON recursos_humanos.parametros_ley TO 'Supervisor'@'localhost';
GRANT SELECT ON recursos_humanos.audit_trail TO 'Supervisor'@'localhost';

-- ============================================
-- CREAR USUARIOS Y ASIGNAR ROLES
-- ============================================

-- Usuario 1: Administrador de RRHH
CREATE USER IF NOT EXISTS 'admin_rrhh'@'localhost' IDENTIFIED BY 'Admin@RH2026';
GRANT 'Gerente_RRHH'@'localhost' TO 'admin_rrhh'@'localhost';
ALTER USER 'admin_rrhh'@'localhost' DEFAULT ROLE 'Gerente_RRHH'@'localhost';

-- Usuario 2: Supervisor de Recursos
CREATE USER IF NOT EXISTS 'supervisor_rh'@'localhost' IDENTIFIED BY 'Supervisor@RH2026';
GRANT 'Supervisor'@'localhost' TO 'supervisor_rh'@'localhost';
ALTER USER 'supervisor_rh'@'localhost' DEFAULT ROLE 'Supervisor'@'localhost';

-- Usuario 3: Usuario de lectura general (sin modificación)
CREATE USER IF NOT EXISTS 'consulta_rh'@'localhost' IDENTIFIED BY 'Consulta@RH2026';
GRANT SELECT ON recursos_humanos.* TO 'consulta_rh'@'localhost';

-- ============================================
-- APLICAR CAMBIOS
-- ============================================
FLUSH PRIVILEGES;

-- ============================================
-- CONFIRMACIÓN
-- ============================================
SELECT '✓ Roles y usuarios creados exitosamente' AS estado;
SELECT USER, authentication_string FROM mysql.user WHERE USER IN ('admin_rrhh', 'supervisor_rh', 'consulta_rh');
