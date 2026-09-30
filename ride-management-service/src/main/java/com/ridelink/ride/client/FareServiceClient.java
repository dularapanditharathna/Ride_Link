package com.ridelink.ride.client;

import com.ridelink.ride.dto.RideDtos.*;
import com.ridelink.ride.exception.ExceptionSupport.DownstreamServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class FareServiceClient {
  private final RestClient client;

  public FareServiceClient(RestClient.Builder b, @Value("${services.fare.base-url}") String url) {
    client = b.baseUrl(url).build();
  }

  public FareResponse finalFare(FinalFareRequest request, String authorization) {
    try {
      return client
          .post()
          .uri("/api/fares/final")
          .header("Authorization", authorization)
          .body(request)
          .retrieve()
          .body(FareResponse.class);
    } catch (RestClientException e) {
      throw new DownstreamServiceException("Fare Service is unavailable; ride was not completed");
    }
  }
}
