@echo off
setlocal enabledelayedexpansion

rem Determine root directory
set SCRIPT_DIR=%~dp0
for %%I in ("%SCRIPT_DIR%..") do set ROOT_DIR=%%~fI

rem Load .env if it exists
if exist "%ROOT_DIR%\.env" (
  for /f "usebackq delims=" %%A in ("%ROOT_DIR%\.env") do (
    set "line=%%A"
    rem Skip comments and empty lines
    if not "!line!"=="" if not "!line:~0,1!"=="#" (
      for /f "tokens=1,* delims==" %%K in ("!line!") do set "%%K=%%L"
    )
  )
) else (
  echo .env not found at %ROOT_DIR%\.env — continuing with current environment 1>&2
)

cd "%ROOT_DIR%\backend"
call gradlew.bat bootRun

endlocal


