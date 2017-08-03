package com.nfa.capapp.service.impl;

import com.nfa.capapp.service.GestureService;
import com.nfa.capapp.domain.Gesture;
import com.nfa.capapp.repository.GestureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Gesture.
 */
@Service
@Transactional
public class GestureServiceImpl implements GestureService{

    private final Logger log = LoggerFactory.getLogger(GestureServiceImpl.class);
    
    @Inject
    private GestureRepository gestureRepository;
    
    /**
     * Save a gesture.
     * @return the persisted entity
     */
    public Gesture save(Gesture gesture) {
        log.debug("Request to save Gesture : {}", gesture);
        Gesture result = gestureRepository.save(gesture);
        return result;
    }

    /**
     *  get all the gestures.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Gesture> findAll(Pageable pageable) {
        log.debug("Request to get all Gestures");
        Page<Gesture> result = gestureRepository.findAll(pageable); 
        return result;
    }

    /**
     *  get one gesture by id.
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Gesture findOne(Long id) {
        log.debug("Request to get Gesture : {}", id);
        Gesture gesture = gestureRepository.findOne(id);
        return gesture;
    }

    /**
     *  delete the  gesture by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete Gesture : {}", id);
        gestureRepository.delete(id);
    }
}
