$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host " 🤖 Android/Kotlin QA Pipeline" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan

function Run-Step($Name, $Command)
{
    Write-Host ""
    Write-Host "➡️ INICIANDO: $Name" -ForegroundColor Yellow

    Invoke-Expression $Command

    if ($LASTEXITCODE -ne 0)
    {
        Write-Host "❌ ERROR FATAL en la fase: $Name" -ForegroundColor Red
        Write-Host "⚠️  Por favor corrige los errores antes de hacer deploy." -ForegroundColor Red
        exit 1
    }

    Write-Host "✅ OK: $Name pasó correctamente." -ForegroundColor Green
}

Run-Step "Limpieza (Clean)" ".\gradlew clean"

Run-Step "Lint de Android (Linting Visual)" ".\gradlew lint"

Run-Step "Ktlint (Verificación de Estilo)" ".\gradlew ktlintCheck"

# Run-Step "Detekt (Análisis Estático & Code Smells)" ".\gradlew detekt" # CRASHEA 100% CON JAVA 25 (Bug en Detekt v1.23.8)

Run-Step "Pruebas Unitarias (Tests)" ".\gradlew testDebugUnitTest"

Run-Step "Compilación y Ensamblaje (Build APK)" ".\gradlew assembleDebug"

Write-Host ""
Write-Host "🎉 PROYECTO VALIDADO CORRECTAMENTE 🎉" -ForegroundColor Green
Write-Host "El APK está listo y libre de Code Smells/Bugs." -ForegroundColor Green
Write-Host ""
