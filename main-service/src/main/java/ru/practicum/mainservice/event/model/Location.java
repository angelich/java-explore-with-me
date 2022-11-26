package ru.practicum.mainservice.event.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class Location {
    private Double lat;
    private Double lon;
}