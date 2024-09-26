database and spring
- docker build -t ingsis-permission-spring-api -f Dockerfile.multi .
- docker compose up -d db
- docker run --env-file .env -p 8083:8080 ingsis-permission-spring-api
- docker ps (to verify)

if any issues
- docker-compose logs db
- docker-compose logs ingsis-permission-spring-api (container name)