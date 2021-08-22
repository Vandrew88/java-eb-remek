@echo OFF
docker run --name remek -it --rm -p 8080:8080 remek java -jar remek.jar remek
pause