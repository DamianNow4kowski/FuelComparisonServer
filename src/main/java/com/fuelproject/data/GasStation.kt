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
    var isForElectricCars = false
    var isForDisabledPeople = false
    var fuels : List<Fuel> = emptyList()

    override fun toString(): String {
        return "GasStation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", isForElectricCars=" + isForElectricCars +
                ", isForDisabledPeople=" + isForDisabledPeople +
                ", fuels=" + fuels +
                '}'
    }

    companion object {
        private const val serialVersionUID = -7619773757862241235L
    }
}
