CREATE TABLE IF NOT EXISTS nivel_prioridad (
    id_prioridad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_prioridad VARCHAR(100) NOT NULL,
    nivel INT NOT NULL,
    color_identificador VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS personal_medico (
    id_medico BIGINT AUTO_INCREMENT PRIMARY KEY,
    documento_identidad VARCHAR(50),
    nombre VARCHAR(150) NOT NULL,
    especialidad VARCHAR(100),
    rol VARCHAR(50),
    tarjeta_profesional VARCHAR(50),
    estado VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS mascota (
    id_mascota BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    nombre_tutor VARCHAR(150) NOT NULL,
    especie VARCHAR(50),
    raza VARCHAR(50),
    sexo VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS turno (
    id_turno BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_turno VARCHAR(20) NOT NULL,
    id_mascota BIGINT,
    id_medico BIGINT,
    id_prioridad BIGINT,
    estado VARCHAR(50),
    fecha DATE,
    hora TIME,
    FOREIGN KEY (id_mascota) REFERENCES mascota(id_mascota),
    FOREIGN KEY (id_medico) REFERENCES personal_medico(id_medico),
    FOREIGN KEY (id_prioridad) REFERENCES nivel_prioridad(id_prioridad)
);
