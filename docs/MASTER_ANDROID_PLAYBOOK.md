# 📘 Master Android Playbook & Informe Técnico 🚀

Este documento es el Manual Maestro y Registro de Inteligencia de Ingeniería para el desarrollo de proyectos Android/Kotlin modernos, utilizando una mentalidad defensiva de backend (Go-style QA). Si vuelves a iniciar un proyecto Android en el futuro, **lee este documento primero**.

---

## 🎯 1. Objetivos Generales y Estrategia

**Estrategia:** Traer la rigurosidad, el tooling y la automatización de la cultura de desarrollo de Go (`gofmt`, `golangci-lint`, `go test`, `go build`) al ecosistema moderno de Android/Kotlin.

**Objetivo:** Evitar los *crashes* en tiempo de ejecución (Run-time) empujando todas las validaciones al tiempo de compilación y análisis estático (Compile-time).

---

## 📊 2. Planificación, Hitos y Fases (Project Status)

| Fase | Hito (Milestone) | Estado | Detalles |
| :--- | :--- | :---: | :--- |
| **Fase 1** | Arquitectura y UI Base | ✅ Completado | Jetpack Compose, Navegación bloqueante (No-Back), Theming y Material 3. |
| **Fase 2** | Capa de Datos y Persistencia | ✅ Completado | Room Database, `Flow`, inyección limpia sin librerías pesadas como Hilt. |
| **Fase 3** | Funciones Core (Alarmas) | ✅ Completado | `MediaPlayer` continuo, `VibratorManager`, y Corrutinas para timers de fondo. |
| **Fase 4** | Pipeline QA y Seguridad | ✅ Completado | Ktlint, Detekt, firma `.jks`, automatización `.ps1`. |
| **Fase 5** | Distribución (OTA) | ⏳ Pendiente | Implementar `IN_APP_UPDATER` con descarga automática de GitHub/AWS. |

**Fase Actual:** **Fase 4 Finalizada (Producción V1).**
**Faltante Inmediato:** Nada crítico. Mantenimiento y pulido de UX visual para la Fase 5.

---

## 🛡️ 3. El Pipeline QA Defensivo (Go vs Kotlin)

Para mantener la calidad, forzamos un pipeline que impide que código basura llegue a compilarse.

```text
 ┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
 │                 │      │                 │      │                 │      │                 │
 │   1. FORMAT     ├─────►│   2. STATIC QA  ├─────►│    3. TESTS     ├─────►│   4. RELEASE    │
 │                 │      │                 │      │                 │      │                 │
 └─────────────────┘      └─────────────────┘      └─────────────────┘      └─────────────────┘
      (gofmt)               (golangci-lint)             (go test)               (go build)
     (ktlint)                (detekt / lint)       (testDebugUnitTest)      (assembleRelease)
```

### El script Automático (`run_safe.ps1`)
Es el corazón del proyecto. Un equivalente al `Makefile` de Go. Si el script truena, el código **NO** se envía.

```powershell
Run-Step "Lint Android" ".\gradlew lint"
Run-Step "Ktlint" ".\gradlew ktlintCheck"
# Detekt: (Deshabilitado en Java 25 localmente, requiere Java 11/17/21)
Run-Step "Unit Tests" ".\gradlew testDebugUnitTest"
Run-Step "Build Release APK" ".\gradlew assembleRelease"
```

---

## 🧠 4. Tutorial: Cómo Iniciar un Proyecto Android Limpio

Cuando empieces un nuevo proyecto desde cero en Android Studio, **nunca programes de inmediato**. Haz esto primero:

1. **Purga tu UI de Dependencias Inútiles:**
   No uses librerías que pesan 20MB por un ícono. Usa emojis de texto (`✅`, `🗑️`) o descarga el `.xml` (VectorAsset) del ícono exacto que necesitas.
2. **Configura el QA en Gradle Raíz (`build.gradle.kts`):**
   ```kotlin
   plugins {
       id("org.jlleitschuh.gradle.ktlint") version "12.1.0" apply false
       id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
   }
   ```
3. **Aplica los Plugins en el App (`app/build.gradle.kts`):**
   ```kotlin
   plugins {
       id("org.jlleitschuh.gradle.ktlint")
       id("io.gitlab.arturbosch.detekt")
   }
   ```
4. **Crea el Keystore de inmediato:**
   Ejecuta `keytool -genkeypair ...` para crear tu `release-key.jks`. Crea un `keystore.properties` y añádelo al `.gitignore`. ¡Nunca subas contraseñas a Git!

---

## 🩺 5. Aprendizaje de Errores (Post-Mortem Técnico)

- **El peligro del "Compose Magic":** Intentar usar funciones atadas fuertemente a macros compiladores de librerías externas (como `viewModel()`) puede hacer que el build falle misteriosamente. **Lección:** Usar siempre `ViewModelProvider` nativo.
- **Java 25 y el Análisis Estático:** Herramientas de AST (Abstract Syntax Tree) como `Detekt` (que usan el compilador embebido de Kotlin) tienen bugs críticos al parsear cadenas de versiones de Java de última generación (`25.0.4.1`). **Lección:** En máquinas Dev con entornos ultra-nuevos, aislar la herramienta o usar contenedores Docker con Java 17 LTS.
- **Minificación de Room:** Dejar `isMinifyEnabled = true` en Release destruye los Data Access Objects (DAOs) de Room porque Proguard cambia sus nombres a `a.b.c`, impidiendo que la base de datos SQL funcione. **Lección:** Deshabilitar Minify o configurar rigurosamente el `proguard-rules.pro`.

---

## 📖 6. Glosario de Términos (Android/Kotlin QA)

- **Lint (Android Lint):** Revisa archivos XML, manifiestos y recursos buscando problemas específicos de Android (ej: traducciones faltantes, íconos grandes).
- **Ktlint:** Validador de formato oficial. Equivalente directo a `gofmt`. No piensa en lógica, solo en espacios, llaves y sangrías.
- **Detekt:** Motor de análisis estático profundo. Revisa "Code Smells" (complejidad ciclomática, funciones enormes, ifs anidados). Equivalente a `staticcheck` o `golangci-lint`.
- **KSP (Kotlin Symbol Processing):** El motor que lee el código antes de compilar y autogenera código extra (como la base de datos de Room).
- **Keystore (.jks):** Archivo de bóveda criptográfica que contiene tu firma digital RSA. Un APK sin esto no es instalable en Producción.

---

## 📜 7. Changelog (Versión 1.0.0 Release)
**[1.0.0] - 2026-09-30**
- 🚀 **NUEVO:** Sistema completo de persistencia Room DB (UserEntity, MedicineReminderEntity).
- 🚀 **NUEVO:** Panel de Administrador de Usuarios con CRUD completo y control de roles.
- 🚀 **NUEVO:** Timers persistentes (Loop Timer) con alerta Sonora Continua y Vibración hasta que el usuario decida apagarlo.
- 🚀 **NUEVO:** Soporte multi-día para recordatorios de medicinas (1 a 30 días) calculando fechas exactas de término (endDateMillis).
- ⚙️ **QA:** Pipeline de automatización `run_safe.ps1` implementado con Ktlint, Lint, Tests y ensamblaje Release automatizado.
- 🔒 **SEGURIDAD:** Generación de Keystore aislado con firmas de Producción ocultas de VCS (Git).

*End of Document.*
