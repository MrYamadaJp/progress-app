Deploying progress-app to Raspberry Pi (24/7)

Overview
- Package as WAR and deploy to Tomcat 9 (Servlet 4, javax.*) on Raspberry Pi (ARM).
- App has login (session-based), DB via JNDI DataSource, and health endpoint.

Prereqs on Raspberry Pi
- OS: Raspberry Pi OS (Bullseye/Bookworm) or Ubuntu Server for ARM.
- Java: OpenJDK 17 (apt install openjdk-17-jre-headless).
- Tomcat: Apache Tomcat 9.x (apt or tarball). Ensure JAVA_HOME set.
- DB: MariaDB/MySQL reachable from Pi (or use remote DB). Create DB/user.

Build (on dev machine)
- mvn -q -DskipTests package
- Artifact: target/progress-app-0.0.1-SNAPSHOT.war

Tomcat setup (on Pi)
1) Place WAR
   - Copy WAR to TOMCAT_HOME/webapps/progress-app.war
   - Tomcat will auto-extract to webapps/progress-app/

2) Configure JNDI DataSource
   - Edit webapps/progress-app/META-INF/context.xml:
     - driverClassName: org.mariadb.jdbc.Driver
     - url: jdbc:mariadb://HOST:3306/DB?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
     - username/password: set to your DB creds

3) App auth credentials
   - Default is admin/changeit (web.xml). Override via env vars on Tomcat:
     - AUTH_USER=youruser
     - AUTH_PASS=yourpass
   - For systemd, set Environment= lines (see below).

4) Session & security
   - App sets HttpOnly session cookie; enable HTTPS on reverse proxy (nginx) or Tomcat connector.
   - When HTTPS is enforced, set secure cookies at container level or flip <secure>true</secure> in web.xml (requires HTTPS).

5) Health endpoint
   - GET http://{host}:8080/progress-app/healthz -> ok (db: unavailable) if JNDI unreachable, ok if DB reachable.

systemd service (example)
[Unit]
Description=Tomcat 9
After=network.target

[Service]
Type=forking
Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64
Environment=AUTH_USER=admin
Environment=AUTH_PASS=changeit
Environment=CATALINA_OPTS=-Xms256m -Xmx512m -Dfile.encoding=UTF-8
ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh
User=tomcat
Group=tomcat
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target

Hardening tips
- Run Tomcat as non-root user, restrict permissions.
- Put Tomcat behind nginx with HTTPS (Let’s Encrypt certbot).
- Set SameSite for session cookie at proxy, add security headers.
- Enable access/log rotation (logrotate or Tomcat config).

Troubleshooting
- 403 on login POST: ensure AuthFilter allows /login and index.jsp.
- 404 for JDBC driver: ensure mariadb-java-client present and Tomcat can load it (in app WEB-INF/lib via Maven or Tomcat/lib).
- DB connection errors: validate JNDI in META-INF/context.xml and DB grants.

