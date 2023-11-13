package com.neo4jdemo.recommendationsystem;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends Neo4jRepository<Place, String>, CypherdslStatementExecutor<Place> {

    @Query("MATCH (p:Place) RETURN DISTINCT p.type")
    List<String> findAllTypes();

    @Query("""
            MATCH (place:Place)<-[:LIKES]-(person:Person)
            WHERE person.age >= $min AND person.age <= $max
            RETURN DISTINCT place
            """)
    List<Place> findPlacesByUserAge(int min, int max);

    @Query("""
            MATCH (place:Place) OPTIONAL MATCH (place)<-[:LIKES]-(person:Person)
            RETURN place.name as name, count(person.name) as likeCount
            ORDER BY likeCount DESC, name ASC
            """)
    List<PopularityDTO> findLikeCount();

    @Query("MATCH (:Person {name: $person})-[:FRIENDS_WITH*..1]-(:Person)-[:LIKES]->(place:Place) RETURN DISTINCT place")
    List<Place> findPlacesByFriendshipDepth1(String person);

    @Query("MATCH (:Person {name: $person})-[:FRIENDS_WITH*..2]-(:Person)-[:LIKES]->(place:Place) RETURN DISTINCT place")
    List<Place> findPlacesByFriendshipDepth2(String person);

    @Query("MATCH (:Person {name: $person})-[:FRIENDS_WITH*..3]-(:Person)-[:LIKES]->(place:Place) RETURN DISTINCT place")
    List<Place> findPlacesByFriendshipDepth3(String person);

    @Query("""
            MATCH (:Person {name: $person})-[:FRIENDS_WITH*..1]-(:Person)-[:LIKES]->(place:Place)
            WHERE place.type = $type
            RETURN DISTINCT place
            """)
    List<Place> findPlacesByTypeAndFriendshipD1(String person, String type);

    @Query("""
            MATCH (:Person {name: $person})-[:FRIENDS_WITH*..2]-(:Person)-[:LIKES]->(place:Place)
            WHERE place.type = $type
            RETURN DISTINCT place
            """)
    List<Place> findPlacesByTypeAndFriendshipD2(String person, String type);

    @Query("""
            MATCH (:Person {name: $person})-[:FRIENDS_WITH*..3]-(:Person)-[:LIKES]->(place:Place)
            WHERE place.type = $type
            RETURN DISTINCT place
            """)
    List<Place> findPlacesByTypeAndFriendshipD3(String person, String type);
}
