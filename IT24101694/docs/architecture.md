# RideLink architecture

```mermaid
flowchart TB
  A[Account Service :8081] --> ADB[(ridelink_account_db)]
  D[Driver & Vehicle Service :8082] --> DDB[(ridelink_driver_db)]
  R[Ride Management Service :8083] --> RDB[(ridelink_ride_db)]
  F[Fare & Payment Service :8084] --> FDB[(ridelink_fare_db)]
  R -->|REST: eligible drivers| D
  R -->|REST: final fare| F
  A -.->|issues shared-secret JWT| D
  A -.->|issues shared-secret JWT| R
  A -.->|issues shared-secret JWT| F
```

Each service owns only its database. IDs such as `accountId` and `rideId` are database-generated numbers, starting at 1 within each table. References between services are stored as numbers, not cross-service foreign keys. Ride assignment deliberately uses a simple explainable algorithm: Driver Service returns available, area-matching profiles ordered by creation time, and Ride Service chooses the first result.
