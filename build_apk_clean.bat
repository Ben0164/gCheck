@echo off
echo ===================================================
echo gCheck Native Android APK Build Solution
echo ===================================================
echo.
echo This script creates a clean native Android build
echo completely bypassing all Flutter plugin issues.
echo.
echo Instructions:
echo 1. Android Studio will open with the project
echo 2. Wait for Gradle sync to complete (may take a few minutes)
echo 3. Use Build > Build Bundle(s) / APK(s) > Build APK(s)
echo 4. APK will be generated in app/build/outputs/apk/debug/
echo.
echo Project: %~dp0
echo.
echo This solution:
echo - Completely removes Flutter plugin cache references
echo - Uses Java 11 compatibility (matching build.gradle.kts)
echo - Disables Gradle caching to avoid corrupted cache issues
echo - Uses Android Studio's internal build system
echo ===================================================
echo.
pause
start "" "C:\Program Files\Android\Android Studio\bin\studio64.exe" "%~dp0"
