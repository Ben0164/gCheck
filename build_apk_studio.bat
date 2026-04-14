@echo off
echo Opening Android Studio to build APK...
echo Please use Build > Build Bundle(s) / APK(s) > Build APK(s) in Android Studio
echo.
echo Path: %~dp0
echo.
pause
start "" "C:\Program Files\Android\Android Studio\bin\studio64.exe" "%~dp0"
