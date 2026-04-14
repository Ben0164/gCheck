@echo off
echo ===================================================
echo gCheck Native Android APK Build Solution
echo ===================================================
echo.
echo Due to persistent Gradle Java version issues, 
echo this script uses Android Studio's internal build system.
echo.
echo Instructions:
echo 1. Android Studio will open with the project
echo 2. Wait for Gradle sync to complete
echo 3. Use Build > Build Bundle(s) / APK(s) > Build APK(s)
echo 4. APK will be generated in app/build/outputs/apk/debug/
echo.
echo Project: %~dp0
echo.
echo This bypasses all command-line Gradle issues.
echo ===================================================
echo.
pause
start "" "C:\Program Files\Android\Android Studio\bin\studio64.exe" "%~dp0"
