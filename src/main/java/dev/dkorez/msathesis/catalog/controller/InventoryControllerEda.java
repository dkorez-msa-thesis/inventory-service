package dev.dkorez.msathesis.catalog.controller;

import dev.dkorez.msathesis.catalog.model.InventoryRequest;
import dev.dkorez.msathesis.catalog.model.ReservationRequest;
import dev.dkorez.msathesis.catalog.service.InventoryCoordinator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("eda/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryControllerEda {
    private final InventoryCoordinator inventoryCoordinator;
    private final static boolean SEND_EVENT = true;

    @Inject
    public InventoryControllerEda(InventoryCoordinator inventoryCoordinator) {
        this.inventoryCoordinator = inventoryCoordinator;
    }

    @POST
    @Path("reserve")
    public Response reserveQuantity(ReservationRequest reservation) {
        inventoryCoordinator.reserveQuantity(reservation, SEND_EVENT);
        return Response.ok().build();
    }

    @POST
    @Path("cancel")
    public Response cancelReservation(ReservationRequest reservation) {
        inventoryCoordinator.cancelReservation(reservation, SEND_EVENT);
        return Response.ok().build();
    }

    @PATCH
    @Path("update")
    public Response cancelReservation(InventoryRequest inventory) {
        inventoryCoordinator.updateQuantity(inventory, SEND_EVENT);
        return Response.ok().build();
    }
}
