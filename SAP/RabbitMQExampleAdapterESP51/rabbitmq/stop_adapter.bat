@echo off 

if "x%ESP_HOME%" == "x" (
	echo "ESP_HOME is not set"
	exit /B 1
)

call "%~dp0set_example_env.bat"

set ADAPTER_LIB_DIR=%~dp0

call "%ESP_HOME%\adapters\framework\bin\stop.bat" "%~dp0%ADAPTER_EXAMPLE_CONFIG_FILE%"
