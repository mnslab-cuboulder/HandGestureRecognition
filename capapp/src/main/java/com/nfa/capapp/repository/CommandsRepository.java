package com.nfa.capapp.repository;

import com.nfa.capapp.domain.Commands;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Commands entity.
 */
public interface CommandsRepository extends JpaRepository<Commands,Long> {

}
