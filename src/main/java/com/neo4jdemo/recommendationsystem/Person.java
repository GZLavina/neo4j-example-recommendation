package com.neo4jdemo.recommendationsystem;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.stream.Collectors;

@Node
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class Person {

    @Id
    private String name;
    private Integer age;

    @EqualsAndHashCode.Exclude
    @Relationship(type="LIKES")
    private List<Place> likedPlaces;

    @EqualsAndHashCode.Exclude
    @Relationship(type="FRIENDS_WITH")
    private List<Person> friends;

    public void addFriend(Person person) {
        if (!friends.contains(person)) {
            friends.add(person);
        }
    }

    public void addLikedPlace(Place place) {
        if (!likedPlaces.contains(place)) {
            likedPlaces.add(place);
        }
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", likedPlaces=" + likedPlaces.stream().map(Place::getName).collect(Collectors.joining(", ")) +
                ", friends=" + friends.stream().map(Person::getName).collect(Collectors.joining(", ")) +
                '}';
    }
}
