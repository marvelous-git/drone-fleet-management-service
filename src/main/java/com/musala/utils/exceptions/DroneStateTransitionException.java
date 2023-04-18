package com.musala.utils.exceptions;

import com.musala.utils.enums.DroneState;
import com.musala.utils.enums.DroneStateAction;

public class DroneStateTransitionException extends RuntimeException {
    private final DroneState droneState;
    private final DroneStateAction action;

    public DroneStateTransitionException(DroneState droneState, DroneStateAction action) {
        this.droneState = droneState;
        this.action = action;
    }

    public DroneState getDroneState() {
        return droneState;
    }

    public DroneStateAction getAction() {
        return action;
    }

    @Override
    public String getMessage() {
        return "invalid action " + action + " on state " + droneState;
    }
}