package com.example.controlpresencia;

// Respuesta del endpoint de resumen mensual — necesaria para mostrar las horas extra en la app
public class ResumenMensualResponse {
    public double horas_trabajadas;  // Horas reales fichadas en el mes
    public double horas_teoricas;    // Horas que debería haber trabajado según su horario
    public double horas_extra;       // Diferencia: trabajadas - teóricas
    public String mes;               // Mes consultado en formato YYYY-MM
}
