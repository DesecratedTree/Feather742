package com.feather.engine.action;

public enum QueueType {

    WEAK,
    NORMAL,
    STRONG,
    SOFT;

    public boolean closesModalInterface() {
        return this == STRONG || this == SOFT;
    }

    public boolean removesWeakScripts() {
        return this == STRONG;
    }
}
