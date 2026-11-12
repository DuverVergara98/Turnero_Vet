// === ESTADO GLOBAL CON RESPALDO INMEDIATO EN LOCALSTORAGE ===
let turnosData = JSON.parse(localStorage.getItem('turnos_vet_cache') || '[]');

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
    setInterval(sincronizarConServidor, 3000);
});

function aplicarConfiguracionUI() {
    const elTel = document.getElementById('cfgTelTutor');
    if (!elTel) return;

    elTel.checked = appConfig.pedirTelTutor;
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

// === SINCRONIZACIÓN DOBLE: H2 + LOCALSTORAGE ===
async function sincronizarConServidor() {
    try {
        const [resTurnos, resMascotas, resMedicos] = await Promise.all([
            fetch('/api/sync/TURNOS'),
            fetch('/api/sync/MASCOTAS'),
            fetch('/api/sync/MEDICOS')
        ]);

        let cambio = false;

        if (resTurnos.ok) {
            const tData = await resTurnos.json();
            if (Array.isArray(tData) && tData.length > 0 && JSON.stringify(tData) !== JSON.stringify(turnosData)) {
                turnosData = tData;
                localStorage.setItem('turnos_vet_cache', JSON.stringify(turnosData));
                cambio = true;
            }
        }
        if (resMascotas.ok) {
            const mData = await resMascotas.json();
            if (Array.isArray(mData) && mData.length > 0 && JSON.stringify(mData) !== JSON.stringify(mascotasData)) {
                mascotasData = mData;
                localStorage.setItem('mascotas_vet_cache', JSON.stringify(mascotasData));
                cambio = true;
            }
        }
        if (resMedicos.ok) {
            const medData = await resMedicos.json();
            if (Array.isArray(medData) && medData.length > 0 && JSON.stringify(medData) !== JSON.stringify(medicosData)) {
                medicosData = medData;
                localStorage.setItem('medicos_vet_cache', JSON.stringify(medicosData));
                cambio = true;
            }
        }

        if (cambio) {
            renderizarTurnos();
            renderizarMascotas();
            renderizarMedicos();
        }
    } catch (e) {
        console.warn("Modo offline activo - usando datos de localStorage.");
    }
}

async function inicializar() {
    // Dibujar inmediatamente lo que haya en localStorage para cero latencia
    renderizarTurnos();
    renderizarMascotas();
    renderizarMedicos();
    // Luego sincronizar con H2
    await sincronizarConServidor();
}

async function guardarEnStorage() {
    // 1. Guardar inmediatamente en el navegador (localStorage)
    localStorage.setItem('turnos_vet_cache', JSON.stringify(turnosData));
    localStorage.setItem('mascotas_vet_cache', JSON.stringify(mascotasData));
    localStorage.setItem('medicos_vet_cache', JSON.stringify(medicosData));

    // 2. Guardar en la Base de Datos H2 del servidor
    try {
        await Promise.all([
            fetch('/api/sync/TURNOS', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(turnosData) }),
            fetch('/api/sync/MASCOTAS', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(mascotasData) }),
            fetch('/api/sync/MEDICOS', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(medicosData) })
        ]);
    } catch (e) {
        console.error("Error sincronizando con H2 (respaldado en localStorage):", e);
    }
}

// === FORMATEO Y TIEMPOS ===
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
    const difMs = Math.max(0, fin - tsRegistro);
    const mins = Math.floor(difMs / (1000 * 60));

    if (mins < 1) return '< 1 min';
    if (mins >= 60) {
        const hrs = Math.floor(mins / 60);
        const minsRest = mins % 60;
        return `${hrs}h ${minsRest}m`;
    }
    return `${mins} min`;
}

function calcularDuracionConsulta(tsInicio, tsFin, estado) {
    if (!tsInicio) return '-';
    if (!tsFin || estado !== 'Atendido') {
        return '<span class="badge bg-info text-dark">En curso</span>';
    }
    const difMs = Math.max(0, tsFin - tsInicio);
    const mins = Math.floor(difMs / (1000 * 60));
    const segs = Math.floor((difMs % (1000 * 60)) / 1000);
    return `<span class="fw-bold text-success">${mins}m ${segs}s</span>`;
}

function abrirPantallaTV() {
    window.open('tv.html', '_blank');
}

function cerrarSesion() {
    if (confirm('¿Desea cerrar la sesión del sistema?')) {
        window.location.href = 'login.html';
    }
}

// === FILTROS ===
function filtrarTablaTurnos() {
    const query = document.getElementById('inputFiltroTurnos').value.toLowerCase().trim();
    document.querySelectorAll('#tablaTurnos tr').forEach(fila => {
        fila.style.display = fila.innerText.toLowerCase().includes(query) ? '' : 'none';
    });
}

function filtrarTablaMascotas() {
    const query = document.getElementById('inputFiltroMascotas').value.toLowerCase().trim();
    document.querySelectorAll('#tablaMascotas tr').forEach(fila => {
        fila.style.display = fila.innerText.toLowerCase().includes(query) ? '' : 'none';
    });
}

function filtrarTablaMedicos() {
    const query = document.getElementById('inputFiltroMedicos').value.toLowerCase().trim();
    document.querySelectorAll('#tablaMedicos tr').forEach(fila => {
        fila.style.display = fila.innerText.toLowerCase().includes(query) ? '' : 'none';
    });
}

// === EXPORTACIONES EXCEL ===
function exportarTurnosExcel() {
    if (turnosData.length === 0) return alert("No hay turnos registrados para exportar.");
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
        "Tiempo Espera": calcularTiempoEspera(t.timestampRegistro, t.timestampInicioAtencion, t.estado)
    }));
    const hoja = XLSX.utils.json_to_sheet(datosExcel);
    const libro = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(libro, hoja, "Reporte Turnos");
    XLSX.writeFile(libro, `Reporte_Turnos_${new Date().toISOString().split('T')[0]}.xlsx`);
}

function exportarMascotasExcel() {
    if (mascotasData.length === 0) return alert("No hay mascotas registradas para exportar.");
    const datosExcel = mascotasData.map(m => ({
        "ID": m.idMascota,
        "Mascota": m.nombre,
        "Tutor": m.nombreTutor,
        "Celular": m.telTutor || '-',
        "Especie": m.especie || '-',
        "Raza": m.raza || '-',
        "Peso (kg)": m.peso || '-'
    }));
    const hoja = XLSX.utils.json_to_sheet(datosExcel);
    const libro = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(libro, hoja, "Directorio Mascotas");
    XLSX.writeFile(libro, `Directorio_Mascotas_${new Date().toISOString().split('T')[0]}.xlsx`);
}

function exportarMedicosExcel() {
    if (medicosData.length === 0) return alert("No hay médicos registrados para exportar.");
    const datosExcel = medicosData.map(med => ({
        "ID": med.idMedico,
        "Nombre": med.nombre,
        "Especialidad": med.especialidad || '-',
        "Tarjeta Profesional": med.tarjetaProfesional || '-'
    }));
    const hoja = XLSX.utils.json_to_sheet(datosExcel);
    const libro = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(libro, hoja, "Personal Médico");
    XLSX.writeFile(libro, `Personal_Medico_${new Date().toISOString().split('T')[0]}.xlsx`);
}

// === RENDERIZADO Y CRUD DE TURNOS ===
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

        const prioId = t.prioridad ? t.prioridad.idPrioridad : 2;
        const badgePrioridad = prioId == 1 ? 'badge-prioridad-alta' : (prioId == 2 ? 'badge-prioridad-media' : 'badge-prioridad-baja');

        let badgeEstado = 'bg-secondary';
        if (t.estado === 'En espera') badgeEstado = 'bg-warning text-dark';
        else if (t.estado === 'Llamando') badgeEstado = 'bg-danger text-white fw-bold animate-pulse';
        else if (t.estado === 'Atendiendo') badgeEstado = 'bg-primary text-white';
        else if (t.estado === 'Atendido') badgeEstado = 'bg-success text-white';

        const tiempoEsperaTexto = calcularTiempoEspera(t.timestampRegistro, t.timestampInicioAtencion, t.estado);
        const duracionTexto = calcularDuracionConsulta(t.timestampInicioAtencion, t.timestampFinConsulta, t.estado);

        tbody.innerHTML += `
            <tr>
                <td class="fw-bold fs-5 text-primary">${t.codigoTurno}</td>
                <td>
                    <div class="fw-bold">🐾 ${t.mascota ? t.mascota.nombre : 'Sin nombre'}</div>
                    <small class="text-muted">Tutor: ${t.mascota ? t.mascota.nombreTutor : 'N/A'}</small>
                </td>
                <td><span class="badge bg-light text-dark border border-secondary fw-bold">${t.servicio || 'Consulta General'}</span></td>
                <td>${t.medico ? t.medico.nombre : 'Sin asignar'}</td>
                <td>
                    <span class="badge ${badgePrioridad} mb-1 d-inline-block">${t.prioridad ? t.prioridad.nombrePrioridad : 'Media'}</span><br>
                    <span class="badge ${badgeEstado}">${(t.estado || 'En espera').toUpperCase()}</span>
                </td>
                <td class="fw-semibold text-dark">${formatearHora(t.timestampRegistro)}</td>
                <td class="fw-bold text-danger">${tiempoEsperaTexto}</td>
                <td class="fw-semibold text-secondary">${duracionTexto}</td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-danger me-1" onclick="abrirModalLlamar(${idx})" title="Llamar a TV">
                        <i class="bi bi-megaphone-fill"></i> Llamar
                    </button>
                    <button class="btn btn-sm btn-outline-success me-1" onclick="cambiarEstadoDirecto(${idx}, 'Atendido')" title="Finalizar Consulta">
                        <i class="bi bi-check-lg"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="editarTurnoModal(${idx})" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-secondary" onclick="eliminarTurno(${idx})" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    if (document.getElementById('statTotal')) document.getElementById('statTotal').innerText = total;
    if (document.getElementById('statEspera')) document.getElementById('statEspera').innerText = espera;
    if (document.getElementById('statAtendidos')) document.getElementById('statAtendidos').innerText = atendidos;
}

function abrirModalLlamar(index) {
    document.getElementById('llamarTurnoIndex').value = index;
    const modal = new bootstrap.Modal(document.getElementById('modalLlamar'));
    modal.show();
}

const formLlamar = document.getElementById('formLlamarConsultorio');
if (formLlamar) {
    formLlamar.addEventListener('submit', async (e) => {
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

        localStorage.setItem('ultimo_llamado_event', JSON.stringify({ ...turnosData[idx], timestamp: ahoraTimestamp }));
        await guardarEnStorage();
        renderizarTurnos();
        bootstrap.Modal.getInstance(document.getElementById('modalLlamar')).hide();
    });
}

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

    await guardarEnStorage();
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
        turnosData.splice(index, 1);
        await guardarEnStorage();
        renderizarTurnos();
    }
}

const formTurno = document.getElementById('formTurno');
if (formTurno) {
    formTurno.addEventListener('submit', async (e) => {
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
        const estadoVal = document.getElementById('selectEstado').value;
        const ahora = new Date().getTime();

        const previo = indexStr !== "" ? turnosData[indexStr] : null;
        let tsFin = previo ? previo.timestampFinConsulta : null;
        let tsInicio = previo ? previo.timestampInicioAtencion : null;

        if (estadoVal === 'Atendido' && !tsFin) {
            if (!tsInicio) tsInicio = ahora - (5 * 60 * 1000);
            tsFin = ahora;
        }

        const turnoObj = {
            idTurno: previo ? previo.idTurno : turnosData.length + 1,
            codigoTurno: codigoFinal,
            mascota: { nombre: nombreMascota, nombreTutor: tutorMascota },
            servicio: servicioVal,
            medico: { nombre: document.getElementById('selectMedico').value || 'Médico General' },
            prioridad: { idPrioridad: parseInt(prioVal), nombrePrioridad: prioTexto },
            estado: estadoVal,
            consultorio: previo ? (previo.consultorio || 'Consultorio 1') : 'Consultorio 1',
            timestampLlamada: previo ? (previo.timestampLlamada || 0) : 0,
            timestampRegistro: previo ? (previo.timestampRegistro || ahora) : ahora,
            timestampInicioAtencion: tsInicio,
            timestampFinConsulta: tsFin
        };

        if (indexStr !== "") {
            turnosData[indexStr] = turnoObj;
        } else {
            turnosData.push(turnoObj);
        }

        await guardarEnStorage();
        renderizarTurnos();
        bootstrap.Modal.getInstance(document.getElementById('modalTurno')).hide();
    });
}

// === RENDERIZADO Y CRUD DE MASCOTAS / PACIENTES ===
function renderizarMascotas() {
    const tbody = document.getElementById('tablaMascotas');
    const datalist = document.getElementById('listaMascotasDatalist');
    if (!tbody) return;
    tbody.innerHTML = '';
    if (datalist) datalist.innerHTML = '';

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
        if (datalist) {
            datalist.innerHTML += `<option value="${m.nombre} (Tutor: ${m.nombreTutor})"></option>`;
        }
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

async function eliminarMascota(index) {
    if (confirm('¿Eliminar esta mascota del directorio?')) {
        mascotasData.splice(index, 1);
        await guardarEnStorage();
        renderizarMascotas();
    }
}

const formMascota = document.getElementById('formMascota');
if (formMascota) {
    formMascota.addEventListener('submit', async (e) => {
        e.preventDefault();
        const indexStr = document.getElementById('mascotaIndex').value;
        const mascotaObj = {
            idMascota: indexStr !== "" ? mascotasData[indexStr].idMascota : mascotasData.length + 1,
            nombre: document.getElementById('mascotaNombre').value.trim(),
            nombreTutor: document.getElementById('mascotaTutor').value.trim(),
            telTutor: document.getElementById('mascotaTelTutor').value.trim(),
            docTutor: document.getElementById('mascotaDocTutor').value.trim(),
            especie: document.getElementById('mascotaEspecie').value.trim(),
            raza: document.getElementById('mascotaRaza').value.trim(),
            peso: document.getElementById('mascotaPeso').value,
            edad: document.getElementById('mascotaEdad').value.trim()
        };

        if (indexStr !== "") {
            mascotasData[indexStr] = mascotaObj;
        } else {
            mascotasData.push(mascotaObj);
        }

        await guardarEnStorage();
        renderizarMascotas();
        bootstrap.Modal.getInstance(document.getElementById('modalMascota')).hide();
    });
}

// === RENDERIZADO Y CRUD DE MÉDICOS ===
function renderizarMedicos() {
    const tbody = document.getElementById('tablaMedicos');
    const select = document.getElementById('selectMedico');
    if (!tbody) return;
    tbody.innerHTML = '';
    if (select) select.innerHTML = '<option value="">Seleccione un médico...</option>';

    medicosData.forEach((med, idx) => {
        tbody.innerHTML += `
            <tr>
                <td>${med.idMedico}</td>
                <td class="fw-bold">${med.nombre}</td>
                <td>${med.especialidad || '-'}</td>
                <td>${med.tarjetaProfesional || '-'}</td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="editarMedicoModal(${idx})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarMedico(${idx})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `;
        if (select) {
            select.innerHTML += `<option value="${med.nombre}">${med.nombre} - ${med.especialidad}</option>`;
        }
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
    document.getElementById('medicoEspecialidad').value = med.especialidad || '';
    document.getElementById('medicoTarjeta').value = med.tarjetaProfesional || '';

    const modal = new bootstrap.Modal(document.getElementById('modalMedico'));
    modal.show();
}

async function eliminarMedico(index) {
    if (confirm('¿Eliminar este médico?')) {
        medicosData.splice(index, 1);
        await guardarEnStorage();
        renderizarMedicos();
    }
}

const formMedico = document.getElementById('formMedico');
if (formMedico) {
    formMedico.addEventListener('submit', async (e) => {
        e.preventDefault();
        const indexStr = document.getElementById('medicoIndex').value;
        const medicoObj = {
            idMedico: indexStr !== "" ? medicosData[indexStr].idMedico : medicosData.length + 1,
            nombre: document.getElementById('medicoNombre').value.trim(),
            especialidad: document.getElementById('medicoEspecialidad').value.trim(),
            tarjetaProfesional: document.getElementById('medicoTarjeta').value.trim()
        };

        if (indexStr !== "") {
            medicosData[indexStr] = medicoObj;
        } else {
            medicosData.push(medicoObj);
        }

        await guardarEnStorage();
        renderizarMedicos();
        bootstrap.Modal.getInstance(document.getElementById('modalMedico')).hide();
    });
}