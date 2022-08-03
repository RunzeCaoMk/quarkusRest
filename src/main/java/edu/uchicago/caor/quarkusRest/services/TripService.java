package edu.uchicago.caor.quarkusRest.services;

import edu.uchicago.caor.quarkusRest.models.TripCountry;
import edu.uchicago.caor.quarkusRest.repositories.TripRepository;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import java.util.List;

public class TripService {
    @Inject
    TripRepository tripRepository;

    // CRUD - C: create new country
    public TripCountry add(TripCountry countryItem) {
        //check for dup before adding record.
        TripCountry checkDup = tripRepository.get(countryItem.getName(), countryItem.getId());
        if (checkDup != null){
            throw new NotSupportedException("The TripCountry with id " + countryItem.getId() + " already exists");
        }
        return tripRepository.add(countryItem);
    }

    // CRUD - R: get a specific country base on name and id
    public TripCountry get(String name, String id) {
        TripCountry item = tripRepository.get(name, id );
        if (item == null){
            throw new NotFoundException("The TripCountry with id " + id + " was not found");
        }
        return item;
    }

    // CRUD - U: update a specific country base on name and id
    public TripCountry update(String name, String id,  String currency) {
        // delete the old one
        delete(name, id);
        // insert new one
        TripCountry tc = new TripCountry(id, name, currency);
        add(tc);
        return tc;
    }

    // CRUD - D: delete a specific country base on name and id
    public TripCountry delete(String name, String id) {
        TripCountry item = tripRepository.get(name, id);
        if (null == item){
            throw new NotFoundException("The TripCountry with id " + id + " was not found");
        }
        return tripRepository.delete(name, id);
    }

    public List<TripCountry> paged(String name, int page){
        return tripRepository.paged(name, page);
    }

}
