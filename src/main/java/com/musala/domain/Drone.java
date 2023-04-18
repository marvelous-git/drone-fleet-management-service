package com.musala.domain;

import com.musala.utils.enums.DroneModel;
import com.musala.utils.enums.DroneState;
import com.musala.utils.enums.DroneStateAction;
import com.musala.utils.enums.EntityStatus;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private DroneModel model;

    private Integer weightLimit;

    private Integer batteryCapacity;

    @Enumerated(EnumType.STRING)
    private DroneState state;

    @OneToMany(mappedBy = "drone", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Where(clause = "status='ACTIVE'")
    private List<Medication> medications;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    private EntityStatus entityStatus;

    @Column(nullable = false, name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "date_modified")
    private LocalDateTime dateModified;

    void setState(DroneState state) {
        this.state = state;
    }

    @PrePersist
    public void init(){
        dateCreated = LocalDateTime.now();
        entityStatus = EntityStatus.ACTIVE;
        batteryCapacity = 100;
        DroneStateTransitionMachine.get().changeState(this, DroneStateAction.REGISTER);
    }

    @PreUpdate
    public void update(){
        dateModified = LocalDateTime.now();
    }
}
