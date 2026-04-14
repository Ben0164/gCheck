@echo off
echo Building APK using Android Studio...
echo.
echo Opening Android Studio - please use Build > Build Bundle(s) / APK(s) > Build APK(s)
echo.
echo Project: %~dp0
echo.
echo This will bypass all Gradle command-line issues and use Android Studio's
echo internal build system which has proper Java configuration.
echo.
pause
start "" "C:\Program Files\Android\Android Studio\bin\studio64.exe" "%~dp0"
