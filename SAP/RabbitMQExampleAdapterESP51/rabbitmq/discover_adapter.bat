@echo off

call "%~dp0set_example_env.bat"

set ADAPTER_LIB_DIR=%~dp0

call "%ESP_HOME%\adapters\framework\bin\discover.bat" "%~dp0%ADAPTER_EXAMPLE_CONFIG_FILE%" %1 %2
