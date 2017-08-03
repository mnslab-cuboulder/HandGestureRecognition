package com.nfa.capapp.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Gesture.
 */
@Entity
@Table(name = "gesture")
public class Gesture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "gesture_name")
    private String gestureName;
    
    @Column(name = "gesture_training_status")
    private String gestureTrainingStatus;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGestureName() {
        return gestureName;
    }
    
    public void setGestureName(String gestureName) {
        this.gestureName = gestureName;
    }

    public String getGestureTrainingStatus() {
        return gestureTrainingStatus;
    }
    
    public void setGestureTrainingStatus(String gestureTrainingStatus) {
        this.gestureTrainingStatus = gestureTrainingStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Gesture gesture = (Gesture) o;
        if(gesture.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, gesture.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Gesture{" +
            "id=" + id +
            ", gestureName='" + gestureName + "'" +
            ", gestureTrainingStatus='" + gestureTrainingStatus + "'" +
            '}';
    }
}
