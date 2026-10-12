let turnosData = [];

// Configuración por defecto personalizable
let appConfig = JSON.parse(localStorage.getItem('app_config_vet')) || {
    pedirTelTutor: true,
    pedirDocTutor: false,
    pedirPesoMascota: true,
    pedirEdadMascota: false
};

let mascotasData = JSON.parse(localStorage.getItem('mascotas_vet_cache')) || [
    { idMascota: 1, nombre: 'Tobias', nombreTutor: 'Duver Vergara', telTutor: '3001234567', especie: 'Canino', raza: 'Golden Retriever', peso: 24.5 },
    { idMascota: 2, nombre: 'Luna', nombreTutor: 'Diana', telTutor: '3109876543', especie: 'Felino', raza: 'Siamés', peso: 4.2 }
];

let medicosData = JSON.parse(localStorage.getItem('medicos_vet_cache')) || [
    { idMedico: 1, nombre: 'Dra. Laura Pérez', especialidad: 'Medicina General', tarjetaProfesional: 'TP-10020' },
    { idMedico: 2, nombre: 'Dr. Carlos Gómez', especialidad: 'Cirugía', tarjetaProfesional: 'TP-30491' }
];

document.addEventListener("DOMContentLoaded", () => {
    aplicarConfiguracionUI();
    inicializar();
    setInterval(sincronizarConServidor, 2500);
});

function aplicarConfiguracionUI() {
    document.getElementById('cfgTelTutor').checked = appConfig.pedirTelTutor;
    document.getElementById('cfgDocTutor').checked = appConfig.pedirDocTutor;
    document.getElementById('cfgPesoMascota').checked = appConfig.pedirPesoMascota;
    document.getElementById('cfgEdadMascota').checked = appConfig.pedirEdadMascota;

    document.getElementById('campoGrupoTelTutor').style.display = appConfig.pedirTelTutor ? '' : 'none';
    document.getElementById('campoGrupoDocTutor').style.display = appConfig.pedirDocTutor ? '' : 'none';
    document.getElementById('campoGrupoPesoMascota').style.display = appConfig.pedirPesoMascota ? '' : 'none';
    document.getElementById('campoGrupoEdadMascota').style.display = appConfig.pedirEdadMascota ? '' : 'none';

    document.getElementById('thTelTutor').style.display = appConfig.pedirTelTutor ? '' : 'none';
    document.getElementById('thPesoMascota').style.display = appConfig.pedirPesoMascota ? '' : 'none';

    renderizarMascotas();
}

function abrirModalConfiguracion() {
    aplicarConfiguracionUI();
    const modal = new bootstrap.Modal(document.getElementById('modalConfiguracion'));
    modal.show();
}

function guardarConfiguracion(e) {
    e.preventDefault();
    appConfig = {
        pedirTelTutor: document.getElementById('cfgTelTutor').checked,
        pedirDocTutor: document.getElementById('cfgDocTutor').checked,
        pedirPesoMascota: document.getElementById('cfgPesoMascota').checked,
        pedirEdadMascota: document.getElementById('cfgEdadMascota').checked
    };

    localStorage.setItem('app_config_vet', JSON.stringify(appConfig));
    aplicarConfiguracionUI();
    bootstrap.Modal.getInstance(document.getElementById('modalConfiguracion')).hide();
}

async function sincronizarConServidor() {
    try {
        const res = await fetch('/api/turnos');
        if (res.ok) {
            const data = await res.json();
            if (JSON.stringify(data) !== JSON.stringify(turnosData)) {
                turnosData = data;
                localStorage.setItem('turnos_vet_cache', JSON.stringify(turnosData));
                renderizarTurnos();
            }
        }
    } catch (e) {
        console.log("Modo offline o reconectando...");
    }
}

async function inicializar() {
    await sincronizarConServidor();
    renderizarMascotas();
    renderizarMedicos();
}

function guardarEnStorage() {
    localStorage.setItem('turnos_vet_cache', JSON.stringify(turnosData));
    localStorage.setItem('mascotas_vet_cache', JSON.stringify(mascotasData));
    localStorage.setItem('medicos_vet_cache', JSON.stringify(medicosData));
}

function formatearHora(ts) {
    if (!ts) return '-';
    return new Date(ts).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', hour12: true });
}

function calcularTiempoEspera(tsRegistro, tsInicioAtencion, estado) {
    if (!tsRegistro) return '-';

    let fin = tsInicioAtencion;
    if (!fin) {
        if (estado === 'Atendido') return 'Atendido';
        fin = new Date().getTime();
    }

    const difMs = fin - tsRegistro;
    const mins = Math.floor(difMs / (1000 * 60));

    if (mins < 1) return '< 1 min';
    if (mins >= 60) {
        const hrs = Math.floor(mins / 60);
        const minsRest = mins % 60;
        return `${hrs}h ${minsRest}m`;
    }
    return `${mins} min`;
}

function calcularDuracionConsulta(tsInicio, tsFin) {
    if (!tsInicio) return '-';
    if (!tsFin) return '<span class="badge bg-info text-dark">En curso</span>';

    const difMs = tsFin - tsInicio;
    const mins = Math.floor(difMs / (1000 * 60));
    const segs = Math.floor((difMs % (1000 * 60)) / 1000);

    return `${mins}m ${segs}s`;
}

function abrirPantallaTV() {
    window.open('tv.html', '_blank');
}

function cerrarSesion() {
    if (confirm('¿Desea cerrar la sesión del sistema?')) {
        window.location.href = 'login.html';
    }
}

// FILTROS
function filtrarTablaTurnos() {
    const query = document.getElementById('inputFiltroTurnos').value.toLowerCase().trim();
    const filas = document.querySelectorAll('#tablaTurnos tr');

    filas.forEach(fila => {
        const textoFila = fila.innerText.toLowerCase();
        fila.style.display = textoFila.includes(query) ? '' : 'none';
    });
}

function filtrarTablaMascotas() {
    const query = document.getElementById('inputFiltroMascotas').value.toLowerCase().trim();
    const filas = document.querySelectorAll('#tablaMascotas tr');

    filas.forEach(fila => {
        const textoFila = fila.innerText.toLowerCase();
        fila.style.display = textoFila.includes(query) ? '' : 'none';
    });
}

function filtrarTablaMedicos() {
    const query = document.getElementById('inputFiltroMedicos').value.toLowerCase().trim();
    const filas = document.querySelectorAll('#tablaMedicos tr');

    filas.forEach(fila => {
        const textoFila = fila.innerText.toLowerCase();
        fila.style.display = textoFila.includes(query) ? '' : 'none';
    });
}

// EXPORTACIONES A EXCEL
function exportarTurnosExcel() {
    if (turnosData.length === 0) {
        alert("No hay turnos registrados para exportar.");
        return;
    }

    const datosExcel = turnosData.map(t => ({
        "Código Turno": t.codigoTurno,
        "Mascota": t.mascota ? t.mascota.nombre : 'N/A',
        "Tutor": t.mascota ? t.mascota.nombreTutor : 'N/A',
        "Servicio / Motivo": t.servicio || 'Consulta General',
        "Médico Asignado": t.medico ? t.medico.nombre : 'Sin asignar',
        "Prioridad": t.prioridad ? t.prioridad.nombrePrioridad : 'Normal',
        "Estado": t.estado,
        "Consultorio": t.consultorio || 'Consultorio 1',
        "Hora Registro": formatearHora(t.timestampRegistro),
        "Tiempo Espera": calcularTiempoEspera(t.timestampRegistro, t.timestampInicioAtencion, t.estado),
        "Duración Consulta": t.timestampFinConsulta ? calcularDuracionConsulta(t.timestampInicioAtencion, t.timestampFinConsulta) : '-'
    }));

    const hoja = XLSX.utils.json_to_sheet(datosExcel);
    const libro = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(libro, hoja, "Reporte Turnos y Tiempos");

    const fechaHoy = new Date().toISOString().split('T')[0];
    XLSX.writeFile(libro, `Reporte_Turnos_y_Tiempos_${fechaHoy}.xlsx`);
}

function exportarMascotasExcel() {
    if (mascotasData.length === 0) {
        alert("No hay mascotas registradas para exportar.");
        return;
    }

    const datosExcel = mascotasData.map(m => ({
        "ID Mascota": m.idMascota,
        "Nombre Mascota": m.nombre,
        "Tutor / Propietario": m.nombreTutor,
        "Celular / WhatsApp": m.telTutor || 'No registrado',
        "Documento Tutor": m.docTutor || 'No registrado',
        "Especie": m.especie || 'N/A',
        "Raza": m.raza || 'N/A',
        "Peso (kg)": m.peso ? m.peso + " kg" : 'N/A',
        "Edad / F. Nacimiento": m.edad || 'N/A'
    }));

    const hoja = XLSX.utils.json_to_sheet(datosExcel);
    const libro = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(libro, hoja, "Directorio Mascotas y Tutores");

    const fechaHoy = new Date().toISOString().split('T')[0];
    XLSX.writeFile(libro, `Directorio_Mascotas_y_Tutores_${fechaHoy}.xlsx`);
}

function exportarMedicosExcel() {
    if (medicosData.length === 0) {
        alert("No hay médicos registrados para exportar.");
        return;
    }

    const datosExcel = medicosData.map(med => ({
        "ID Médico": med.idMedico,
        "Nombre Completo": med.nombre,
        "Especialidad": med.especialidad || 'General',
        "Tarjeta Profesional": med.tarjetaProfesional || 'N/A'
    }));

    const hoja = XLSX.utils.json_to_sheet(datosExcel);
    const libro = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(libro, hoja, "Personal Médico");

    const fechaHoy = new Date().toISOString().split('T')[0];
    XLSX.writeFile(libro, `Personal_Medico_${fechaHoy}.xlsx`);
}

function renderizarTurnos() {
    const tbody = document.getElementById('tablaTurnos');
    if (!tbody) return;
    tbody.innerHTML = '';

    let total = turnosData.length;
    let espera = 0;
    let atendidos = 0;

    turnosData.forEach((t, idx) => {
        if (t.estado === 'En espera' || t.estado === 'Llamando') espera++;
        if (t.estado === 'Atendido') atendidos++;

        const badgePrioridad = t.prioridad.idPrioridad == 1 ? 'badge-prioridad-alta' :
                              (t.prioridad.idPrioridad == 2 ? 'badge-prioridad-media' : 'badge-prioridad-baja');

        let badgeEstado = 'bg-secondary';
        if (t.estado === 'En espera') badgeEstado = 'bg-warning text-dark';
        else if (t.estado === 'Llamando') badgeEstado = 'bg-danger text-white fw-bold animate-pulse';
        else if (t.estado === 'Atendiendo') badgeEstado = 'bg-primary text-white';
        else if (t.estado === 'Atendido') badgeEstado = 'bg-success text-white';

        const tiempoEsperaTexto = calcularTiempoEspera(t.timestampRegistro, t.timestampInicioAtencion, t.estado);
        const duracionTexto = calcularDuracionConsulta(t.timestampInicioAtencion, t.timestampFinConsulta);

        tbody.innerHTML += `
            <tr>
                <td class="fw-bold fs-5 text-primary">${t.codigoTurno}</td>
                <td>
                    <div class="fw-bold">🐾 ${t.mascota.nombre}</div>
                    <small class="text-muted">Tutor: ${t.mascota.nombreTutor}</small>
                </td>
                <td>
                    <span class="badge bg-light text-dark border border-secondary fw-bold">${t.servicio || 'Consulta General'}</span>
                </td>
                <td>${t.medico.nombre}</td>
                <td>
                    <span class="badge ${badgePrioridad} mb-1 d-inline-block">${t.prioridad.nombrePrioridad}</span><br>
                    <span class="badge ${badgeEstado}">${t.estado.toUpperCase()}</span>
                </td>
                <td class="fw-semibold text-dark">${formatearHora(t.timestampRegistro)}</td>
                <td class="fw-bold text-danger">${tiempoEsperaTexto}</td>
                <td class="fw-semibold text-secondary">${duracionTexto}</td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-danger me-1" onclick="abrirModalLlamar(${idx})" title="Llamar a Pantalla TV">
                        <i class="bi bi-megaphone-fill me-1"></i>Llamar
                    </button>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="cambiarEstadoDirecto(${idx}, 'Atendiendo')" title="Ingresar a Consultorio">
                        <i class="bi bi-play-fill me-1"></i>Atender
                    </button>
                    <button class="btn btn-sm btn-success me-1 fw-bold" onclick="cambiarEstadoDirecto(${idx}, 'Atendido')" title="Marcar Consulta como Finalizada">
                        <i class="bi bi-check-circle-fill me-1"></i>Finalizar
                    </button>
                    <button class="btn btn-sm btn-outline-warning me-1 text-dark" onclick="cambiarEstadoDirecto(${idx}, 'En espera')" title="Devolver a Sala de Espera">
                        <i class="bi bi-arrow-counterclockwise me-1"></i>A Espera
                    </button>
                    <button class="btn btn-sm btn-outline-secondary me-1" onclick="editarTurnoModal(${idx})" title="Editar Turno">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-dark" onclick="eliminarTurno(${idx})" title="Eliminar Turno">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    document.getElementById('statTotal').innerText = total;
    document.getElementById('statEspera').innerText = espera;
    document.getElementById('statAtendidos').innerText = atendidos;
}

function abrirModalLlamar(index) {
    document.getElementById('llamarTurnoIndex').value = index;
    const modal = new bootstrap.Modal(document.getElementById('modalLlamar'));
    modal.show();
}

document.getElementById('formLlamarConsultorio').addEventListener('submit', async (e) => {
    e.preventDefault();
    const idx = document.getElementById('llamarTurnoIndex').value;
    const consultorioVal = document.getElementById('selectConsultorio').value;

    const ahoraTimestamp = new Date().getTime();
    turnosData[idx].estado = 'Llamando';
    turnosData[idx].consultorio = consultorioVal;
    turnosData[idx].timestampLlamada = ahoraTimestamp;

    if (!turnosData[idx].timestampInicioAtencion) {
        turnosData[idx].timestampInicioAtencion = ahoraTimestamp;
    }

    await fetch('/api/turnos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(turnosData[idx])
    });

    localStorage.setItem('ultimo_llamado_event', JSON.stringify({
        ...turnosData[idx],
        timestamp: ahoraTimestamp
    }));

    guardarEnStorage();
    renderizarTurnos();
    bootstrap.Modal.getInstance(document.getElementById('modalLlamar')).hide();
});

async function cambiarEstadoDirecto(index, nuevoEstado) {
    const ahora = new Date().getTime();
    const turno = turnosData[index];
    if (!turno) return;

    turno.estado = nuevoEstado;

    if (nuevoEstado === 'Atendiendo' && !turno.timestampInicioAtencion) {
        turno.timestampInicioAtencion = ahora;
    } else if (nuevoEstado === 'Atendido') {
        if (!turno.timestampInicioAtencion) {
            turno.timestampInicioAtencion = ahora - (10 * 60 * 1000);
        }
        turno.timestampFinConsulta = ahora;
    }

    await fetch('/api/turnos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(turno)
    });

    guardarEnStorage();
    renderizarTurnos();
}

function prepararNuevoTurno() {
    document.getElementById('modalTurnoTitulo').innerText = "Registrar Nuevo Turno";
    document.getElementById('turnoIndex').value = "";
    document.getElementById('formTurno').reset();

    const numSiguiente = String(turnosData.length + 1).padStart(2, '0');
    document.getElementById('codigoTurnoNumero').value = numSiguiente;

    if (mascotasData.length > 0) {
        const ultima = mascotasData[mascotasData.length - 1];
        document.getElementById('inputMascotaBusqueda').value = `${ultima.nombre} (Tutor: ${ultima.nombreTutor})`;
    }
}

function editarTurnoModal(index) {
    const t = turnosData[index];
    document.getElementById('modalTurnoTitulo').innerText = "Modificar Turno y Prioridad";
    document.getElementById('turnoIndex').value = index;
    
    document.getElementById('codigoTurnoNumero').value = t.codigoTurno.replace('T-', '');
    document.getElementById('inputMascotaBusqueda').value = `${t.mascota.nombre} (Tutor: ${t.mascota.nombreTutor})`;
    document.getElementById('selectServicio').value = t.servicio || 'Consulta General';
    document.getElementById('selectMedico').value = t.medico.nombre;
    document.getElementById('selectPrioridad').value = t.prioridad.idPrioridad;
    document.getElementById('selectEstado').value = t.estado;

    const modal = new bootstrap.Modal(document.getElementById('modalTurno'));
    modal.show();
}

async function eliminarTurno(index) {
    if (confirm('¿Eliminar este turno?')) {
        await fetch(`/api/turnos/${index}`, { method: 'DELETE' });
        turnosData.splice(index, 1);
        guardarEnStorage();
        renderizarTurnos();
    }
}

document.getElementById('formTurno').addEventListener('submit', async (e) => {
    e.preventDefault();
    const indexStr = document.getElementById('turnoIndex').value;
    const numInput = document.getElementById('codigoTurnoNumero').value.trim();
    const codigoFinal = numInput.startsWith('T-') ? numInput : `T-${numInput}`;

    const busquedaVal = document.getElementById('inputMascotaBusqueda').value;
    const partes = busquedaVal.split(' (Tutor: ');
    const nombreMascota = partes[0] || 'Mascota';
    const tutorMascota = partes[1] ? partes[1].replace(')', '') : 'Tutor';

    const prioVal = document.getElementById('selectPrioridad').value;
    const prioTexto = prioVal == 1 ? 'Alta - Urgencia' : (prioVal == 2 ? 'Media - Consulta' : 'Baja - Control');

    const servicioVal = document.getElementById('selectServicio').value;
    const ahora = new Date().getTime();

    const turnoObj = {
        idTurno: indexStr !== "" ? turnosData[indexStr].idTurno : turnosData.length + 1,
        codigoTurno: codigoFinal,
        mascota: { nombre: nombreMascota, nombreTutor: tutorMascota },
        servicio: servicioVal,
        medico: { nombre: document.getElementById('selectMedico').value || 'Médico General' },
        prioridad: { idPrioridad: parseInt(prioVal), nombrePrioridad: prioTexto },
        estado: document.getElementById('selectEstado').value,
        consultorio: indexStr !== "" ? (turnosData[indexStr].consultorio || 'Consultorio 1') : 'Consultorio 1',
        timestampLlamada: indexStr !== "" ? (turnosData[indexStr].timestampLlamada || 0) : 0,
        timestampRegistro: indexStr !== "" ? (turnosData[indexStr].timestampRegistro || ahora) : ahora,
        timestampInicioAtencion: indexStr !== "" ? (turnosData[indexStr].timestampInicioAtencion || null) : null,
        timestampFinConsulta: indexStr !== "" ? (turnosData[indexStr].timestampFinConsulta || null) : null
    };

    await fetch('/api/turnos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(turnoObj)
    });

    if (indexStr !== "") {
        turnosData[indexStr] = turnoObj;
    } else {
        turnosData.push(turnoObj);
    }

    guardarEnStorage();
    renderizarTurnos();
    bootstrap.Modal.getInstance(document.getElementById('modalTurno')).hide();
});

// MASCOTAS & TUTORES (CRUD CON CAMPOS DINÁMICOS)
function renderizarMascotas() {
    const tbody = document.getElementById('tablaMascotas');
    const datalist = document.getElementById('listaMascotasDatalist');
    if (!tbody || !datalist) return;
    tbody.innerHTML = '';
    datalist.innerHTML = '';

    mascotasData.forEach((m, idx) => {
        const tdTel = appConfig.pedirTelTutor ? `<td>${m.telTutor || '-'}</td>` : '';
        const tdPeso = appConfig.pedirPesoMascota ? `<td>${m.peso ? m.peso + ' kg' : '-'}</td>` : '';

        tbody.innerHTML += `
            <tr>
                <td>${m.idMascota}</td>
                <td class="fw-bold">🐾 ${m.nombre}</td>
                <td>${m.nombreTutor}</td>
                ${tdTel}
                <td>${m.especie || '-'} ${m.raza ? ' / ' + m.raza : ''}</td>
                ${tdPeso}
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="editarMascotaModal(${idx})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarMascota(${idx})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `;
        datalist.innerHTML += `<option value="${m.nombre} (Tutor: ${m.nombreTutor})"></option>`;
        datalist.innerHTML += `<option value="${m.nombreTutor} - ${m.telTutor ? m.telTutor : ''}"></option>`;
    });
}

function prepararNuevaMascota() {
    document.getElementById('modalMascotaTitulo').innerText = "🐾 Registrar Mascota / Tutor";
    document.getElementById('mascotaIndex').value = "";
    document.getElementById('formMascota').reset();
}

function editarMascotaModal(index) {
    const m = mascotasData[index];
    document.getElementById('modalMascotaTitulo').innerText = "🐾 Editar Mascota / Tutor";
    document.getElementById('mascotaIndex').value = index;
    document.getElementById('mascotaNombre').value = m.nombre;
    document.getElementById('mascotaTutor').value = m.nombreTutor;
    document.getElementById('mascotaTelTutor').value = m.telTutor || '';
    document.getElementById('mascotaDocTutor').value = m.docTutor || '';
    document.getElementById('mascotaEspecie').value = m.especie || '';
    document.getElementById('mascotaRaza').value = m.raza || '';
    document.getElementById('mascotaPeso').value = m.peso || '';
    document.getElementById('mascotaEdad').value = m.edad || '';

    const modal = new bootstrap.Modal(document.getElementById('modalMascota'));
    modal.show();
}

document.getElementById('formMascota').addEventListener('submit', (e) => {
    e.preventDefault();
    const indexStr = document.getElementById('mascotaIndex').value;
    const mascotaObj = {
        idMascota: indexStr !== "" ? mascotasData[indexStr].idMascota : mascotasData.length + 1,
        nombre: document.getElementById('mascotaNombre').value,
        nombreTutor: document.getElementById('mascotaTutor').value,
        telTutor: document.getElementById('mascotaTelTutor').value,
        docTutor: document.getElementById('mascotaDocTutor').value,
        especie: document.getElementById('mascotaEspecie').value,
        raza: document.getElementById('mascotaRaza').value,
        peso: document.getElementById('mascotaPeso').value ? parseFloat(document.getElementById('mascotaPeso').value) : null,
        edad: document.getElementById('mascotaEdad').value
    };

    if (indexStr !== "") {
        mascotasData[indexStr] = mascotaObj;
    } else {
        mascotasData.push(mascotaObj);
    }

    guardarEnStorage();
    renderizarMascotas();
    bootstrap.Modal.getInstance(document.getElementById('modalMascota')).hide();
});

function eliminarMascota(index) {
    if (confirm('¿Eliminar esta mascota?')) {
        mascotasData.splice(index, 1);
        guardarEnStorage();
        renderizarMascotas();
    }
}

// MÉDICOS (CRUD)
function renderizarMedicos() {
    const tbody = document.getElementById('tablaMedicos');
    const select = document.getElementById('selectMedico');
    if (!tbody || !select) return;
    tbody.innerHTML = '';
    select.innerHTML = '<option value="">Seleccione un médico...</option>';

    medicosData.forEach((med, idx) => {
        tbody.innerHTML += `
            <tr>
                <td>${med.idMedico}</td>
                <td class="fw-bold">${med.nombre}</td>
                <td>${med.especialidad}</td>
                <td>${med.tarjetaProfesional}</td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="editarMedicoModal(${idx})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarMedico(${idx})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `;
        select.innerHTML += `<option value="${med.nombre}">${med.nombre} - ${med.especialidad}</option>`;
    });
}

function prepararNuevoMedico() {
    document.getElementById('modalMedicoTitulo').innerText = "Registrar Médico";
    document.getElementById('medicoIndex').value = "";
    document.getElementById('formMedico').reset();
}

function editarMedicoModal(index) {
    const med = medicosData[index];
    document.getElementById('modalMedicoTitulo').innerText = "Editar Médico";
    document.getElementById('medicoIndex').value = index;
    document.getElementById('medicoNombre').value = med.nombre;
    document.getElementById('medicoEspecialidad').value = med.especialidad;
    document.getElementById('medicoTarjeta').value = med.tarjetaProfesional;

    const modal = new bootstrap.Modal(document.getElementById('modalMedico'));
    modal.show();
}

document.getElementById('formMedico').addEventListener('submit', (e) => {
    e.preventDefault();
    const indexStr = document.getElementById('medicoIndex').value;
    const medicoObj = {
        idMedico: indexStr !== "" ? medicosData[indexStr].idMedico : medicosData.length + 1,
        nombre: document.getElementById('medicoNombre').value,
        especialidad: document.getElementById('medicoEspecialidad').value,
        tarjetaProfesional: document.getElementById('medicoTarjeta').value
    };

    if (indexStr !== "") {
        medicosData[indexStr] = medicoObj;
    } else {
        medicosData.push(medicoObj);
    }

    guardarEnStorage();
    renderizarMedicos();
    bootstrap.Modal.getInstance(document.getElementById('modalMedico')).hide();
});

function eliminarMedico(index) {
    if (confirm('¿Eliminar este médico?')) {
        medicosData.splice(index, 1);
        guardarEnStorage();
        renderizarMedicos();
    }
}
