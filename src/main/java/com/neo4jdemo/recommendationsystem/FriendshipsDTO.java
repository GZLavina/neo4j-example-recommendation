package com.neo4jdemo.recommendationsystem;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FriendshipsDTO {
    String name;
    Long friendCount;
}
