package com.nfa.capapp.web.rest;

import com.nfa.capapp.Application;
import com.nfa.capapp.domain.Gesture;
import com.nfa.capapp.repository.GestureRepository;
import com.nfa.capapp.service.GestureService;

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
 * Test class for the GestureResource REST controller.
 *
 * @see GestureResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class GestureResourceIntTest {

    private static final String DEFAULT_GESTURE_NAME = "AAAAA";
    private static final String UPDATED_GESTURE_NAME = "BBBBB";
    private static final String DEFAULT_GESTURE_TRAINING_STATUS = "AAAAA";
    private static final String UPDATED_GESTURE_TRAINING_STATUS = "BBBBB";

    @Inject
    private GestureRepository gestureRepository;

    @Inject
    private GestureService gestureService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restGestureMockMvc;

    private Gesture gesture;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GestureResource gestureResource = new GestureResource();
        ReflectionTestUtils.setField(gestureResource, "gestureService", gestureService);
        this.restGestureMockMvc = MockMvcBuilders.standaloneSetup(gestureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        gesture = new Gesture();
        gesture.setGestureName(DEFAULT_GESTURE_NAME);
        gesture.setGestureTrainingStatus(DEFAULT_GESTURE_TRAINING_STATUS);
    }

    @Test
    @Transactional
    public void createGesture() throws Exception {
        int databaseSizeBeforeCreate = gestureRepository.findAll().size();

        // Create the Gesture

        restGestureMockMvc.perform(post("/api/gestures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(gesture)))
                .andExpect(status().isCreated());

        // Validate the Gesture in the database
        List<Gesture> gestures = gestureRepository.findAll();
        assertThat(gestures).hasSize(databaseSizeBeforeCreate + 1);
        Gesture testGesture = gestures.get(gestures.size() - 1);
        assertThat(testGesture.getGestureName()).isEqualTo(DEFAULT_GESTURE_NAME);
        assertThat(testGesture.getGestureTrainingStatus()).isEqualTo(DEFAULT_GESTURE_TRAINING_STATUS);
    }

    @Test
    @Transactional
    public void getAllGestures() throws Exception {
        // Initialize the database
        gestureRepository.saveAndFlush(gesture);

        // Get all the gestures
        restGestureMockMvc.perform(get("/api/gestures?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(gesture.getId().intValue())))
                .andExpect(jsonPath("$.[*].gestureName").value(hasItem(DEFAULT_GESTURE_NAME.toString())))
                .andExpect(jsonPath("$.[*].gestureTrainingStatus").value(hasItem(DEFAULT_GESTURE_TRAINING_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getGesture() throws Exception {
        // Initialize the database
        gestureRepository.saveAndFlush(gesture);

        // Get the gesture
        restGestureMockMvc.perform(get("/api/gestures/{id}", gesture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(gesture.getId().intValue()))
            .andExpect(jsonPath("$.gestureName").value(DEFAULT_GESTURE_NAME.toString()))
            .andExpect(jsonPath("$.gestureTrainingStatus").value(DEFAULT_GESTURE_TRAINING_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGesture() throws Exception {
        // Get the gesture
        restGestureMockMvc.perform(get("/api/gestures/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGesture() throws Exception {
        // Initialize the database
        gestureRepository.saveAndFlush(gesture);

		int databaseSizeBeforeUpdate = gestureRepository.findAll().size();

        // Update the gesture
        gesture.setGestureName(UPDATED_GESTURE_NAME);
        gesture.setGestureTrainingStatus(UPDATED_GESTURE_TRAINING_STATUS);

        restGestureMockMvc.perform(put("/api/gestures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(gesture)))
                .andExpect(status().isOk());

        // Validate the Gesture in the database
        List<Gesture> gestures = gestureRepository.findAll();
        assertThat(gestures).hasSize(databaseSizeBeforeUpdate);
        Gesture testGesture = gestures.get(gestures.size() - 1);
        assertThat(testGesture.getGestureName()).isEqualTo(UPDATED_GESTURE_NAME);
        assertThat(testGesture.getGestureTrainingStatus()).isEqualTo(UPDATED_GESTURE_TRAINING_STATUS);
    }

    @Test
    @Transactional
    public void deleteGesture() throws Exception {
        // Initialize the database
        gestureRepository.saveAndFlush(gesture);

		int databaseSizeBeforeDelete = gestureRepository.findAll().size();

        // Get the gesture
        restGestureMockMvc.perform(delete("/api/gestures/{id}", gesture.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Gesture> gestures = gestureRepository.findAll();
        assertThat(gestures).hasSize(databaseSizeBeforeDelete - 1);
    }
}
