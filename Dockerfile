FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache wget \
    && addgroup -S cinemaone \
    && adduser -S -G cinemaone cinemaone

WORKDIR /app
COPY --chown=cinemaone:cinemaone target/cinema-one-platform-*.jar /app/app.jar

USER cinemaone
EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=4s --start-period=35s --retries=6 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:-} -jar /app/app.jar"]
