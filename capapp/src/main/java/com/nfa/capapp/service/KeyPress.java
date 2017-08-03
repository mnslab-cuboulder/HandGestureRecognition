/**
 * Created by raphael on 4/10/16.
 * notes:
 * up		38
 * down		40
 * right	39
 * left		37
 */

package com.nfa.capapp.service;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.stereotype.Service;

@Service
public class KeyPress {
    private static final Logger log = LoggerFactory.getLogger(KeyPress.class);
    private Robot robot;
    private volatile boolean hold = false;

    public KeyPress() throws AWTException {
        robot = new Robot();
        robot.setAutoDelay(40);
        robot.setAutoWaitForIdle(true);
    }
    
    public void anyKey(int i) {
    	
        log.info("anyKey -- Key press with key code " + i);

    	if (i < 128 && i > 0) {
            robot.keyPress(i);
            robot.keyRelease(i);
    	}
    }
    
    public void press(int i) {
        release();
        log.info("press -- Key press (hold) with key code " + i);
        hold = true;
        
    	if (i < 128 &&  i > 0) {
            while (hold) {
            	robot.keyPress(i);
            }
    	}
    }
    
    public void release() {
    	log.info("release -- Key release key");
    	hold = false;
    }

}
