@echo off

if "x%ESP_HOME%" == "x" (
	echo "ESP_HOME is not set"
	exit /B 1
)

call "%~dp0set_example_env.bat"

"%ESP_HOME%\bin\esp_cluster_admin.exe" --uri=%ADAPTER_EXAMPLE_CLUSTER_URI% %ADAPTER_EXAMPLE_CLUSTER_ADMIN_CREDENTIALS% --stop_project --workspace-name=%ADAPTER_EXAMPLE_WORKSPACE_NAME% --project-name=%ADAPTER_EXAMPLE_PROJECT_NAME%

"%ESP_HOME%\bin\esp_cluster_admin.exe" --uri=%ADAPTER_EXAMPLE_CLUSTER_URI% %ADAPTER_EXAMPLE_CLUSTER_ADMIN_CREDENTIALS% --remove_project --workspace-name=%ADAPTER_EXAMPLE_WORKSPACE_NAME% --project-name=%ADAPTER_EXAMPLE_PROJECT_NAME%
