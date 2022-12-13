package com.redhat.rharyanto.hellovertx.rest;

import com.hazelcast.core.HazelcastInstance;
import com.redhat.rharyanto.hellovertx.entity.Person;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:rharyant@redhat.com">Robertus Lilik Haryanto</a>
 */
public class PersonHandler {

    private final static Logger logger = LoggerFactory.getLogger(PersonHandler.class);

    private final static String ENDPOINT = "/person";

    public class DataInitializer {
        public static List<Person> init() {
            Person p1 = new Person("Robert", 25, Person.Sex.MALE);
            Person p2 = new Person("Defi", 24, Person.Sex.FEMALE);
            Person p3 = new Person("Raphael", 23, Person.Sex.MALE);
            Person p4 = new Person("Mikhael", 22, Person.Sex.MALE);
            Person p5 = new Person("Gabriel", 21, Person.Sex.MALE);

            List<Person> persons = new ArrayList<>();
            persons.add(p1);
            persons.add(p2);
            persons.add(p3);
            persons.add(p4);
            persons.add(p5);

            return persons;
        }
    }

    private List<Person> persons;

    public PersonHandler() {
        persons = DataInitializer.init();
    }

    public PersonHandler(HazelcastInstance hzInstance) {
        if (hzInstance != null) {
            persons = hzInstance.getList("person");
            if (persons.size() != 0) {
                logger.info("Sync-ing existing Person data from Hazelcast...");
            } else {
                logger.info("Initialising new Person data...");
                persons.addAll(DataInitializer.init());
            }
        }
    }

    public void getAll(RoutingContext rc) {
        logger.info("Incoming request to " + PersonHandler.ENDPOINT + "/all");
        rc.response().end(Json.encode(persons));
    }

    public void get(RoutingContext rc) {
        logger.info("Incoming request to " + PersonHandler.ENDPOINT + "/:name");
        var params = rc.pathParams();
        var name = params.get("name");
        List<Person> results = persons.stream()
                .filter(p -> {
                    return name.equalsIgnoreCase(p.getName());
                }).toList();
        rc.response().end(Json.encode(results));
    }

    public void getBySex(RoutingContext rc) {
        logger.info("Incoming request to " + PersonHandler.ENDPOINT + "/sex/:sex");
        var params = rc.pathParams();
        var sex = params.get("sex");
        List<Person> results = persons.stream()
                .filter(p -> {
                    return sex.equalsIgnoreCase(p.getSex().toString());
                }).toList();
        rc.response().end(Json.encode(results));
    }
}
