package com.nfa.capapp.web.rest;

import com.nfa.capapp.Application;
import com.nfa.capapp.domain.Commands;
import com.nfa.capapp.repository.CommandsRepository;
import com.nfa.capapp.service.CommandsService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CommandsResource REST controller.
 *
 * @see CommandsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CommandsResourceIntTest {


    @Inject
    private CommandsRepository commandsRepository;

    @Inject
    private CommandsService commandsService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCommandsMockMvc;

    private Commands commands;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CommandsResource commandsResource = new CommandsResource();
        ReflectionTestUtils.setField(commandsResource, "commandsService", commandsService);
        this.restCommandsMockMvc = MockMvcBuilders.standaloneSetup(commandsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        commands = new Commands();
    }

    @Test
    @Transactional
    public void createCommands() throws Exception {
        int databaseSizeBeforeCreate = commandsRepository.findAll().size();

        // Create the Commands

        restCommandsMockMvc.perform(post("/api/commandss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(commands)))
                .andExpect(status().isCreated());

        // Validate the Commands in the database
        List<Commands> commandss = commandsRepository.findAll();
        assertThat(commandss).hasSize(databaseSizeBeforeCreate + 1);
        Commands testCommands = commandss.get(commandss.size() - 1);
    }

    @Test
    @Transactional
    public void getAllCommandss() throws Exception {
        // Initialize the database
        commandsRepository.saveAndFlush(commands);

        // Get all the commandss
        restCommandsMockMvc.perform(get("/api/commandss?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(commands.getId().intValue())));
    }

    @Test
    @Transactional
    public void getCommands() throws Exception {
        // Initialize the database
        commandsRepository.saveAndFlush(commands);

        // Get the commands
        restCommandsMockMvc.perform(get("/api/commandss/{id}", commands.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(commands.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCommands() throws Exception {
        // Get the commands
        restCommandsMockMvc.perform(get("/api/commandss/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCommands() throws Exception {
        // Initialize the database
        commandsRepository.saveAndFlush(commands);

		int databaseSizeBeforeUpdate = commandsRepository.findAll().size();

        // Update the commands

        restCommandsMockMvc.perform(put("/api/commandss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(commands)))
                .andExpect(status().isOk());

        // Validate the Commands in the database
        List<Commands> commandss = commandsRepository.findAll();
        assertThat(commandss).hasSize(databaseSizeBeforeUpdate);
        Commands testCommands = commandss.get(commandss.size() - 1);
    }

    @Test
    @Transactional
    public void deleteCommands() throws Exception {
        // Initialize the database
        commandsRepository.saveAndFlush(commands);

		int databaseSizeBeforeDelete = commandsRepository.findAll().size();

        // Get the commands
        restCommandsMockMvc.perform(delete("/api/commandss/{id}", commands.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Commands> commandss = commandsRepository.findAll();
        assertThat(commandss).hasSize(databaseSizeBeforeDelete - 1);
    }
}
