package edu.uchicago.caor.quarkusRest.repositories;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import edu.uchicago.caor.quarkusRest.models.TripCountry;
import io.quarkus.runtime.StartupEvent;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class TripRepository {
    @Inject
    MongoClient mongoClient;

    private Gson gson = new Gson();
    public static final int PAGE_SIZE = 20;

    // generate dummy data on start
    void onStart(@Observes StartupEvent ev) {
        long collectionSize = getCollection().countDocuments();
        if (collectionSize > 0) return;

        Faker faker = new Faker();
        getCollection().insertMany(
                Stream.generate(() -> new TripCountry(
                                UUID.randomUUID().toString(),
                                faker.country().name(),
                                faker.country().currency())
                        )
                        .peek(trip -> System.out.println(trip))
                        .map(trip -> item2doc(trip))
                        .limit(1000).collect(Collectors.toList())
        );
    }

    // CRUD - C: create new country
    public TripCountry add(TripCountry tc) {
        getCollection().insertOne(item2doc(tc));
        return tc;
    }

    // CRUD - R: get a specific country base on name and id
    public TripCountry get(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);

        FindIterable<Document> documents = getCollection().find(query);

        List<TripCountry> items = new ArrayList<>();
        for (Document document : documents) {
            items.add(doc2item(document));
        }

        //this will produce a 404 not found
        if (items.size() != 1) return null;

        return items.get(0);
    }

    public List<TripCountry> getAll() {
        FindIterable<Document> documents = getCollection().find();

        List<TripCountry> items = new ArrayList<>();
        for (Document document : documents) {
            items.add(doc2item(document));
        }

        //this will produce a 404 not found
        if (items.size() == 0) return null;

        return items;
    }

    // CRUD - U: update a specific country base on name and id
    public TripCountry update(String id, String name, String currency) {
        // delete the old one
        delete(id);
        // insert new one
        TripCountry tc = new TripCountry(id, name, currency);
        add(tc);
        return tc;
    }

    // CRUD - D: delete a specific country base on name and id
    public TripCountry delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);

        FindIterable<Document> documents = getCollection().find(query);

        Document firstDocument = null;
        for (Document document : documents) {
            firstDocument = document;
            break;
        }
        getCollection().deleteOne(firstDocument);

        return doc2item(firstDocument);
    }

    public List<TripCountry> paged(int page) {
//        BasicDBObject query = new BasicDBObject();
//        query.put("name", name);
        List<TripCountry> trip = new ArrayList<>();
        try {
            // find(query)
            MongoCursor<Document> cursor = getCollection().find().skip(PAGE_SIZE * (page - 1)).limit(PAGE_SIZE).iterator();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                trip.add(doc2item(document));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trip;
    }

    // define the collection
    private MongoCollection getCollection() {
        return mongoClient.getDatabase("trip_db").getCollection("trip_collection");
    }

    // local transform ops: from doc to item
    private TripCountry doc2item(Document document) {
        if (document != null && !document.isEmpty()) {
            return gson.fromJson(document.toJson(), TripCountry.class);
        }
        return null;
    }

    // local transform ops: from item to doc
    private Document item2doc(TripCountry item) {
        if (item != null) {
            String json = gson.toJson(item);
            return Document.parse(json);
        }
        return null;
    }
}
