# 🚀 Arquitectura de Producción (v1.0.0)

## Resumen de Refactorización

La aplicación AlarmReminder2 ha evolucionado de un prototipo UI-Bound (temporizador anclado a la interfaz gráfica) a una aplicación de producción **System-Bound**.

### 1. Timers en Paralelo (LazyColumn)
Hemos reemplazado la vista central por una lista dinámica (`LazyColumn`).
Cada medicamento (`MedicineReminderEntity`) ahora posee su propio estado en base de datos:
- `isRunning`: Booleano para saber si el ciclo está activo.
- `targetAlarmTimeMillis`: Milisegundo exacto calculado en el futuro en el que debe sonar la alarma.

### 2. Integración de AlarmManager (Background Execution)
Se inyectó el uso nativo de `AlarmManager` con `setExactAndAllowWhileIdle`.
Esto significa que cuando presionas "Iniciar" en cualquier tarjeta, el ViewModel programa el Sistema Operativo Android para despertar la app en el futuro.
**Beneficio:** Puedes minimizar la aplicación, apagar la pantalla o cerrar los procesos, y la alarma sonará de todas formas gracias al `AlarmReceiver`.

### 3. Selector de Tonos (RingtonePicker)
Se integró `ActivityResultContracts.StartActivityForResult()` en Compose.
Ahora cada medicina tiene un botón "🎵 Tono" que abre el gestor de archivos de Android. La `URI` seleccionada se guarda en Room (`ringtoneUri`) y es inyectada en el `AlarmPlayerHelper` al momento de detonar la alarma.

## Próximos Pasos (Google Play)
1. Declarar los servicios Foreground (si deseas escalar a Alarmas en pantalla completa estilo despertador).
2. Probar en Android 14+ asegurando que el usuario otorgue el permiso explícito de `SCHEDULE_EXACT_ALARM` en configuraciones, lo cual actualmente está en un bloque `try-catch` de seguridad.
