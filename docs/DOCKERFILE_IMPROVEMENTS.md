# Dockerfile Analysis & Improvements

## ✅ What Was Good in Your Original Dockerfile

1. ✅ **Multi-stage build** - Reduces final image size significantly
2. ✅ **Build caching** - Uses `--mount=type=cache` for Maven dependencies
3. ✅ **Layer optimization** - Copies POM first, then source code
4. ✅ **Skips tests** - Appropriate for Docker build (tests should run in CI/CD)
5. ✅ **Container-aware JVM** - Uses `-XX:+UseContainerSupport`
6. ✅ **Memory management** - Sets `-XX:MaxRAMPercentage=75.0`
7. ✅ **Timezone configuration** - Sets both system and JVM timezone
8. ✅ **Log directory** - Creates `/var/log/spring` for Logback

## ⚠️ Issues Fixed

### 1. **Using JDK Instead of JRE** ❌ → ✅
**Before:**
```dockerfile
FROM eclipse-temurin:21-jdk
```
**After:**
```dockerfile
FROM eclipse-temurin:21-jre
```
**Why:** JDK includes development tools (javac, jdb, etc.) that aren't needed in production. JRE is ~50% smaller and reduces attack surface.

**Savings:** ~200MB image size reduction

---

### 2. **Running as Root User** ❌ → ✅
**Before:** Application ran as root (default)

**After:**
```dockerfile
RUN groupadd -r spring && useradd -r -g spring spring
# ... 
RUN chown spring:spring /app/app.jar
USER spring
```
**Why:** Running as root violates security best practices. If the application is compromised, the attacker has root access to the container.

**Security Impact:** High - prevents privilege escalation attacks

---

### 3. **Missing Archived Logs Directory** ❌ → ✅
**Before:**
```dockerfile
RUN mkdir -p /var/log/spring
```
**After:**
```dockerfile
RUN mkdir -p /var/log/spring/archived && \
    chown -R spring:spring /var/log/spring
```
**Why:** Your `logback-spring.xml` archives logs to `/var/log/spring/archived/`. Without this directory, log rotation would fail.

---

### 4. **No Volume for Persistent Logs** ❌ → ✅
**Before:** No volume declaration

**After:**
```dockerfile
VOLUME ["/var/log/spring"]
```
**Why:** Without a volume, logs are lost when the container is recreated. Docker can now mount a host directory or named volume to persist logs.

**Usage:**
```bash
docker run -v /host/logs:/var/log/spring myapp
```

---

### 5. **No Health Check** ❌ → ✅
**Before:** No health check

**After:**
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8000/actuator/health || exit 1
```
**Why:** Docker/Kubernetes can now monitor application health and restart unhealthy containers automatically.

**Note:** Requires Spring Boot Actuator in your `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

---

### 6. **Wrong Timezone** ❌ → ✅
**Before:**
```dockerfile
ENV TZ=Asia/Tokyo
```
**After:**
```dockerfile
ENV TZ=Asia/Dili
```
**Why:** Timor-Leste uses `Asia/Dili` timezone, not `Asia/Tokyo`. This affects log timestamps and time-based operations.

---

### 7. **Wrong Spring Profile in Production Dockerfile** ❌ → ✅
**Before (Dockerfile):**
```dockerfile
-Dspring.profiles.active=staging
```
**After (Dockerfile):**
```dockerfile
-Dspring.profiles.active=prod
```
**Why:** Production Dockerfile should use `prod` profile, not `staging`.

---

## 📊 Comparison: Before vs After

| Feature | Before | After | Impact |
|---------|--------|-------|--------|
| Base Image | JDK (~450MB) | JRE (~250MB) | -200MB |
| User | root | spring (non-root) | 🔒 High security |
| Health Check | None | Configured | 🏥 Auto-recovery |
| Log Persistence | Lost on restart | Volume mounted | 💾 Data safety |
| Archived Logs | Missing dir | Created | 📁 Log rotation works |
| Timezone | Asia/Tokyo | Asia/Dili | 🕐 Correct timestamps |
| Security Score | Medium | High | ⭐⭐⭐⭐⭐ |

## 🚀 How to Use the Improved Dockerfiles

### Build Staging Image
```bash
docker build -f Dockerfile.staging -t lis-api:staging .
```

### Build Production Image
```bash
docker build -f Dockerfile -t lis-api:prod .
```

### Run with Log Persistence
```bash
# Using named volume (recommended)
docker run -d \
  -p 8000:8000 \
  -v lis-logs:/var/log/spring \
  --name lis-api \
  lis-api:staging

# Using host directory
docker run -d \
  -p 8000:8000 \
  -v /var/log/lis-api:/var/log/spring \
  --name lis-api \
  lis-api:staging
```

### Check Health Status
```bash
docker ps  # Shows health status
docker inspect lis-api | grep -A 5 Health
```

### View Logs
```bash
# Application logs
docker logs lis-api

# File logs
docker exec lis-api cat /var/log/spring/app.info.log
docker exec lis-api cat /var/log/spring/app.error.log
```

## 🔧 Additional Recommendations

### 1. Enable Actuator Health Endpoint
Add to `application-prod.yaml` and `application-staging.yaml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: when-authorized
```

### 2. Docker Compose Example
Create `docker-compose.staging.yml`:
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
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8000/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3

volumes:
  lis-logs:
```

### 3. Production Considerations

Add these environment variables in production:
```bash
-e JAVA_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/spring"
```

### 4. Log Rotation in Host
If using host volumes, configure logrotate on the host:
```bash
# /etc/logrotate.d/lis-api
/var/log/lis-api/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
}
```

## 📋 Pre-deployment Checklist

Before deploying, ensure:

- [ ] Spring Boot Actuator is in `pom.xml`
- [ ] Health endpoint is enabled in configuration
- [ ] Database connection is configured for staging/prod
- [ ] Environment variables are properly set
- [ ] Log volume is mounted (for persistence)
- [ ] Firewall allows port 8000
- [ ] SSL/TLS is configured (if using HTTPS)
- [ ] Database credentials are in environment variables (not hardcoded)

## 🎯 Summary

Your Dockerfile was **already good** with proper multi-stage build and caching. The improvements make it:

✅ **More Secure** - Non-root user, smaller attack surface  
✅ **More Reliable** - Health checks, automatic recovery  
✅ **More Maintainable** - Persistent logs, proper timezone  
✅ **Production-Ready** - All best practices implemented  

The Dockerfile is now **production-grade** and ready for deployment! 🚀

