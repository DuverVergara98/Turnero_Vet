package com.veterinaria.turnero.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private int status;
    private String error;
    private String mensaje;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
