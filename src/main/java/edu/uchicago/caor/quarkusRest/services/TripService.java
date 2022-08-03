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
        TripCountry checkDup = tripRepository.get(countryItem.getId());
        if (checkDup != null){
            throw new NotSupportedException("The TripCountry with id " + countryItem.getId() + " already exists");
        }
        return tripRepository.add(countryItem);
    }

    // CRUD - R:
    // get a specific country base on name and id
    public TripCountry get(String id) {
        TripCountry item = tripRepository.get(id);
        if (item == null){
            throw new NotFoundException("The TripCountry with id " + id + " was not found");
        }
        return item;
    }
    // get all the record form DB
    public List<TripCountry> getall() {
        List<TripCountry> items = tripRepository.getall();
        if (items == null || items.isEmpty()) {
            throw new NotFoundException("There is no record in the DB");
        }
        return items;
    }
    // get paged data
    public List<TripCountry> paged(int page){
        return tripRepository.paged(page);
    }

    // CRUD - U: update a specific country base on name and id
    public TripCountry update(String id, String name, String currency) {
        TripCountry item = tripRepository.update(id, name, currency);
        if (item == null){
            throw new NotFoundException("The TripCountry with id " + id + " was not updated correctly");
        }
        return item;
    }

    // CRUD - D: delete a specific country base on name and id
    public TripCountry delete(String id) {
        TripCountry item = tripRepository.get(id);
        if (null == item){
            throw new NotFoundException("The TripCountry with id " + id + " was not found");
        }
        return tripRepository.delete(id);
    }




}
