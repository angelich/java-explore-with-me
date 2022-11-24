package ru.practicum.mainservice.event.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Location {
    private Double lat;
    private Double lon;
}