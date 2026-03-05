package com.example.controlpresencia;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

// Worker de WorkManager que se ejecuta periódicamente para comprobar si el empleado ha fichado
// Si no ha fichado a su hora, lanza una notificación recordándoselo
// Para usarlo necesito añadir al build.gradle: implementation 'androidx.work:work-runtime:2.9.0'
public class FichajeNotificationWorker extends Worker {

    // ID del canal de notificaciones — obligatorio desde Android 8.0 (Oreo)
    private static final String CHANNEL_ID = "canal_fichaje";

    public FichajeNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Recibo el tipo de aviso: "entrada" o "salida"
        String tipoAviso = getInputData().getString("tipo_aviso");

        if ("entrada".equals(tipoAviso)) {
            mostrarNotificacion(
                    "⏰ Recuerda fichar",
                    "Deberías haber fichado la entrada. ¡No olvides registrar tu presencia!"
            );
        } else if ("salida".equals(tipoAviso)) {
            mostrarNotificacion(
                    "⏰ Recuerda fichar la salida",
                    "Ha pasado tu hora de salida. ¡No olvides fichar!"
            );
        }

        return Result.success();
    }

    // Construyo y muestro la notificación con el mensaje correspondiente
    private void mostrarNotificacion(String titulo, String mensaje) {
        NotificationManager notifManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // En Android 8+ necesito crear el canal de notificaciones antes de mostrar ninguna
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Avisos de fichaje",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Recordatorios para fichar entrada y salida");
            notifManager.createNotificationChannel(canal);
        }

        // Construyo la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // La notificación desaparece al tocarla

        notifManager.notify(1001, builder.build());
    }
}
