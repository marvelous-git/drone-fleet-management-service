package com.musala.repository;

import com.musala.domain.Drone;
import com.musala.utils.enums.DroneState;
import com.musala.utils.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

    Optional<Drone> findBySerialNumberAndEntityStatusIsNot(String serial, EntityStatus entityStatus);

    List<Drone> findByStateAndEntityStatusIsNotAndBatteryCapacityGreaterThan(DroneState state, EntityStatus entityStatus, Integer batteryCapacity);

    Boolean existsBySerialNumberAndEntityStatusIsNot(String serialNumber, EntityStatus entityStatus);

    long countByEntityStatusIsNot(EntityStatus entityStatus);
}