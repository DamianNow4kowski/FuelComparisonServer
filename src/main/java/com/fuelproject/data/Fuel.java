package com.fuelproject.data;

import java.io.Serializable;

public class Fuel implements Serializable {

    public long fuelId;
    public String name;
    public Float price;

    @Override
    public String toString() {
        return "Fuel{" +
            "fuelId=" + fuelId +
            ", price=" + price +
            ", name=" + name +
            '}';
    }
}
