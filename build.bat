@echo Off
call mvn clean package -DskipTests
docker build -t remek .
pause