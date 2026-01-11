# 🐳 Docker Quick Reference - LIS API

## 📦 Build Commands

```bash
# Staging
docker build -f Dockerfile.staging -t lis-api:staging .

# Production
docker build -f Dockerfile -t lis-api:prod .
```

## 🚀 Run Commands

### Basic Run
```bash
docker run -d -p 8000:8000 --name lis-api lis-api:staging
```

### With Persistent Logs (Recommended)
```bash
docker run -d \
  -p 8000:8000 \
  -v lis-logs:/var/log/spring \
  --name lis-api \
  lis-api:staging
```

### With Host Directory Logs
```bash
docker run -d \
  -p 8000:8000 \
  -v /var/log/lis-api:/var/log/spring \
  --name lis-api \
  lis-api:staging
```

### With Environment Variables
```bash
docker run -d \
  -p 8000:8000 \
  -v lis-logs:/var/log/spring \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/lisdb \
  -e SPRING_DATASOURCE_USERNAME=lisuser \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  --name lis-api \
  lis-api:staging
```

## 🔍 Monitoring Commands

### Check Health Status
```bash
# Via Docker
docker ps

# Via curl
curl http://localhost:8000/follow/health

# Detailed health info
curl http://localhost:8000/follow/health | jq
```

### View Logs
```bash
# Container stdout/stderr logs
docker logs lis-api

# Follow logs
docker logs -f lis-api

# Last 100 lines
docker logs --tail 100 lis-api

# File logs
docker exec lis-api cat /var/log/spring/app.info.log
docker exec lis-api tail -f /var/log/spring/app.error.log
```

### Check Container Stats
```bash
docker stats lis-api
```

## 🛠️ Management Commands

### Start/Stop/Restart
```bash
docker start lis-api
docker stop lis-api
docker restart lis-api
```

### Remove Container
```bash
docker rm -f lis-api
```

### Remove Image
```bash
docker rmi lis-api:staging
```

### Shell Access
```bash
docker exec -it lis-api sh
```

## 📊 Volume Management

### List Volumes
```bash
docker volume ls
```

### Inspect Volume
```bash
docker volume inspect lis-logs
```

### Backup Logs
```bash
docker run --rm \
  -v lis-logs:/source \
  -v $(pwd):/backup \
  alpine tar czf /backup/lis-logs-backup.tar.gz -C /source .
```

### Restore Logs
```bash
docker run --rm \
  -v lis-logs:/target \
  -v $(pwd):/backup \
  alpine tar xzf /backup/lis-logs-backup.tar.gz -C /target
```

### Remove Volume
```bash
docker volume rm lis-logs
```

## 🔧 Troubleshooting

### Container Won't Start
```bash
# Check logs
docker logs lis-api

# Check if port is already in use
lsof -i :8000
```

### Container is Unhealthy
```bash
# Check health status
docker inspect lis-api | grep -A 10 Health

# Check health endpoint
curl -v http://localhost:8000/follow/health

# Check application logs
docker logs --tail 50 lis-api
```

### Out of Disk Space
```bash
# Clean up unused containers, images, volumes
docker system prune -a

# Remove only stopped containers
docker container prune

# Remove unused volumes
docker volume prune
```

## 🐳 Docker Compose (Optional)

Create `docker-compose.yml`:
```yaml
version: '3.8'
services:
  lis-api:
    build:
      context: .
      dockerfile: Dockerfile.staging
    ports:
      - "8000:8000"
    volumes:
      - lis-logs:/var/log/spring
    environment:
      - SPRING_PROFILES_ACTIVE=staging
    restart: unless-stopped

volumes:
  lis-logs:
```

### Docker Compose Commands
```bash
# Start
docker-compose up -d

# Stop
docker-compose down

# View logs
docker-compose logs -f

# Rebuild
docker-compose up -d --build
```

## 📈 Production Deployment

### Pull Image from Registry
```bash
docker pull registry.example.com/lis-api:prod
```

### Tag and Push
```bash
docker tag lis-api:prod registry.example.com/lis-api:prod
docker push registry.example.com/lis-api:prod
```

### Run in Production
```bash
docker run -d \
  --name lis-api \
  -p 8000:8000 \
  -v /var/log/lis-api:/var/log/spring \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=${DB_URL} \
  -e SPRING_DATASOURCE_USERNAME=${DB_USER} \
  -e SPRING_DATASOURCE_PASSWORD=${DB_PASS} \
  --restart=unless-stopped \
  --memory=2g \
  --cpus=2 \
  lis-api:prod
```

## 🔐 Security Notes

- ✅ Application runs as non-root user (`spring`)
- ✅ Uses JRE (not JDK) for minimal attack surface
- ✅ Health checks enabled for monitoring
- ✅ Logs persisted outside container
- ⚠️ Always use secrets management for passwords in production
- ⚠️ Use HTTPS/TLS in production environments

## 📞 Health Endpoints

- **Health:** http://localhost:8000/follow/health
- **Application:** http://localhost:8000

## 📁 Important Paths

- **Application JAR:** `/app/app.jar`
- **Logs Directory:** `/var/log/spring/`
- **Info Log:** `/var/log/spring/app.info.log`
- **Error Log:** `/var/log/spring/app.error.log`
- **Archived Logs:** `/var/log/spring/archived/`

---

**Note:** Replace `lis-api:staging` with `lis-api:prod` for production deployments.

