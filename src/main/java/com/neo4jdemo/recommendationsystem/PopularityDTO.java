package com.neo4jdemo.recommendationsystem;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PopularityDTO {
    String name;
    Long likeCount;
}
