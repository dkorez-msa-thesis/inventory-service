package dev.dkorez.msathesis.catalog.controller;

import dev.dkorez.msathesis.catalog.model.InventoryRequest;
import dev.dkorez.msathesis.catalog.model.ReservationRequest;
import dev.dkorez.msathesis.catalog.service.InventoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryControllerRest {

    private final InventoryService inventoryService;

    @Inject
    public InventoryControllerRest(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @POST
    @Path("reserve")
    public Response reserveQuantity(ReservationRequest reservation) {
        inventoryService.reserveQuantity(reservation);
        return Response.ok().build();
    }

    @POST
    @Path("reserve")
    public Response cancelReservation(ReservationRequest reservation) {
        inventoryService.cancelReservation(reservation);
        return Response.ok().build();
    }

    @PATCH
    @Path("update")
    public Response cancelReservation(InventoryRequest inventory) {
        inventoryService.updateQuantity(inventory);
        return Response.ok().build();
    }
}
