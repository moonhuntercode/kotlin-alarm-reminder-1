# 🩺 Registro de Resolución de Errores (Error Resolution Log)

Este documento centraliza los problemas y errores de compilación enfrentados durante el desarrollo de **AlarmReminder2** y las decisiones arquitectónicas tomadas para solucionarlos de manera nativa y profesional.

---

## 1. 🚨 Unresolved reference 'compose' (Lifecycle ViewModel)
**Contexto:** El linter mostraba error al intentar usar la función `viewModel()` proporcionada por la librería de Compose en `LoopTimerScreen.kt`.
**Causa:** Aunque la dependencia `androidx.lifecycle:lifecycle-viewmodel-compose` existía en Gradle, en muchos entornos locales falla la sincronización o la versión no es 100% compatible con el stack de KSP activo.
**Resolución:** 
Se purgó la dependencia externa y se refactorizó el código inyectando el ViewModel de manera nativa usando `ViewModelProvider`:
```kotlin
val factory = remember { LoopTimerViewModelFactory(dao) }
val viewModel: LoopTimerViewModel = remember {
    ViewModelProvider(context as ViewModelStoreOwner, factory)[LoopTimerViewModel::class.java]
}
```
*Impacto:* El código ahora compila instantáneamente en cualquier versión de Android sin requerir la macro de compose.

---

## 2. 🚨 Unresolved reference 'Icons' (Material Icons)
**Contexto:** Errores de importación en `ManageMedicinesDialog.kt` al intentar usar `Icons.Default.CheckCircle` y `Icons.Default.Delete`.
**Causa:** La librería `androidx.compose.material:material-icons-extended` es extremadamente pesada y no estaba incluida en `libs.versions.toml`.
**Resolución:** 
En lugar de forzar una descarga de +15MB para 2 íconos, se eliminaron los imports y se utilizaron *Text Emojis* nativos (`✅` y `🗑️`). 
*Impacto:* UI limpia, cero advertencias y menor peso del APK final.

---

## 3. ⚠️ Code Smells: CascadeIf & FunctionName
**Contexto:** El linter de Kotlin estándar alertaba sobre una cadena larga de `if-else` y sobre el uso de mayúsculas en las funciones de UI.
**Resolución:** 
- El `CascadeIf` fue transformado elegantemente en un bloque `when(status)`.
- Se añadió la etiqueta `@Suppress("FunctionName")` a los componentes `@Composable`.
*Impacto:* Respetamos la convención de Compose (Mayúscula inicial) sin que el linter nos marque errores falsos positivos.

---

## 4. 🚨 ILLEGAL_SUSPEND_FUNCTION_CALL (Corrutinas)
**Contexto:** Todo el DAO (`insert`, `delete`, `update`) falló en `LoopTimerViewModel` indicando que las funciones suspendidas requerían un bloque corrutina válido.
**Causa:** Al implementar el `AlarmManager`, se borraron accidentalmente los import de `kotlinx.coroutines.launch` y `kotlinx.coroutines.Dispatchers`. El compilador ya no entendía qué era `viewModelScope.launch` y lo asumía como un scope normal sincrónico.
**Resolución:** 
Se re-insertaron los imports correspondientes.
*Impacto:* Recuperación total de la capa asíncrona (Dispatchers.IO) para proteger el Hilo Principal de la UI (Main Thread).

---

## 5. ⚠️ ConvertLongToDuration (Legacy delay)
**Contexto:** Advertencia en la línea `delay(1000)` del ViewModel.
**Resolución:** 
Se importó `kotlin.time.Duration.Companion.milliseconds` y se migró a la sintaxis Typesafe: `delay(1000L.milliseconds)`.

---

## 🏗️ 6. Evolución Arquitectónica (De 1 Día a Multi-Días)
**Limitación Original:** El sistema estaba acoplado a la variable `dateString = today`, haciendo imposible generar tratamientos de 1 semana o 1 mes.
**Resolución:**
- **Entidad (Room):** Se purgó `dateString` y se crearon `durationDays: Int` y `endDateMillis: Long`.
- **DAO:** Se cambió la consulta de "Hoy" a un rango lógico: `WHERE endDateMillis >= currentTimeMillis`.
- **UI:** El modal `AddMedicineDialog` ahora expone un Slider dinámico que permite elegir de 1 a 30 días de duración del tratamiento. La meta diaria (ej: 4 tomas) se multiplica por los días en el ViewModel (`targetDoses * durationDays`) para saber exactamente cuándo el tratamiento ha finalizado de por vida.

---

## 🛡️ 7. Fallo Preventivo en el Pipeline de QA (Linting y Syntax)
**Contexto:** Al ejecutar por primera vez el script `.\run_safe.ps1`, el pipeline abortó la compilación (*BUILD FAILED*) debido a la etapa de *Android Lint*.
**Errores Detectados:**
1. `Unresolved reference 'getAllUsers'` y `deleteUser` en `AdminDashboardScreen.kt`.
2. `No parameter with name 'pinHash' found` en `UserEntity`.
3. `Syntax error: Expecting a top level declaration` en `AlarmPlayerHelper.kt` (llaves mal anidadas).
**Causa:**
Durante la refactorización profunda para inyectar el CRUD completo, se mandó a llamar a funciones de `UserDao` que no habían sido programadas, y un error de indentación masiva rompió las llaves `}` del singleton `AlarmPlayerManager`.
**Resolución:**
1. Se añadieron las consultas `@Query("SELECT * FROM users")` y `@Delete` en `UserDao.kt`.
2. Se corrigió el mapeo de variables en el constructor del DAO (`pin` vs `pinHash`).
3. Se sanearon las llaves de apertura/cierre en `AlarmPlayerHelper.kt`.
*Impacto:* El script de automatización (`run_safe.ps1`) demostró su inmenso valor. Detuvo en seco un *deploy* que de otra manera habría resultado en un APK corrupto e inoperable (Crash inmediato). Aprendizaje: **El Pipeline es la única fuente de verdad**.
