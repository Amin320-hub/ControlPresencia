package com.example.controlpresencia;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

// Activity de fichaje por NFC
// Cuando el usuario acerca el teléfono a la etiqueta NFC, se lanza automáticamente el fichaje
// Tiene las mismas reglas que el fichaje por GPS: no se puede entrar dos veces sin salir
public class FichajeNFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private FichajeViewModel viewModel;
    private String token;
    private TextView tvEstadoNFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichaje_nfc);

        token = getIntent().getStringExtra("TOKEN");
        tvEstadoNFC = findViewById(R.id.tvEstadoNFC);

        // Inicializo el adaptador NFC del dispositivo
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            // El dispositivo no tiene NFC
            Toast.makeText(this, "Este dispositivo no tiene NFC", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Activa el NFC en los ajustes del dispositivo", Toast.LENGTH_LONG).show();
        }

        // Creo el PendingIntent que se ejecutará cuando se detecte una etiqueta NFC
        // FLAG_MUTABLE es necesario a partir de Android 12
        pendingIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
        );

        // Creo el ViewModel para gestionar el fichaje (igual que en FicharActivity)
        viewModel = new ViewModelProvider(this).get(FichajeViewModel.class);

        // Observo el resultado del fichaje y actualizo la UI
        viewModel.getFichajeResult().observe(this, fichajeResponse -> {
            tvEstadoNFC.setText("✅ " + fichajeResponse.msg);
            Toast.makeText(this, fichajeResponse.msg, Toast.LENGTH_SHORT).show();
        });

        viewModel.getErrorMessage().observe(this, error ->
                tvEstadoNFC.setText("❌ " + error)
        );

        tvEstadoNFC.setText("Acerca el teléfono a la etiqueta NFC para fichar");
    }

    // Cuando la Activity vuelve al primer plano, activo el modo foreground del NFC
    // Esto hace que esta Activity tenga prioridad para recibir los tags NFC
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            IntentFilter[] filters = new IntentFilter[]{tagDetected};
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
        }
    }

    // Cuando la Activity pierde el foco, desactivo el modo foreground para no interferir con otras apps
    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    // Se llama cuando se detecta una etiqueta NFC mientras esta Activity está en primer plano
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // Se detectó un tag NFC — obtengo el Tag para verificar que es válido
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tag != null) {
                tvEstadoNFC.setText("🔄 Tag NFC detectado, fichando...");
                // Ficho sin coordenadas GPS (0,0) porque el fichaje NFC no requiere geolocalización
                // El servidor validará que es una petición NFC por el endpoint
                viewModel.fichar(token, 0.0, 0.0);
            }
        }
    }
}
