# 🛡️ Pipeline de Calidad (QA) para Android/Kotlin

Al igual que en lenguajes robustos como Go, donde el flujo de `lint -> test -> build -> run` es ley, en el ecosistema de Android moderno podemos (y debemos) aplicar exactamente la misma filosofía arquitectónica para garantizar un código libre de *code smells* y errores en producción.

## 🛠️ Herramientas Instaladas

Hemos configurado tres capas de protección estática para este proyecto:

1. **Android Lint (`.\gradlew lint`)**
   - El linter nativo de Google.
   - Detecta problemas específicos de la plataforma (ej. falta de accesibilidad en XML, permisos faltantes en AndroidManifest, traducciones faltantes).

2. **Ktlint (`.\gradlew ktlintCheck`)**
   - El equivalente directo a `gofmt` y `golangci-lint` (fase de estilo).
   - Fuerza de manera estricta el estilo de código oficial de Kotlin, evitando debates de formato en el equipo.
   - *Autocorrección disponible mediante:* `.\gradlew ktlintFormat`

3. **Detekt (`.\gradlew detekt`)**
   - Análisis estático avanzado, el equivalente a `staticcheck` en Go.
   - Analiza *Code Smells*, funciones demasiado largas, complejidad ciclomática alta y posibles fugas de memoria o malos usos de Corrutinas/Arquitectura.

4. **JUnit / Instrumentados (`.\gradlew testDebugUnitTest`)**
   - Asegura la validez de la lógica de negocio pura (ej. cálculos de fechas en ViewModel, assertions de DAO).

---

## 🚀 Ejecutando el Pipeline Completo (Automatización)

Para no ejecutar estos comandos de forma manual uno por uno, hemos creado el script de PowerShell `run_safe.ps1` en la raíz del proyecto. Este script imita el comportamiento de los pipelines CI/CD y los scripts de tu entorno Go.

**Uso:**
Abre la terminal en Antigravity / VS Code y ejecuta:
```powershell
.\run_safe.ps1
```

**Comportamiento esperado:**
El script ejecutará secuencialmente cada etapa (Clean, Lint, Ktlint, Detekt, Tests y Build).
- ✅ Si una etapa pasa, muestra un `OK` verde y salta a la siguiente.
- ❌ Si **CUALQUIER** etapa falla (por ejemplo, Detekt encuentra una función enorme), el script lanza un error fatal rojo y detiene la compilación del APK, garantizando que código defectuoso nunca llegue al artefacto final.
