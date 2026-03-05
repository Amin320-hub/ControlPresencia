// =====================================================================
// DEPENDENCIAS PARA AÑADIR EN build.gradle (Module: app)
// Añade estas líneas dentro del bloque dependencies { }
// =====================================================================

dependencies {
    // Retrofit — para las llamadas a la API REST
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // Google Play Services Location — para obtener la ubicación GPS
    implementation 'com.google.android.gms:play-services-location:21.0.1'

    // Google Maps — para la pantalla del mapa de la empresa (solo admin)
    implementation 'com.google.android.gms:play-services-maps:18.1.0'

    // WorkManager — para programar las notificaciones de fichaje
    implementation 'androidx.work:work-runtime:2.9.0'

    // JWT Decode — para leer el rol del usuario desde el token JWT sin llamar al servidor
    implementation 'com.auth0.android:jwtdecode:2.0.2'

    // ViewModel y LiveData — para la arquitectura MVVM
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.6.2'
}

// =====================================================================
// PERMISOS PARA AÑADIR EN AndroidManifest.xml (dentro de <manifest>)
// =====================================================================

/*
    <!-- Permisos de localización GPS para el fichaje -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <!-- Permiso NFC para el fichaje por etiqueta NFC -->
    <uses-permission android:name="android.permission.NFC" />
    <uses-feature android:name="android.hardware.nfc" android:required="false" />

    <!-- Permisos de internet para las llamadas a la API REST -->
    <uses-permission android:name="android.permission.INTERNET" />

    <!-- Permiso para mostrar notificaciones (obligatorio desde Android 13) -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
*/

// =====================================================================
// ACTIVITIES QUE HAY QUE DECLARAR EN AndroidManifest.xml
// Añade estas líneas dentro del bloque <application>
// =====================================================================

/*
    <activity android:name=".FicharActivity" />
    <activity android:name=".IncidenciasActivity" />
    <activity android:name=".MisRegistrosActivity" />
    <activity android:name=".HorasExtraActivity" />
    <activity android:name=".CambiarPasswordActivity" />
    <activity android:name=".AdminActivity" />
    <activity android:name=".MapaEmpresaActivity" />
    <activity android:name=".FichajeNFCActivity">
        <!-- Este intent-filter hace que esta Activity se abra automáticamente cuando se detecta un tag NFC -->
        <intent-filter>
            <action android:name="android.nfc.action.TAG_DISCOVERED" />
            <category android:name="android.intent.category.DEFAULT" />
        </intent-filter>
    </activity>
*/

// =====================================================================
// CLAVE DE GOOGLE MAPS — en AndroidManifest.xml dentro de <application>
// Necesitas obtener tu propia API Key en: https://console.cloud.google.com
// =====================================================================

/*
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="TU_CLAVE_DE_GOOGLE_MAPS_AQUI" />
*/
