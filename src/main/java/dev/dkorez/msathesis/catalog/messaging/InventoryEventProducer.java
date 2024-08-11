package dev.dkorez.msathesis.catalog.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class InventoryEventProducer {
    @Inject
    @Channel("inventory-events")
    Emitter<InventoryEvent> eventEmitter;

    public void sendEvent(InventoryEvent event) {
        eventEmitter.send(event);
    }
}
