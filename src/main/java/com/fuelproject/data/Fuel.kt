package com.fuelproject.data

import java.io.Serializable

class Fuel : Serializable {
    var fuelId: Long = 0
    var name: String? = null
    var price: Float? = null
    override fun toString(): String {
        return "Fuel{" +
                "fuelId=" + fuelId +
                ", price=" + price +
                ", name=" + name +
                '}'
    }
}
