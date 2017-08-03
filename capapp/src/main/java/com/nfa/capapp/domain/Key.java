package com.nfa.capapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;


/**
 * A Key.
 */
@Entity
@Table(name = "key")
public class Key {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "key_press")
    private int keyPress;

    public int getKeyPress() {
        return keyPress;
    }

    public void setKeyPress(int keyPress) {
        this.keyPress = keyPress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
