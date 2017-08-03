package com.nfa.capapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nfa.capapp.domain.Commands;
import com.nfa.capapp.service.CommandsService;
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
 * REST controller for managing Commands.
 */
@RestController
@RequestMapping("/api")
public class CommandsResource {

    private final Logger log = LoggerFactory.getLogger(CommandsResource.class);
        
    @Inject
    private CommandsService commandsService;
    
    /**
     * POST  /commandss -> Create a new commands.
     */
    @RequestMapping(value = "/commandss",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Commands> createCommands(@RequestBody Commands commands) throws URISyntaxException {
        log.debug("REST request to save Commands : {}", commands);
        if (commands.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("commands", "idexists", "A new commands cannot already have an ID")).body(null);
        }
        Commands result = commandsService.save(commands);
        return ResponseEntity.created(new URI("/api/commandss/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("commands", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /commandss -> Updates an existing commands.
     */
    @RequestMapping(value = "/commandss",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Commands> updateCommands(@RequestBody Commands commands) throws URISyntaxException {
        log.debug("REST request to update Commands : {}", commands);
        if (commands.getId() == null) {
            return createCommands(commands);
        }
        Commands result = commandsService.save(commands);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("commands", commands.getId().toString()))
            .body(result);
    }

    /**
     * GET  /commandss -> get all the commandss.
     */
    @RequestMapping(value = "/commandss",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Commands>> getAllCommandss(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Commandss");
        Page<Commands> page = commandsService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/commandss");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /commandss/:id -> get the "id" commands.
     */
    @RequestMapping(value = "/commandss/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Commands> getCommands(@PathVariable Long id) {
        log.debug("REST request to get Commands : {}", id);
        Commands commands = commandsService.findOne(id);
        return Optional.ofNullable(commands)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /commandss/:id -> delete the "id" commands.
     */
    @RequestMapping(value = "/commandss/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCommands(@PathVariable Long id) {
        log.debug("REST request to delete Commands : {}", id);
        commandsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("commands", id.toString())).build();
    }
}
