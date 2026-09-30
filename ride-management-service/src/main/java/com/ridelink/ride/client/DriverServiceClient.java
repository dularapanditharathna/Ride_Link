package com.ridelink.ride.client;

import com.ridelink.ride.dto.RideDtos.EligibleDriver;
import com.ridelink.ride.exception.ExceptionSupport.DownstreamServiceException;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class DriverServiceClient {
  private final RestClient client;

  public DriverServiceClient(
      RestClient.Builder b, @Value("${services.driver.base-url}") String url) {
    client = b.baseUrl(url).build();
  }

  public List<EligibleDriver> eligible(String area, String authorization) {
    try {
      return client
          .get()
          .uri(u -> u.path("/api/drivers/eligible").queryParam("serviceArea", area).build())
          .header("Authorization", authorization)
          .retrieve()
          .body(new ParameterizedTypeReference<List<EligibleDriver>>() {});
    } catch (RestClientException e) {
      throw new DownstreamServiceException("Driver Service is unavailable");
    }
  }
}
