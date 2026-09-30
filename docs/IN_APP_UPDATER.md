# 🔄 Actualizaciones Automáticas "In-App" (Fuera de Google Play)

Si distribuyes tu APK de manera independiente, necesitas construir tu propio sistema OTA (Over-The-Air) de actualizaciones. Cuando subas una nueva versión, la aplicación descargará el nuevo `.apk` e invitará al usuario a actualizarse automáticamente.

## Arquitectura de Actualización Independiente

### 1. El Servidor de Versiones (Remote Config)

Necesitas hospedar un archivo pequeñísimo (ej. `version.json`) en un servidor gratuito como **GitHub Pages, Gist, Firebase Remote Config o AWS S3**.

```json
{
   cd "E:/aplicaciones/kotlin-apps/alarmReminder2/app/build/outputs/apk/debug/"
  "latest_version_code": 2,
  "latest_version_name": "1.1.0",
  "download_url": "https://tuservidor.com/app-release-v1.1.0.apk",
  "release_notes": "Añadido CRUD de Usuarios y Notificaciones Nativas."
}
```

### 2. Comprobador de Versión (ViewModel)

Al abrir la App (en `MainActivity` o `LoginScreen`), haces una petición HTTP rápida (usando `Retrofit` o `Ktor`) para leer ese `version.json`.
Si `remote_version_code` es mayor que tu `BuildConfig.VERSION_CODE`, se muestra un Modal de "Nueva Actualización Disponible".

### 3. Descarga en Segundo Plano (DownloadManager)

Si el usuario acepta, usas el `DownloadManager` nativo de Android. Esto descargará el APK directamente en la carpeta de *Descargas* del teléfono con una notificación nativa de progreso.

### 4. Permisos Críticos (AndroidManifest.xml)

Tu app necesitará obligatoriamente estos permisos para poder descargar e iniciar una instalación externa:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

### 5. Lanzar el Instalador del Sistema (Intent)

Una vez el `DownloadManager` finaliza, se dispara un `BroadcastReceiver` que lanza la pantalla de instalación nativa de Android mediante un FileProvider:

```kotlin
val intent = Intent(Intent.ACTION_VIEW).apply {
    setDataAndType(apkUri, "application/vnd.android.package-archive")
    flags = Intent.FLAG_ACTIVITY_NEW_TASK
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}
context.startActivity(intent)
```

*(Nota: El teléfono de tu usuario o familiar le pedirá permiso para "Instalar aplicaciones desconocidas de esta fuente" la primera vez).*

---

## 🚀 ¿Qué nos falta para empaquetar el APK V1.0?

Revisando el estado actual de tu `run_safe.ps1` y el proyecto, la aplicación es 100% funcional. Si quieres, las actualizaciones automáticas podemos dejarlas para la `v2`. Para sacar el APK hoy mismo a producción solo faltaría:

1. **Firmar el APK (Keystore):**
   Actualmente tu APK es `app-debug.apk`. Para que sea instalable sin problemas y tenga integridad, deberíamos generar una llave criptográfica `.jks` (Keystore) para compilar en modo `Release`.
2. **Desactivar Reglas de Proguard (Minificación):**
   Asegurarnos de que el código no se corrompa al ofuscarse en Release.

¿Deseas que implementemos el sistema de actualizaciones automáticas (OTA) ahora mismo, o prefieres generar el Keystore de Producción para tener tu primer APK oficial?
