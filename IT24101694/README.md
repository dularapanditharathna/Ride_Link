# RideLink

RideLink is a backend-only university group project demonstrating four independently executable Spring Boot microservices for a ride-sharing workflow. It uses Java 21, Spring Boot 3, REST, Spring Security/JWT, JPA/Hibernate, MySQL, Bean Validation, Swagger/OpenAPI, JUnit 5, Mockito, and GitHub Actions.

## Services and ownership

| Member | Service | Port | Owned database |
|---|---|---:|---|
| Member 1 | Account Service | 8081 | `ridelink_account_db` |
| Member 2 | Driver & Vehicle Service | 8082 | `ridelink_driver_db` |
| Member 3 | Ride Management Service | 8083 | `ridelink_ride_db` |
| Member 4 | Fare & Payment Service | 8084 | `ridelink_fare_db` |

There are no cross-database queries, cross-service JPA relationships, or shared repositories. Ride Service calls Driver Service for eligible drivers and Fare Service for final fare calculation using REST. See [architecture](docs/architecture.md) and the [sequence diagram](docs/sequence-diagram.md).

## Prerequisites and startup

Install Java 21 and start a local MySQL Server. The MySQL `root` user (or the user supplied when running a service manually) must be able to create databases. No Maven installation is required: the included wrapper downloads Maven on first use.

Check `java --version` and `javac -version` in a new PowerShell window before startup; both must report 21. If Java 17 appears, put the JDK 21 `bin` directory ahead of older Java entries on `PATH`.

From the project root, start each service yourself with one command at a time. Run all commands in the **same PowerShell window**:

```powershell
.\scripts\run-one.ps1 account
.\scripts\run-one.ps1 driver
.\scripts\run-one.ps1 fare
.\scripts\run-one.ps1 ride
```

The first command prompts for the MySQL password and generates a shared JWT secret in memory. Each command opens only the selected service in its own log window. Wait until that service reports it has started before entering the next command. Start Account first to register and log in, then Driver and Fare before using the complete ride workflow. Keep the original PowerShell window open until you have started all the services you need; a new window would generate a different JWT secret. Close a service window to stop that service.

Each JDBC URL includes `createDatabaseIfNotExist=true`; Hibernate uses `ddl-auto=update`. The four databases and missing tables are created automatically as their services start. Check status with:

```powershell
.\scripts\check-services.ps1
```

### One-time reset after switching from UUIDs to numeric IDs

If you ran an older RideLink version that stored UUID IDs, stop all four services and run this once from the project root:

```powershell
.\scripts\reset-databases.ps1
```

The script asks you to type `RESET` and enter your MySQL password. It permanently deletes **only** `ridelink_account_db`, `ridelink_driver_db`, `ridelink_ride_db`, and `ridelink_fare_db`; all old accounts, rides, and payments are lost. It needs `mysql.exe` on `PATH` and uses `MYSQL_USERNAME` if set, or `root` otherwise. Then start services with `run-one.ps1` as above and register accounts again. Previously issued JWTs and saved Postman IDs will no longer be usable.

All entity IDs are now generated numeric values, such as `1`, `2`, and `3`. Numbering is separate for each table (an account and a ride can both have ID `1`), and deleted IDs may leave gaps. JSON requests use numbers without quotes, for example `{"rideId": 1, "amount": 1350.00, "paymentMethod": "CARD", "simulateFailure": false}`.

If you launch Maven directly instead of using `run-one.ps1`, set `MYSQL_PASSWORD` and the same Base64 `JWT_SECRET` in every service terminal first. A service fails to start when `JWT_SECRET` is missing.

## Swagger

- Account: http://localhost:8081/swagger-ui/index.html
- Driver: http://localhost:8082/swagger-ui/index.html
- Ride: http://localhost:8083/swagger-ui/index.html
- Fare: http://localhost:8084/swagger-ui/index.html

Use **Authorize** and enter the JWT (Swagger adds `Bearer`). Account registration and login are public; other APIs require authentication. Roles are `PASSENGER`, `DRIVER`, and `ADMIN`. BCrypt hashes all passwords, suspended users cannot log in, and only admins can change account status.

## Main APIs

- Account: passenger/driver registration, login, current profile/update, admin status update.
- Driver: profile, vehicles, location, availability, and deterministic eligible-driver lookup.
- Ride: create/get/list, accept, start, complete, and cancel with guarded state transitions.
- Fare: estimate/final calculation, simulated success/failure payment, payment lookup, receipt.

Fare calculation is configured as `base + distanceKm × perKm` (defaults LKR 300 + LKR 100/km). This is a simulation: there is no real payment provider. Setting `simulateFailure` to `true` exercises the safe failure path.

## Postman and demo

For step-by-step instructions for all four members, including every URL, example JSON, and required token, read the [Postman API testing guide](docs/postman-api-testing-guide.md).

Import both files in `postman/`, select the RideLink Local environment, then run requests in folder order. Collection scripts capture tokens and IDs. Follow [the demo workflow](docs/demo-workflow.md) for the complete scenario and negative cases.

## Tests and CI

Tests use Mockito and H2/test configuration and never require MySQL:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

The root build covers all modules. You can also run the same commands from an individual service using `..\mvnw.cmd`. GitHub Actions uses Java 21 and independently packages every service on pushes and pull requests; any failed matrix job fails CI.

## Git workflow

Keep `main` stable, integrate through `develop`, and use branches such as `feature/account-login`, `feature/driver-availability`, `feature/ride-lifecycle`, and `feature/fare-payment`. Prefer focused commits, pull requests, peer review, integration testing on `develop`, then merge a stable release to `main`.

## Security and limitations

Passwords, tokens, MySQL credentials, and JWT secrets are never committed. Main configuration requires `JWT_SECRET`; the startup script generates and supplies it automatically. This educational system uses one local MySQL server, synchronous REST, a simple first-eligible-driver algorithm, simulated coordinates, no maps, and no real payment gateway. It has no service discovery, messaging, containers, or frontend by design.
