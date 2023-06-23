package com.example.catalogocomidafjp.Room

data class ClientModel(
    var nombre : String? = "",
    var apellido: String? = "",
    var sexo: String? = "",
    var edad: Int? = 0,
    var favorites: ArrayList<String>? = null
)
