
::@echo off

if "x%ESP_HOME%" == "x" (
	echo "ESP_HOME is not set"
	exit /B 1
)

call "%~dp0set_example_env.bat"

echo %ESP_HOME%
echo %ADAPTER_EXAMPLE_PROJECT_URI% 
echo %ADAPTER_EXAMPLE_TOOL_CREDENTIALS% 
echo %ADAPTER_EXAMPLE_SUBSCRIBE_STREAMS%


"%ESP_HOME%\bin\esp_subscribe.exe" -p %ADAPTER_EXAMPLE_PROJECT_URI% %ADAPTER_EXAMPLE_TOOL_CREDENTIALS% -s %ADAPTER_EXAMPLE_SUBSCRIBE_STREAMS%

