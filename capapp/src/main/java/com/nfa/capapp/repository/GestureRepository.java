package com.nfa.capapp.repository;

import com.nfa.capapp.domain.Gesture;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Gesture entity.
 */
public interface GestureRepository extends JpaRepository<Gesture,Long> {

}
