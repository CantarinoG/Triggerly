package io.github.cantarinog.triggerly.domain.service;

import io.github.cantarinog.triggerly.domain.model.TriggerEvent;

public interface AlarmScheduler {
    void schedule(TriggerEvent triggerEvent);
    void cancel(TriggerEvent triggerEvent);
}
