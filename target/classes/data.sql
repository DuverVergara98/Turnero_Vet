-- Insertar Niveles de Prioridad si no existen
MERGE INTO nivel_prioridad (id_prioridad, nombre_prioridad, nivel, color_identificador) 
KEY(id_prioridad) 
VALUES 
(1, 'Alta - Urgencia', 1, 'Rojo'),
(2, 'Media - Consulta', 2, 'Amarillo'),
(3, 'Baja - Control', 3, 'Verde');

-- Insertar Médicos de prueba
MERGE INTO personal_medico (id_medico, documento_identidad, nombre, especialidad, rol, estado) 
KEY(id_medico) 
VALUES 
(1, '100100100', 'Dra. Laura Pérez', 'Medicina General', 'Veterinario', 'Activo'),
(2, '100100202', 'Dr. Carlos Gómez', 'Cirugía y Traumatología', 'Veterinario', 'Activo');

-- Insertar Mascotas de prueba
MERGE INTO mascota (id_mascota, nombre, nombre_tutor, especie, raza, sexo) 
KEY(id_mascota) 
VALUES 
(1, 'Tobias', 'Duver Vergara', 'Canino', 'Golden Retriever', 'Macho'),
(2, 'Luna', 'Diana', 'Felino', 'Siamés', 'Hembra');

-- Insertar Turnos iniciales
MERGE INTO turno (id_turno, codigo_turno, id_mascota, id_medico, id_prioridad, estado, fecha, hora) 
KEY(id_turno) 
VALUES 
(1, 'T-001', 1, 1, 1, 'En espera', CURRENT_DATE(), CURRENT_TIME()),
(2, 'T-002', 2, 2, 2, 'Atendiendo', CURRENT_DATE(), CURRENT_TIME());
