package com.nfa.capapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nfa.capapp.domain.Gesture;
import com.nfa.capapp.service.GestureService;
import com.nfa.capapp.web.rest.util.HeaderUtil;
import com.nfa.capapp.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Gesture.
 */
@RestController
@RequestMapping("/api")
public class GestureResource {

    private final Logger log = LoggerFactory.getLogger(GestureResource.class);
        
    @Inject
    private GestureService gestureService;
    
    /**
     * POST  /gestures -> Create a new gesture.
     */
    @RequestMapping(value = "/gestures",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Gesture> createGesture(@RequestBody Gesture gesture) throws URISyntaxException {
        log.debug("REST request to save Gesture : {}", gesture);
        if (gesture.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("gesture", "idexists", "A new gesture cannot already have an ID")).body(null);
        }
        Gesture result = gestureService.save(gesture);
        return ResponseEntity.created(new URI("/api/gestures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("gesture", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /gestures -> Updates an existing gesture.
     */
    @RequestMapping(value = "/gestures",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Gesture> updateGesture(@RequestBody Gesture gesture) throws URISyntaxException {
        log.debug("REST request to update Gesture : {}", gesture);
        if (gesture.getId() == null) {
            return createGesture(gesture);
        }
        Gesture result = gestureService.save(gesture);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("gesture", gesture.getId().toString()))
            .body(result);
    }

    /**
     * GET  /gestures -> get all the gestures.
     */
    @RequestMapping(value = "/gestures",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Gesture>> getAllGestures(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Gestures");
        Page<Gesture> page = gestureService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/gestures");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /gestures/:id -> get the "id" gesture.
     */
    @RequestMapping(value = "/gestures/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Gesture> getGesture(@PathVariable Long id) {
        log.debug("REST request to get Gesture : {}", id);
        Gesture gesture = gestureService.findOne(id);
        return Optional.ofNullable(gesture)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /gestures/:id -> delete the "id" gesture.
     */
    @RequestMapping(value = "/gestures/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteGesture(@PathVariable Long id) {
        log.debug("REST request to delete Gesture : {}", id);
        gestureService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("gesture", id.toString())).build();
    }
}
