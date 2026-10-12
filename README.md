# Tourist Microservice Load Balancing

A small, complete Spring Boot / Spring Cloud demo containing:
- **Eureka Server** — `http://localhost:8761`
- **Tourist Service** — registered as `TOURIST-SERVICE`
- **API Gateway** — `http://localhost:8080`, routing through Eureka and Spring Cloud LoadBalancer

## Versions

- Java 21 (Java 17+ can work with these Spring Boot versions; this project is configured for Java 21)
- Spring Boot 3.5.5
- Spring Cloud 2025.0.0
- Maven multi-module project

The root `pom.xml` manages the shared versions and contains the three modules. Import the root project, not just one subfolder.

## Import into Eclipse IDE

1. Extract `TouristMicroserviceLoadBalancing.zip` to a normal writable folder.
2. Open Eclipse and select **File → Import… → Maven → Existing Maven Projects → Next**.
3. For **Root Directory**, browse to the extracted `TouristMicroserviceLoadBalancing` folder (the folder containing the top-level `pom.xml`).
4. Ensure Eclipse detects the root project and all three modules (`eureka-server`, `tourist-service`, `api-gateway`), then click **Finish**.
5. Wait for Maven dependency downloads and workspace build to finish.
6. If required, right-click the root project → **Maven → Update Project…** → tick **Force Update of Snapshots/Releases** → **OK**.
7. In **Window → Preferences → Java → Installed JREs**, select a JDK 21 installation. Also check **Project → Properties → Java Compiler** uses Java 21.

Internet access is needed the first time Maven downloads dependencies.

## Start the applications in this order

Keep each application running in its own Eclipse Console. Do not run the `*Tests` class.

### 1. Eureka Server

- Open `eureka-server/src/main/java/com/example/eurekaserver/EurekaServerApplication.java`.
- Right-click → **Run As → Spring Boot App** (or **Java Application**).
- Wait until the console reports that Eureka Server has started.
- Open <http://localhost:8761>.

### 2. Tourist Service instance 1

- Open `tourist-service/src/main/java/com/example/touristservice/TouristServiceApplication.java`.
- Right-click → **Run As → Spring Boot App**.
- It uses port `8081` by default.
- Wait a short time for registration.

### 3. Tourist Service instance 2

Run a second copy of `TouristServiceApplication`:
1. Select **Run → Run Configurations…**.
2. Select the launch configuration for `TouristServiceApplication` and click **Duplicate** (or create another Java/Spring Boot launch configuration for the same main class).
3. In the second configuration, open **Arguments → Program arguments** and enter:
   `--server.port=8082`
4. Apply and Run.

Both instances use the same Eureka service ID (`TOURIST-SERVICE`) but distinct instance IDs and ports.

### 4. API Gateway

- Open `api-gateway/src/main/java/com/example/apigateway/ApiGatewayApplication.java`.
- Right-click → **Run As → Spring Boot App**.
- It runs on port `8080` and routes `/tourists` to `lb://TOURIST-SERVICE`.

## Test

### Eureka dashboard

Open <http://localhost:8761>. Under **Instances currently registered with Eureka**, wait for `TOURIST-SERVICE` to show **two instances**. Eureka registration can take several seconds.

### Direct service requests

- <http://localhost:8081/tourists>
- <http://localhost:8082/tourists>

Each response includes `instancePort`, so you can identify the responding instance.

### Gateway / load balancing

Open <http://localhost:8080/tourists> several times, or use PowerShell:

```powershell
1..10 | ForEach-Object {
  (Invoke-RestMethod http://localhost:8080/tourists).instancePort
}
```

The output should include both `8081` and `8082` over repeated requests. The precise sequence is not guaranteed; Spring Cloud LoadBalancer uses round-robin selection by default, and browser connection reuse or timing may affect what you observe.

Also test:
- `http://localhost:8080/actuator/health`
- `http://localhost:8081/health`
- `http://localhost:8082/health`

## Troubleshooting

- **Port already in use:** stop the other process using that port, or change `server.port`.
- **No Tourist Service instances:** confirm Eureka is running first; check each Tourist Service console for successful registration and confirm `defaultZone` is exactly `http://localhost:8761/eureka/`.
- **Gateway returns 503:** wait for registration, confirm both instances are UP in Eureka, and verify that the gateway console has no dependency/configuration errors.
- **Maven cannot resolve dependencies:** check internet access, then right-click root project → **Maven → Update Project…** → **Force Update of Snapshots/Releases**.
- **Wrong main class / `ClassNotFoundException`:** run the actual `*Application.java` under `src/main/java`, not a test class under `src/test/java`; then run **Project → Clean…** and Maven Update Project.
- **Eclipse says XML schema downloading is disabled:** that is an IDE XML validator setting. It is not itself a malformed POM. Confirm the first line of each POM is `<?xml version="1.0" encoding="UTF-8"?>` and use Maven Update Project.

## Endpoints summary

| Application | URL | Purpose |
|---|---|---|
| Eureka Server | `http://localhost:8761` | Service registry dashboard |
| Tourist Service 1 | `http://localhost:8081/tourists` | Direct instance test |
| Tourist Service 2 | `http://localhost:8082/tourists` | Direct instance test |
| API Gateway | `http://localhost:8080/tourists` | Load-balanced route |
