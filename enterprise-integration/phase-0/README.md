# Phase 0
## Prerequisites
```text
PS C:\Users\SabharwalR> systeminfo | findstr /B /C:"OS Name" /C:"OS Version"
OS Name:                   Microsoft Windows 11 Enterprise
OS Version:                10.0.22631 N/A Build 22631

PS C:\Users\SabharwalR> java -version
openjdk version "21.0.9" 2025-10-21 LTS
OpenJDK Runtime Environment Temurin-21.0.9+10 (build 21.0.9+10-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.9+10 (build 21.0.9+10-LTS, mixed mode, sharing)

PS C:\Users\SabharwalR> javac -version
javac 21.0.9

PS C:\Users\SabharwalR> gradle -v

------------------------------------------------------------
Gradle 9.3.1
------------------------------------------------------------

Build time:    2026-01-29 14:15:01 UTC
Revision:      44f4e8d3122ee6e7cbf5a248d7e20b4ca666bda3

Kotlin:        2.2.21
Groovy:        4.0.29
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  21.0.9 (Eclipse Adoptium 21.0.9+10-LTS)
Daemon JVM:    C:\Users\SabharwalR\.jdks\temurin-21.0.9 (no Daemon JVM specified, using current Java home)
OS:            Windows 11 10.0 amd64

PS C:\Users\SabharwalR> git --version
git version 2.52.0.windows.1

PS C:\Users\SabharwalR> docker --version
Docker version 29.2.1, build a5c7197

PS C:\Users\SabharwalR> docker compose version
Docker Compose version v5.0.2

PS C:\Users\SabharwalR> docker ps
CONTAINER ID   IMAGE                   COMMAND                  CREATED          STATUS         PORTS                                                                                          NAMES
a398962dbe93   rmohr/activemq:5.15.9   "/bin/sh -c 'bin/act…"   10 seconds ago   Up 9 seconds   0.0.0.0:8161->8161/tcp, [::]:8161->8161/tcp, 0.0.0.0:61616->61616/tcp, [::]:61616->61616/tcp   activemq
61d8d4532efc   postgres                "docker-entrypoint.s…"   3 weeks ago      Up 3 days      0.0.0.0:5432->5432/tcp, [::]:5432->5432/tcp                                                    postgres
PS C:\Users\SabharwalR>
```
- ActiveMQ: `docker-compose up -d`
