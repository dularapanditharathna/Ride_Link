# Demo workflow

1. Import the Postman collection and environment and select the environment.
2. Register and log in the passenger. Tests save `passenger_token` and `passenger_id`.
3. Register and log in the driver. Tests save `driver_token` and `driver_id`.
4. Create the driver profile for `Negombo`, add a vehicle, set a location, and set availability to true.
5. Request a fare estimate as the passenger.
6. Create a ride. The ride ID is saved and its status should be `ASSIGNED`.
7. Accept, start, and complete it as the assigned driver.
8. Create a successful simulated payment as the passenger; retrieve its receipt.
9. Demonstrate errors using the Negative Scenarios folder: missing token, passenger on a driver endpoint, no matching driver, invalid transition, duplicate email, and `simulateFailure: true` payment.

