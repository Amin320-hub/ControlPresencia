package com.example.controlpresencia;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

// Clase de utilidad para programar las notificaciones de fichaje con WorkManager
// La llamo desde FicharActivity justo después de que el usuario fichaje la entrada/salida
public class NotificationScheduler {

    // Programo un aviso para dentro de X minutos desde ahora
    // Por ejemplo: si entra a las 9:00 y no ficha salida, aviso a las 17:30 (hora de salida + 15min margen)
    public static void programarAvisoSalida(Context context, long minutosHastaAviso) {
        Data inputData = new Data.Builder()
                .putString("tipo_aviso", "salida")
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(FichajeNotificationWorker.class)
                .setInitialDelay(minutosHastaAviso, TimeUnit.MINUTES)
                .setInputData(inputData)
                .addTag("aviso_salida") // Uso el tag para poder cancelarlo si el usuario ficha antes
                .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }

    // Programo un aviso de entrada para mañana por si no ficha a su hora
    // Calculo el delay hasta la hora de entrada del trabajador
    public static void programarAvisoEntrada(Context context, int horaEntrada, int minutosEntrada) {
        // Calculo los milisegundos que faltan hasta la hora de entrada de mañana + 15 min de margen
        Calendar ahora = Calendar.getInstance();
        Calendar horaObjetivo = Calendar.getInstance();

        horaObjetivo.set(Calendar.HOUR_OF_DAY, horaEntrada);
        horaObjetivo.set(Calendar.MINUTE, minutosEntrada + 15); // 15 min de margen
        horaObjetivo.set(Calendar.SECOND, 0);

        // Si ya ha pasado esa hora hoy, lo programo para mañana
        if (horaObjetivo.before(ahora)) {
            horaObjetivo.add(Calendar.DAY_OF_YEAR, 1);
        }

        long delayMs = horaObjetivo.getTimeInMillis() - ahora.getTimeInMillis();

        Data inputData = new Data.Builder()
                .putString("tipo_aviso", "entrada")
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(FichajeNotificationWorker.class)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("aviso_entrada")
                .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }

    // Cancelo el aviso de salida cuando el trabajador ficha correctamente
    public static void cancelarAvisoSalida(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("aviso_salida");
    }

    // Cancelo el aviso de entrada cuando el trabajador ficha correctamente
    public static void cancelarAvisoEntrada(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("aviso_entrada");
    }
}
