package edu.uchicago.caor.quarkusRest.resources;

import edu.uchicago.caor.quarkusRest.models.TripCountry;
import edu.uchicago.caor.quarkusRest.services.TripService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/trip")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TripResource {

    @Inject
    TripService tripService;

    @POST
    public TripCountry add(TripCountry tripCountry){
        return tripService.add(tripCountry);
    }

    @GET
    public List<TripCountry> getAll() {
        return tripService.getAll();
    }

    @GET
    @Path("{id}")
    public TripCountry get(@PathParam("id") String id) {
        return tripService.get(id);
    }

    @PUT
    @Path("{id}/{name}/{currency}")
    public TripCountry update(@PathParam("id") String id, @PathParam("name") String name, @PathParam("currency") String currency) {
        return tripService.update(id, name, currency);
    }

    @DELETE
    @Path("{id}")
    public TripCountry delete(@PathParam("id") String id) {
        return tripService.delete(id);
    }

    @GET
    @Path("/paged/{page}")
    public List<TripCountry> paged(@PathParam("page") int page){
        return  tripService.paged(page);
    }
}
