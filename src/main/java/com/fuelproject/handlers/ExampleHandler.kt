package com.fuelproject.handlers

import java.io.IOException


class ExampleHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        writeResponse("Imie to  ${params!!.urlParams["name"]}")
    }
}
