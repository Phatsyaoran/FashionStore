@echo off
chcp 65001 >nul
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.2_13"
title Fashion Store - He Thong Khoi Chay Tu Dong
echo ======================================================================
echo       HE THONG THUONG MAI DIEN TU BAN QUAN AO (FASHION STORE)
echo         Spring Boot 3 + MS SQL Server + Vue 3 + Tailwind CSS
echo ======================================================================
echo.

:: 0. DON DEP TIEN TRINH CU DE TRANH TRUNG CONG & ERR_NGROK_334
echo [0/5] Dang don dep tien trinh cu (Port 8080, 5173, Ngrok)...
taskkill /F /IM ngrok.exe /T >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080 " ^| findstr LISTENING') do taskkill /F /PID %%a /T >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173 " ^| findstr LISTENING') do taskkill /F /PID %%a /T >nul 2>&1

:: 1. KIEM TRA JAVA_HOME
if not exist "%JAVA_HOME%\bin\java.exe" (
    if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    ) else if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    )
)
echo [1/5] Su dung Java tai: %JAVA_HOME%

:: 2. KIEM TRA DATABASE (CHI CHAY SEED KHI CHUA CO DB FashionStoreDB)
echo [2/5] Dang kiem tra co so du lieu FashionStoreDB...
set "SQL_INSTANCE=localhost"
sqlcmd -S %SQL_INSTANCE% -U sa -P 123 -Q "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    set "SQL_INSTANCE=localhost\SQLEXPRESS"
    sqlcmd -S %SQL_INSTANCE% -U sa -P 123 -Q "SELECT 1;" >nul 2>&1
)

sqlcmd -S %SQL_INSTANCE% -U sa -P 123 -h -1 -Q "SET NOCOUNT ON; SELECT DB_ID('FashionStoreDB');" 2>nul | findstr /r "[0-9]" >nul
if %errorlevel% neq 0 (
    if exist "%~dp0database\schema_and_seed.sql" (
        echo Chua co FashionStoreDB, dang chay database\schema_and_seed.sql...
        sqlcmd -S %SQL_INSTANCE% -U sa -P 123 -i "%~dp0database\schema_and_seed.sql"
        echo [Thanh cong] Da khoi tao Schema va Seed Data!
    ) else (
        echo [Bo qua] Khong tim thay database\schema_and_seed.sql
    )
) else (
    echo [OK] FashionStoreDB da ton tai, giu nguyen du lieu hien co.
)
echo.

:: 3. KHOI DONG BACKEND SPRING BOOT (PORT 8080)
echo [3/5] Dang khoi dong Backend Spring Boot (Port 8080)...
set "MAVEN_CMD=mvnw.cmd"
if not exist "%~dp0backend-java\mvnw.cmd" set "MAVEN_CMD=mvn"
start "FashionStore Backend (8080)" cmd /k "cd /d "%~dp0backend-java" && %MAVEN_CMD% spring-boot:run"

:: 4. KHOI DONG FRONTEND VUE 3 (PORT 5173)
echo [4/5] Dang khoi dong Frontend Vue 3 Vite (Port 5173)...
start "FashionStore Frontend (5173)" cmd /k "cd /d "%~dp0frontend-vue" && npm run dev"

:: 5. KHOI DONG NGROK TUNNEL STATIC DOMAIN
echo [5/5] Dang khoi dong Ngrok Tunnel Static Domain (Port 5173)...
start "Ngrok Tunnel - FashionStore" cmd /k "ngrok http --url=dart-married-cabana.ngrok-free.dev 5173"
echo.

:: CHO BACKEND MO CONG 8080 XONG MOI MO TRINH DUYET (TOI DA 30 GIAY)
echo Dang cho Backend san sang tai cong 8080...
set /a COUNT=0
:WAIT_BACKEND
netstat -ano | findstr ":8080 " | findstr LISTENING >nul 2>&1
if %errorlevel% equ 0 goto READY
set /a COUNT+=1
if %COUNT% geq 30 goto READY
timeout /t 1 /nobreak >nul
goto WAIT_BACKEND

:READY
echo ======================================================================
echo    HE THONG DA SAN SANG!
echo    - Trang Chu (Local)  : http://localhost:5173
echo    - Ngrok Mobile/Web   : https://dart-married-cabana.ngrok-free.dev
echo    - Quan Tri Admin     : http://localhost:5173/admin (admin@fashion.vn / 123456)
echo    - Swagger OpenAPI UI : http://localhost:8080/swagger-ui.html
echo ======================================================================
start https://dart-married-cabana.ngrok-free.dev
exit /b 0