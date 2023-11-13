package com.neo4jdemo.recommendationsystem;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class PlacesInCommonDTO {
    String personName;
    List<String> placesInCommon;
}
