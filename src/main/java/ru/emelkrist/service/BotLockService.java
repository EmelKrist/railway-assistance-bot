package ru.emelkrist.service;

import org.springframework.stereotype.Service;

@Service
public class BotLockService {
    private boolean locked = false;

    public synchronized boolean isLocked() {
        return locked;
    }

    public synchronized void setLocked(boolean locked) {
        this.locked = locked;
    }
}
