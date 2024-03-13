package pl.sda.carrental.model.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class CarDTO {
    private final String make;
    private final String model;
    private final String bodyStyle;
    private final int year;
    private final String color;
    private final double mileage;
    private final BigDecimal price;

}
