# RideLink API testing guide for beginners

This guide shows how each of the four members can test their service in Postman. You do not need to know Java to follow it. Test Member 1 first because its login API provides the tokens required by the other services.

## Before you send requests

1. Start MySQL and the services you need. To test the complete ride, run all four services using the commands in the [README](../README.md#prerequisites-and-startup). Start them from the **same original PowerShell window** so they share the same JWT secret.
2. In Postman, create a request, select its method (`GET`, `POST`, `PUT`, or `PATCH`), paste the full URL, and click **Send**.
3. For a request with JSON, select **Body → raw → JSON** and paste the example JSON. Postman should send `Content-Type: application/json`. A request marked **No body** needs nothing in its Body tab.
4. After login, copy the entire `token` value from the response. It is a long text string, often beginning with `eyJ`. It is **not** the numeric `accountId`. For protected requests, select **Authorization → Bearer Token** and paste only the token into the Token box. Postman adds the `Bearer` prefix for you. Do not put the token in the JSON body.
5. Save a **passenger token** and a **driver token** separately. Each token identifies the account that logged in. A passenger token cannot perform driver-only actions.
6. IDs such as `1` in the URLs and JSON below are examples. Copy the real `accountId`, ride `id`, vehicle `id`, and payment `id` returned by *your* requests. They are separate numbers and may not match each other. JSON numeric IDs have no quotes: `"rideId": 1`.

`/me` means “the account or driver profile identified by my token.” You do not add your ID to a `/me` URL.

| Member | Service | Base URL |
|---|---|---|
| 1 | Account | `http://localhost:8081` |
| 2 | Driver & Vehicle | `http://localhost:8082` |
| 3 | Ride Management | `http://localhost:8083` |
| 4 | Fare & Payment | `http://localhost:8084` |

## Member 1 — Account Service

**How it works:** Register accounts, log in to receive tokens, then use a token to read or update the logged-in account. Member 1 can test registration and login with only the Account Service running.

### 1. Register a passenger

- **Method:** `POST`
- **URL:** `http://localhost:8081/api/auth/register/passenger`
- **Token:** None; this is public.
- **Body:**

```json
{
  "firstName": "Nimal",
  "lastName": "Perera",
  "email": "passenger@ridelink.test",
  "password": "Password123",
  "phone": "0771234567"
}
```

The response contains the new passenger account's numeric `id`. You cannot register the same email twice.

### 2. Register a driver

- **Method:** `POST`
- **URL:** `http://localhost:8081/api/auth/register/driver`
- **Token:** None; this is public.
- **Body:**

```json
{
  "firstName": "Sunil",
  "lastName": "Silva",
  "email": "driver@ridelink.test",
  "password": "Password123",
  "phone": "0712345678"
}
```

Use a different email from the passenger account.

### 3. Log in as the passenger and driver

- **Method:** `POST`
- **URL:** `http://localhost:8081/api/auth/login`
- **Token:** None; this is public.
- **Passenger body:**

```json
{
  "email": "passenger@ridelink.test",
  "password": "Password123"
}
```

Send a second request to the **same URL** with `"email": "driver@ridelink.test"` and the driver's password. Each response has a `token`, numeric `accountId`, email, and role. Save both tokens and both account IDs. The passenger and driver tokens are different.

### 4. Get your own account

- **Method:** `GET`
- **URL:** `http://localhost:8081/api/accounts/me`
- **Token:** Passenger **or** driver token; the response shows the account that owns that token.
- **Body:** No body.

Try the request once with each token to see the difference.

### 5. Update your own account

- **Method:** `PUT`
- **URL:** `http://localhost:8081/api/accounts/me`
- **Token:** Passenger **or** driver token; it updates that token owner's account.
- **Body:**

```json
{
  "firstName": "Nimal",
  "lastName": "Perera",
  "phone": "0777654321"
}
```

This example is for the passenger token. For the driver token, enter that driver's name and phone.

### 6. Change an account's status (admin-only test)

- **Method:** `PATCH`
- **URL:** `http://localhost:8081/api/accounts/1/status`
- **Token:** **ADMIN token only.** Replace `1` with the account ID to change.
- **Body to suspend:**

```json
{
  "status": "SUSPENDED"
}
```

Send `{"status":"ACTIVE"}` to allow new logins again. A suspended account cannot log in again. An already issued token is not immediately revoked by the current implementation. There is no public admin-registration API, so skip this test unless an admin account has been set up separately. A passenger or driver token returns `403 Forbidden` here.

**Member 1 has no DELETE URL.**

## Member 2 — Driver & Vehicle Service

**Before testing:** Register and log in as a **driver** in Member 1. Keep that driver's token. All Member 2 requests below use the **driver token**, except the eligible-drivers GET, which accepts any valid login token.

**How it works:** A driver creates a profile, attaches vehicles, sets location and availability, then appears in eligible-driver searches for their service area. The numeric driver profile `id`, the driver's account `accountId`, and a vehicle `id` are different values.

### 1. Create the driver profile

- **Method:** `POST`
- **URL:** `http://localhost:8082/api/drivers/profile`
- **Token:** Driver token.
- **Body:**

```json
{
  "serviceArea": "Colombo"
}
```

The response contains the profile `id`, the driver's `accountId`, and `"available": false`. Create only one profile for each driver account.

### 2. Get your driver profile

- **Method:** `GET`
- **URL:** `http://localhost:8082/api/drivers/me`
- **Token:** Driver token.
- **Body:** No body.

### 3. Change your service area

- **Method:** `PUT`
- **URL:** `http://localhost:8082/api/drivers/me`
- **Token:** Driver token.
- **Body:**

```json
{
  "serviceArea": "Negombo"
}
```

The remaining examples use `Negombo` so the area matches the ride in Member 3.

### 4. Add a vehicle

- **Method:** `POST`
- **URL:** `http://localhost:8082/api/drivers/vehicles`
- **Token:** Driver token.
- **Body:**

```json
{
  "registrationNumber": "WP-CAB-1234",
  "make": "Toyota",
  "model": "Aqua",
  "color": "White",
  "vehicleType": "CAR"
}
```

Save the vehicle `id` returned in the response. Allowed types are `CAR`, `VAN`, `MOTORBIKE`, and `TUK_TUK`.

### 5. List your vehicles

- **Method:** `GET`
- **URL:** `http://localhost:8082/api/drivers/vehicles`
- **Token:** Driver token.
- **Body:** No body.

### 6. Update a vehicle

- **Method:** `PUT`
- **URL:** `http://localhost:8082/api/drivers/vehicles/1`
- **Token:** Driver token. Replace `1` with the **vehicle ID** returned in step 4.
- **Body:**

```json
{
  "registrationNumber": "WP-CAB-1234",
  "make": "Toyota",
  "model": "Aqua",
  "color": "Blue",
  "vehicleType": "CAR"
}
```

This example changes the vehicle color. The driver cannot update another driver's vehicle.

### 7. Set your location

- **Method:** `PUT`
- **URL:** `http://localhost:8082/api/drivers/location`
- **Token:** Driver token.
- **Body:**

```json
{
  "latitude": 7.2086,
  "longitude": 79.8358
}
```

Latitude must be between `-90` and `90`; longitude between `-180` and `180`.

### 8. Become available for rides

- **Method:** `PATCH`
- **URL:** `http://localhost:8082/api/drivers/availability`
- **Token:** **Driver token, not an admin token.** The driver changes their own availability.
- **Body:**

```json
{
  "available": true
}
```

Send `{"available":false}` later to stop appearing in eligible-driver results.

### 9. Find eligible drivers

- **Method:** `GET`
- **URL:** `http://localhost:8082/api/drivers/eligible?serviceArea=Negombo`
- **Token:** Any valid passenger or driver token.
- **Body:** No body.

Your driver should appear if its service area is `Negombo` and availability is `true`. The Ride Service uses this list to assign an available driver. Set availability to `false` and send this GET again to see the driver disappear.

**Member 2 has no DELETE URL.**

## Member 3 — Ride Management Service

**Before testing:** Have a passenger token and a driver token from Member 1. In Member 2, make a driver in `Negombo` available. Keep the Fare Service running too; ride completion calls it. If multiple drivers are available, the first eligible driver is assigned, so check `driverAccountId` in the ride response before using a driver token.

**How it works:** A passenger requests a ride. The Ride Service asks Member 2 for available drivers and assigns one. That assigned driver accepts, starts, and completes the ride. Completion asks Member 4 to calculate the final fare.

### 1. Create a ride

- **Method:** `POST`
- **URL:** `http://localhost:8083/api/rides`
- **Token:** Passenger token.
- **Body:**

```json
{
  "pickup": "Negombo Station",
  "destination": "Katunayake Airport",
  "serviceArea": "Negombo",
  "distanceKm": 10.5,
  "estimatedFare": 1350.00
}
```

The response should contain `"status": "ASSIGNED"`, a numeric ride `id`, and `driverAccountId`. Save the ride ID. If no available driver matches the service area, the request fails and no ride is created.

### 2. Get one ride

- **Method:** `GET`
- **URL:** `http://localhost:8083/api/rides/1`
- **Token:** That ride's passenger token **or** assigned driver's token. Replace `1` with the ride ID.
- **Body:** No body.

Send this GET after each later step to see the status change.

### 3. List my rides

- **Method:** `GET`
- **URL:** `http://localhost:8083/api/rides/my`
- **Token:** Passenger token to see rides they requested, or driver token to see rides assigned to them.
- **Body:** No body.

### 4. Accept the ride

- **Method:** `POST`
- **URL:** `http://localhost:8083/api/rides/1/accept`
- **Token:** **Assigned driver's token.** Replace `1` with the ride ID.
- **Body:** No body.
- **Result:** `ASSIGNED` → `ACCEPTED`.

### 5. Start the ride

- **Method:** `POST`
- **URL:** `http://localhost:8083/api/rides/1/start`
- **Token:** Assigned driver's token.
- **Body:** No body.
- **Result:** `ACCEPTED` → `IN_PROGRESS`.

### 6. Complete the ride

- **Method:** `POST`
- **URL:** `http://localhost:8083/api/rides/1/complete`
- **Token:** Assigned driver's token.
- **Body:** No body.
- **Result:** `IN_PROGRESS` → `COMPLETED`, with a `finalFare` in the response. The Fare Service must be running.

Do these actions in order: **create → accept → start → complete**. Calling `start` before `accept`, for example, is an invalid status transition.

### 7. Cancel a different ride

- **Method:** `POST`
- **URL:** `http://localhost:8083/api/rides/2/cancel`
- **Token:** That ride's passenger token or assigned driver's token. Replace `2` with the **new** ride ID.
- **Body:** No body.

To test cancellation, create a second ride and cancel it before completion. Do not try to cancel the completed ride from step 6.

**Member 3 has no PUT, PATCH, or DELETE URLs.** The POST action URLs change the ride's status.

## Member 4 — Fare & Payment Service

**Before testing:** Have the passenger and driver tokens from Member 1. For a realistic end-to-end payment, complete a ride in Member 3 and use its real ride ID and final fare.

**How it works:** Fare calculation uses the configured base fare plus distance times the per-kilometre rate. With the default settings, `300 + 10.5 × 100 = 1350.00`. Payment is simulated; no real card is charged.

### 1. Estimate a fare

- **Method:** `POST`
- **URL:** `http://localhost:8084/api/fares/estimate`
- **Token:** Passenger token.
- **Body:**

```json
{
  "pickup": "Negombo Station",
  "destination": "Katunayake Airport",
  "distanceKm": 10.5
}
```

The response includes the estimated `amount`. This creates a fare-estimate record, not a payment.

### 2. Calculate a final fare directly (optional)

- **Method:** `POST`
- **URL:** `http://localhost:8084/api/fares/final`
- **Token:** Driver or admin token. A driver token is enough; it does **not** need an admin token.
- **Body:**

```json
{
  "rideId": 1,
  "distanceKm": 10.5,
  "passengerAccountId": 1,
  "pickup": "Negombo Station",
  "destination": "Katunayake Airport"
}
```

Replace both numeric IDs with the real ride ID and passenger account ID. Ride completion in Member 3 already calls this endpoint automatically. This direct request is useful when testing Member 4 by itself; skip it during the normal end-to-end flow to avoid making another final-fare record.

### 3. Make a successful simulated payment

- **Method:** `POST`
- **URL:** `http://localhost:8084/api/payments`
- **Token:** Passenger token.
- **Body:**

```json
{
  "rideId": 1,
  "amount": 1350.00,
  "paymentMethod": "CARD",
  "simulateFailure": false
}
```

Use the real ride ID and final fare amount. The response should show `"status": "SUCCESS"` and a numeric payment `id`. Save the **payment ID**; it is not the ride ID. This does not charge a real card.

### 4. Get a payment

- **Method:** `GET`
- **URL:** `http://localhost:8084/api/payments/1`
- **Token:** The paying passenger's token; an admin token also works. Replace `1` with the **payment ID**.
- **Body:** No body.

The response shows the payment amount, status, ride ID, and receipt number.

### 5. Get a receipt

- **Method:** `GET`
- **URL:** `http://localhost:8084/api/payments/1/receipt`
- **Token:** The paying passenger's token; an admin token also works. Replace `1` with the payment ID.
- **Body:** No body.

A receipt is available only for a successful payment.

### 6. Test a simulated failure (optional)

- **Method:** `POST`
- **URL:** `http://localhost:8084/api/payments`
- **Token:** Passenger token.
- **Body:**

```json
{
  "rideId": 1,
  "amount": 1350.00,
  "paymentMethod": "CARD",
  "simulateFailure": true
}
```

This deliberately returns a payment-failure response. A failed payment cannot produce a successful receipt.

**Member 4 has no PUT, PATCH, or DELETE URLs. There is also no GET endpoint for fare records.**

## Complete team testing order

1. **Member 1:** Register passenger and driver, log in both, and share the two tokens and numeric account IDs with the team.
2. **Member 2:** Using the driver token, create a profile in `Negombo`, add a vehicle, set location, and set availability to `true`.
3. **Member 4:** Using the passenger token, optionally calculate a fare estimate.
4. **Member 3:** Using the passenger token, create a ride. Copy its ride ID and check which driver was assigned. Using that driver's token, accept, start, and complete it.
5. **Member 4:** Using the passenger token, create a payment for that ride. Copy the payment ID, then GET the payment and receipt.

In short: **Account login → available driver → ride → final fare → simulated payment → receipt**.

## If a request does not work

| Result | Common reason to check |
|---|---|
| `401 Unauthorized` | Missing, expired, or invalid token; or services were started with different JWT secrets. Log in again and paste the whole token. |
| `403 Forbidden` | The token has the wrong role—for example, a passenger token on a driver-only URL. |
| `404 Not Found` | The numeric ID in the URL does not exist, or the driver profile has not been created yet. |
| `409 Conflict` | A duplicate account/profile, no eligible driver, or a ride action performed in the wrong order. |
| `402 Payment Required` | Expected when testing `"simulateFailure": true`. |

If you prefer ready-made requests, import the [Postman collection](../postman/RideLink.postman_collection.json) and [Postman environment](../postman/RideLink.postman_environment.json). The collection can save tokens and IDs as you run its requests in order.
