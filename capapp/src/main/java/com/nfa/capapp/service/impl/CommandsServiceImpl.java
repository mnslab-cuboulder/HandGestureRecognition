package com.nfa.capapp.service.impl;

import com.nfa.capapp.service.CommandsService;
import com.nfa.capapp.domain.Commands;
import com.nfa.capapp.repository.CommandsRepository;
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
 * Service Implementation for managing Commands.
 */
@Service
@Transactional
public class CommandsServiceImpl implements CommandsService{

    private final Logger log = LoggerFactory.getLogger(CommandsServiceImpl.class);
    
    @Inject
    private CommandsRepository commandsRepository;
    
    /**
     * Save a commands.
     * @return the persisted entity
     */
    public Commands save(Commands commands) {
        log.debug("Request to save Commands : {}", commands);
        Commands result = commandsRepository.save(commands);
        return result;
    }

    /**
     *  get all the commandss.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Commands> findAll(Pageable pageable) {
        log.debug("Request to get all Commandss");
        Page<Commands> result = commandsRepository.findAll(pageable); 
        return result;
    }

    /**
     *  get one commands by id.
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Commands findOne(Long id) {
        log.debug("Request to get Commands : {}", id);
        Commands commands = commandsRepository.findOne(id);
        return commands;
    }

    /**
     *  delete the  commands by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete Commands : {}", id);
        commandsRepository.delete(id);
    }
}
