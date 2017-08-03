package com.nfa.capapp.service;

import com.nfa.capapp.domain.Commands;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Commands.
 */
public interface CommandsService {

    /**
     * Save a commands.
     * @return the persisted entity
     */
    public Commands save(Commands commands);

    /**
     *  get all the commandss.
     *  @return the list of entities
     */
    public Page<Commands> findAll(Pageable pageable);

    /**
     *  get the "id" commands.
     *  @return the entity
     */
    public Commands findOne(Long id);

    /**
     *  delete the "id" commands.
     */
    public void delete(Long id);
}
