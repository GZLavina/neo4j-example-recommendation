package com.neo4jdemo.recommendationsystem;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {

    @Query("MATCH (:Person {name: $name})-[:FRIENDS_WITH]-(p:Person) RETURN p")
    List<Person> findAllFriends(String name);

    @Query("MATCH (:Person {name: $name})-[:LIKES]->(p:Place) RETURN p")
    List<Place> findLikedPlacesByPerson(String name);

    @Query("MATCH (p:Person) WHERE p.age >= $min AND p.age <= $max RETURN p")
    List<Person> findByAgeBetween(Integer min, Integer max);

    @Query("""
            MATCH (p:Person) OPTIONAL MATCH (p)-[:FRIENDS_WITH]-(friend:Person)
            RETURN p.name as name, count(friend.name) as friendCount
            ORDER BY friendCount DESC, name ASC
            """)
    List<FriendshipsDTO> findFriendshipCount();

    @Query("MATCH (:Person {name: $name1})-[r:FRIENDS_WITH]-(:Person {name: $name2}) DELETE r")
    void removeFriendship(String name1, String name2);

    @Query("MATCH (:Person {name: $person})-[r:LIKES]->(:Place {name: $place}) DELETE r")
    void removeLike(String person, String place);

    @Query("""
            MATCH (:Person {name: $person})-[:LIKES]->(place:Place)<-[:LIKES]-(person:Person)
            RETURN person.name as personName, collect(place.name) as placesInCommon
            """)
    List<PlacesInCommonDTO> findPersonWithPlacesInCommon(String person);
}
