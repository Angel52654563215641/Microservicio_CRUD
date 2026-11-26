# HU-01 — Microservicio CRUD con MongoDB y calidad de código

Proyecto individual desarrollado con **Java 25, Spring Boot 3.5.6, Spring Data MongoDB, JUnit 5, Mockito, Testcontainers, JaCoCo y SonarQube**.

## 1. Requisitos

- JDK 25 configurado en `JAVA_HOME`.
- Maven 3.9 o superior.
- Docker Desktop iniciado (MongoDB, SonarQube y pruebas de integración con Testcontainers).
- Postman o cualquier cliente HTTP.

Verifica las herramientas:

```powershell
java -version
mvn -version
docker --version
```

## 2. Levantar los servicios

Desde la carpeta raíz del proyecto:

```powershell
docker compose up -d mongodb sonarqube
```

MongoDB queda disponible en `localhost:27017`. SonarQube queda disponible en `http://localhost:9000`.

La primera carga de SonarQube puede tardar unos minutos. Entra con `admin` / `admin` y cambia la contraseña cuando lo solicite.

## 3. Ejecutar el microservicio

```powershell
mvn spring-boot:run
```

La API queda en `http://localhost:8080`.

## 4. Endpoints CRUD

| Método | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | `/api/productos` | Crear producto | 201 Created |
| GET | `/api/productos` | Listar productos | 200 OK |
| GET | `/api/productos/{id}` | Consultar por ID | 200 OK |
| PUT | `/api/productos/{id}` | Actualizar producto | 200 OK |
| DELETE | `/api/productos/{id}` | Eliminar producto | 204 No Content |

### JSON para crear

```json
{
  "nombre": "Teclado mecánico",
  "sku": "TEC-001",
  "precio": 899.00,
  "existencia": 12
}
```

### JSON para actualizar

```json
{
  "nombre": "Teclado mecánico RGB",
  "sku": "TEC-001",
  "precio": 999.00,
  "existencia": 10
}
```

### Ejemplo de error 400

```json
{
  "nombre": "",
  "sku": "TEC-002",
  "precio": 0,
  "existencia": -1
}
```

El manejador global devuelve un JSON con `timestamp`, `status`, `error` y `message`. Los estados contemplados son 400, 404, 409 y 500.

## 5. Ejecutar pruebas y generar JaCoCo

Con Docker Desktop activo, ejecuta:

```powershell
mvn clean verify
```

Esto compila, ejecuta las pruebas unitarias e integración y genera el reporte en:

`target/site/jacoco/index.html`

El proyecto configura un umbral mínimo de 80% de cobertura de líneas y 70% de ramas. Si no se alcanza, la fase `verify` falla deliberadamente. Las pruebas de integración usan Testcontainers y levantan MongoDB en un contenedor temporal; si Docker no está disponible se omiten.

## 6. Análisis con SonarQube

Con SonarQube iniciado y el reporte JaCoCo generado:

1. Abre `http://localhost:9000`.
2. Inicia sesión y crea un proyecto con la clave `hu01-microservicio-nosql` (o utiliza esa misma clave).
3. Genera un token de análisis.
4. En PowerShell, desde la raíz del proyecto, ejecuta:

```powershell
$env:SONAR_TOKEN="PEGA_AQUI_TU_TOKEN"
mvn clean verify sonar:sonar "-Dsonar.token=$env:SONAR_TOKEN" "-Dsonar.host.url=http://localhost:9000"
```

5. Abre el proyecto en SonarQube y revisa Bugs, Vulnerabilities, Code Smells y Quality Gate.

**Importante:** el resultado del Quality Gate depende del análisis real ejecutado en tu equipo. No se afirma un resultado de SonarQube ni un porcentaje de cobertura sin ejecutar las herramientas.

## 7. Estructura

```text
HU01_Microservicio_NoSQL/
├── src/
│   ├── main/java/mx/utch/hu01/
│   │   ├── controller/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   ├── main/resources/application.properties
│   └── test/java/mx/utch/hu01/
├── evidencias/
├── sonar/
├── compose.yaml
├── pom.xml
├── README.md
└── Reporte_Tecnico_HU01.pdf
```

## 8. Repositorio Git

Para iniciar el repositorio y realizar commits:

```powershell
git init
git add .
git commit -m "feat: implementar microservicio CRUD con MongoDB"
git branch -M main
```

Crea un repositorio vacío en GitHub y conecta la URL que te proporcione:

```powershell
git remote add origin https://github.com/USUARIO/hu01-microservicio-nosql.git
git push -u origin main
```

Sustituye `USUARIO` por tu usuario real de GitHub. No se inventa un enlace de repositorio.

## 9. Consideraciones de entrega

- Agrega las capturas reales a `evidencias/`.
- Actualiza el reporte PDF con las evidencias obtenidas.
- Incluye el enlace real de tu repositorio en el PDF o en la plataforma.
- Adjunta el reporte JaCoCo generado y la captura del Quality Gate de SonarQube.
