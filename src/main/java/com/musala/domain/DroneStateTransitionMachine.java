package com.musala.domain;

import com.musala.utils.enums.DroneState;
import com.musala.utils.enums.DroneStateAction;
import com.musala.utils.exceptions.DroneStateTransitionException;

public class DroneStateTransitionMachine {

    private static final DroneStateTransitionMachine instance = new DroneStateTransitionMachine();

    private DroneStateTransitionMachine() {

    }

    public static DroneStateTransitionMachine get() {
        return instance;
    }

    public Drone changeState(Drone drone, DroneStateAction action) throws DroneStateTransitionException {
        if (drone == null || action == null) {
            throw new IllegalArgumentException();
        }

        DroneState nextState = getNextState(drone, action);

        if (nextState == null) {
            throw new DroneStateTransitionException(drone.getState(), action);
        }

        drone.setState(nextState);
        return drone;
    }

    DroneState getNextState(Drone drone, DroneStateAction action) throws DroneStateTransitionException {
        if (drone.getState() == null) {
            return fromNull(drone.getState(), action);
        }

        switch (drone.getState()) {
            case IDLE:
                return fromIdle(drone.getState(), action);
            case LOADING:
                return fromLoading(drone.getState(), action);
            case LOADED:
                return fromLoaded(drone.getState(), action);
            case DELIVERING:
                return fromDelivering(drone.getState(), action);
            case DELIVERED:
                return fromDelivered(drone.getState(), action);
            case RETURNING:
                return fromReturning(drone.getState(), action);
        }
        throw new DroneStateTransitionException(drone.getState(), action);
    }

    private DroneState fromNull(DroneState state, DroneStateAction action) throws DroneStateTransitionException {
        switch (action) {
            case REGISTER:
                return DroneState.IDLE;
            default:
                throw new DroneStateTransitionException(state, action);
        }
    }

    private DroneState fromIdle(DroneState state, DroneStateAction action)
            throws DroneStateTransitionException {
        switch (action) {
            case LOAD:
                return DroneState.LOADING;
            default:
                throw new DroneStateTransitionException(state, action);
        }
    }
    private DroneState fromLoading(DroneState state, DroneStateAction action)
            throws DroneStateTransitionException {
        switch (action) {
            case INSPECT_LOAD:
                return DroneState.LOADED;
            default:
                throw new DroneStateTransitionException(state, action);
        }
    }

    private DroneState fromLoaded(DroneState state, DroneStateAction action)
            throws DroneStateTransitionException {
        switch (action) {
            case DEPART:
                return DroneState.DELIVERING;
            default:
                throw new DroneStateTransitionException(state, action);
        }
    }
    private DroneState fromDelivering(DroneState state, DroneStateAction action)
            throws DroneStateTransitionException {
        switch (action) {
            case ARRIVE:
                return DroneState.DELIVERED;
            case RETURN:
                return DroneState.RETURNING; // In case battery got low
            default:
                throw new DroneStateTransitionException(state, action);
        }
    }

    private DroneState fromDelivered(DroneState state, DroneStateAction action)
            throws DroneStateTransitionException {
        switch (action) {
            case RETURN:
                return DroneState.RETURNING;
            default:
                throw new DroneStateTransitionException(state, action);
        }
    }

    private DroneState fromReturning(DroneState state, DroneStateAction action) {
        switch (action) {
            case ARRIVE:
                return DroneState.IDLE;
            default:
                throw new DroneStateTransitionException(state, action);
        }
    }







}