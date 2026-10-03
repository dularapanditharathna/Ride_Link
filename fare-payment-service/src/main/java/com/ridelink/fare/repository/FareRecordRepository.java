package com.ridelink.fare.repository;

import com.ridelink.fare.entity.FareRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FareRecordRepository extends JpaRepository<FareRecord, Long> {}
