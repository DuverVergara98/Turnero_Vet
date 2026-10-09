INSERT INTO MASCOTA (nombre, especie, raza, sexo) VALUES ('Rocky', 'Canino', 'Criollo', 'Macho');
INSERT INTO MASCOTA (nombre, especie, raza, sexo) VALUES ('Mimi', 'Felino', 'Siames', 'Hembra');

INSERT INTO PERSONAL_MEDICO (nombre, especialidad, tarjeta_profesional) VALUES ('Dr. Carlos Perez', 'Medicina General', 'TP-10023');
INSERT INTO PERSONAL_MEDICO (nombre, especialidad, tarjeta_profesional) VALUES ('Dra. Laura Gomez', 'Cirugia', 'TP-20541');

INSERT INTO NIVEL_PRIORIDAD (descripcion, color_hex) VALUES ('ALTA - URGENCIA', '#FF0000');
INSERT INTO NIVEL_PRIORIDAD (descripcion, color_hex) VALUES ('MEDIA - CONSULTA', '#FFA500');
INSERT INTO NIVEL_PRIORIDAD (descripcion, color_hex) VALUES ('BAJA - CONTROL', '#008000');

INSERT INTO TURNO (codigo_turno, id_mascota, id_medico, id_prioridad, estado) VALUES ('T-001', 1, 1, 1, 'EN_ESPERA');
INSERT INTO TURNO (codigo_turno, id_mascota, id_medico, id_prioridad, estado) VALUES ('T-002', 2, 2, 2, 'EN_ESPERA');
