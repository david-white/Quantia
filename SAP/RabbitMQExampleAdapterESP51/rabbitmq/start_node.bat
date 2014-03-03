
@echo off

if "x%ESP_HOME%" == "x" (
	echo "ESP_HOME is not set"
	exit /B 1
)

"%ESP_HOME%\cluster\examples\start_node.bat"

