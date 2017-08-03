package com.nfa.capapp.service;

import com.nfa.capapp.domain.Gesture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Gesture.
 */
public interface GestureService {

    /**
     * Save a gesture.
     * @return the persisted entity
     */
    public Gesture save(Gesture gesture);

    /**
     *  get all the gestures.
     *  @return the list of entities
     */
    public Page<Gesture> findAll(Pageable pageable);

    /**
     *  get the "id" gesture.
     *  @return the entity
     */
    public Gesture findOne(Long id);

    /**
     *  delete the "id" gesture.
     */
    public void delete(Long id);
}
