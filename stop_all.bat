@echo off
chcp 65001 >nul
title Fashion Store - Dung Toan Bo Tien Trinh
echo ======================================================================
echo           DANG DUNG TOAN BO HE THONG FASHION STORE
echo ======================================================================
echo.

echo [1/3] Dang ngat tien trinh Port 8080 (Backend)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080 " ^| findstr LISTENING') do (
    echo Dong PID %%a...
    taskkill /F /PID %%a /T >nul 2>&1
)

echo [2/3] Dang ngat tien trinh Port 5173 (Frontend)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173 " ^| findstr LISTENING') do (
    echo Dong PID %%a...
    taskkill /F /PID %%a /T >nul 2>&1
)

echo [3/3] Dang dong Ngrok Tunnel...
taskkill /F /IM ngrok.exe /T >nul 2>&1

echo.
echo [HOAN TAT] Da giai phong toan bo port 8080, 5173 va Ngrok!
pause
exit /b 0