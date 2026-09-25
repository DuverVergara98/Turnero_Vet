-- 1. Crear la base de datos
CREATE DATABASE TurneroVeterinario;
GO

USE TurneroVeterinario;
GO

-- 2. Tabla TUTOR
CREATE TABLE TUTOR (
    id_tutor INT IDENTITY(1,1) PRIMARY KEY,
    documento_identidad VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(150)
);

-- 3. Tabla MASCOTA
CREATE TABLE MASCOTA (
    id_mascota INT IDENTITY(1,1) PRIMARY KEY,
    id_tutor INT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    especie VARCHAR(30) NOT NULL,
    raza VARCHAR(50),
    fecha_nacimiento DATE,
    sexo VARCHAR(10) CHECK (sexo IN ('Macho', 'Hembra')),
    CONSTRAINT FK_Mascota_Tutor FOREIGN KEY (id_tutor) REFERENCES TUTOR(id_tutor)
);

-- 4. Tabla NIVEL_PRIORIDAD
CREATE TABLE NIVEL_PRIORIDAD (
    id_prioridad INT IDENTITY(1,1) PRIMARY KEY,
    nombre_prioridad VARCHAR(50) NOT NULL, -- Ej: Urgencia, General
    nivel INT NOT NULL,                   -- Ej: 1-Crítico, 2-Alta, 3-Normal
    color_identificador VARCHAR(20),      -- Ej: Rojo, Amarillo, Verde
    descripcion VARCHAR(200)
);

-- 5. Tabla PERSONAL_MEDICO
CREATE TABLE PERSONAL_MEDICO (
    id_medico INT IDENTITY(1,1) PRIMARY KEY,
    documento_identidad VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(30) CHECK (rol IN ('Recepción', 'Veterinario')),
    especialidad VARCHAR(50),
    telefono VARCHAR(20),
    email VARCHAR(100),
    estado VARCHAR(10) DEFAULT 'Activo' CHECK (estado IN ('Activo', 'Inactivo'))
);

-- 6. Tabla TURNO
CREATE TABLE TURNO (
    id_turno INT IDENTITY(1,1) PRIMARY KEY,
    codigo_turno VARCHAR(10) NOT NULL, -- Ej: A-001
    id_mascota INT NOT NULL,
    id_prioridad INT NOT NULL,
    id_medico INT NOT NULL,
    fecha DATE DEFAULT CAST(GETDATE() AS DATE),
    hora TIME DEFAULT CAST(GETDATE() AS TIME),
    estado VARCHAR(20) DEFAULT 'En espera' CHECK (estado IN ('En espera', 'Atendiendo', 'Finalizado', 'Cancelado')),
    CONSTRAINT FK_Turno_Mascota FOREIGN KEY (id_mascota) REFERENCES MASCOTA(id_mascota),
    CONSTRAINT FK_Turno_Prioridad FOREIGN KEY (id_prioridad) REFERENCES NIVEL_PRIORIDAD(id_prioridad),
    CONSTRAINT FK_Turno_Medico FOREIGN KEY (id_medico) REFERENCES PERSONAL_MEDICO(id_medico)
);

-- 7. Tabla CONSULTA
CREATE TABLE CONSULTA (
    id_consulta INT IDENTITY(1,1) PRIMARY KEY,
    id_turno INT NOT NULL UNIQUE, -- Relación 1:1 con Turno
    id_mascota INT NOT NULL,
    id_medico INT NOT NULL,
    diagnostico VARCHAR(MAX) NOT NULL,
    tratamiento VARCHAR(MAX),
    observaciones VARCHAR(MAX),
    fecha_atencion DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Consulta_Turno FOREIGN KEY (id_turno) REFERENCES TURNO(id_turno),
    CONSTRAINT FK_Consulta_Mascota FOREIGN KEY (id_mascota) REFERENCES MASCOTA(id_mascota),
    CONSTRAINT FK_Consulta_Medico FOREIGN KEY (id_medico) REFERENCES PERSONAL_MEDICO(id_medico)
);
GO