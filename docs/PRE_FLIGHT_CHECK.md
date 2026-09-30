# 🛡️ Comprobaciones previas al despliegue (Pre-flight Checks)

Antes de generar el empaquetado final (`.apk` o `.aab`), es vital realizar un "Pre-flight Check" para asegurarnos de que el código no solo compila, sino que está libre de advertencias y *Code Smells*.

Aquí tienes las 2 maneras de hacerlo dependiendo de la herramienta que uses.

---

## 1. Validación en Android Studio (Recomendado)

Android Studio posee la interfaz más potente para auditorías de código (Linting).

1. **Sincronización Total:**
   - Ve a **File > Sync Project with Gradle Files** (o pulsa el ícono del elefante en la barra superior). 
   - *Por qué:* Esto descarga cualquier dependencia y mapea las funciones, previniendo errores falsos de `UNRESOLVED_REFERENCE` (como nos pasó con las Corrutinas).
2. **Reconstrucción (Rebuild):**
   - Ve a **Build > Rebuild Project**. 
   - *Por qué:* Borra la caché y compila desde cero. Si pasa este punto, no tienes errores fatales de sintaxis.
3. **Auditoría de Linter Completa:**
   - Ve a **Code > Inspect Code...**
   - Elige **"Whole project"** (Todo el proyecto) y dale a **OK**.
   - *Por qué:* Se abrirá un panel de `Problems / Inspections` abajo. Corrige los *Warnings* (amarillo) y *Errors* (rojo). El objetivo es que la pestaña esté vacía antes del Deploy.

---

## 2. Validación en VS Code / Antigravity (Terminal)

Si estás codificando desde VS Code o usando Antigravity IDE y prefieres los comandos, delega el trabajo al motor interno de Gradle.

**1. Simulación de Compilación Rápida:**
Ejecuta esto en la terminal (Powershell/CMD en Windows). Revisa que todo esté correcto sin generar el APK pesado:
```bash
.\gradlew assembleDebug --dry-run
```
*Si esto falla, Gradle te lanzará el error exacto (Ej: Línea 45, Unresolved Reference).*

**2. Ejecución del Linter Nativo:**
Corre la inspección de calidad por consola:
```bash
.\gradlew lint
```
Al finalizar, Gradle generará un informe visual y súper detallado en la ruta:
`app\build\reports\lint-results.html`

*Ábrelo en Chrome/Edge y podrás ver los mismos warnings que verías en Android Studio.*
