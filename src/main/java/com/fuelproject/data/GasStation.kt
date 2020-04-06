package com.fuelproject.data

import java.io.Serializable


class GasStation : Serializable {
    var id: Long = 0
    var name = ""
    var address = ""
    var city = ""
    var latitude = 0.0
    var longitude = 0.0
    var openFrom = ""
    var openTo = ""
    var hasPetrol95 = false
    var hasPetrol98 = false
    var hasDieselFuel = false
    var hasNaturalGas = false
    var isForElectricCars = false
    var isForDisabledPeople = false

    override fun toString(): String {
        return "GasStation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", isForElectricCars=" + isForElectricCars +
                ", isForDisabledPeople=" + isForDisabledPeople +
                '}'
    }

    companion object {
        private const val serialVersionUID = -7619773757862241235L
    }
}
