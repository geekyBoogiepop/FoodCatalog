package com.example.catalogocomidafjp.ViewModel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.catalogocomidafjp.Retrofit.RetrofitInstance
import com.example.catalogocomidafjp.Room.ClientModel
import com.example.catalogocomidafjp.Room.Meal
import com.example.catalogocomidafjp.Room.MealList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealViewModel: ViewModel() {
    private var mealDetailsLiveData = MutableLiveData<Meal>()

    fun getMealDetail(id: String) {
        RetrofitInstance.api.getMealDetails(id).enqueue(object: Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body() != null) {
                    mealDetailsLiveData.value = response.body()!!.meals[0]
                }
                else {
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("MealActivity", t.message.toString())
            }
        })
    }

    fun getMealInfo(id: String, callback: (Meal?) -> Unit) {
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val mealList = response.body()
                val meal: Meal? = mealList?.meals?.get(0)
                callback(meal)
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("MealActivity", t.message.toString())
                callback(null)
            }
        })
    }



    fun observerMealDetailsLiveData() : LiveData<Meal> {
        return mealDetailsLiveData
    }

//    fun saveFavoriteInFirestore(food: HashMap<String, String>, clientId: String) {
//        val db = Firebase.firestore
//// Add a new document with a generated ID
//        db.collection("client")
//            .add(food)
//            .addOnSuccessListener { documentReference ->
//                Log.d(ContentValues.TAG, "Documento guardado con ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w(ContentValues.TAG, "Error guardando el documento", e)
//            }
//    }

//    fun saveFavoriteInFirestore(newFavorite: String, clientId: String) : Int{
//        val db = Firebase.firestore
//        var code: Int = 0
//// Add a new document with a generated ID
//        val client = db.collection("client").document(clientId)
//        client.get()
//            .addOnSuccessListener { documentReference ->
//                if (documentReference.exists()) {
//                    var currentFavorites = documentReference.get("favorites") as? ArrayList<String>
//                    if (currentFavorites != null) {
//                        if (currentFavorites.contains(newFavorite)) {
//                            currentFavorites.remove(newFavorite)
//                            code = -1
//                        }
//                        else {
//                            currentFavorites.add(newFavorite)
//                            code = 1
//                        }
//                        client.update("favorites", currentFavorites)
//                            .addOnSuccessListener {
//                                Log.d(ContentValues.TAG, "Nueva comida favorita agregada al cliente con ID: ${clientId}")
//                            }
//                            .addOnFailureListener {
//                                Log.d(ContentValues.TAG, "Error al agregar la nueva comida favorita al cliente con ID: ${clientId}")
//                            }
//                    }
//                } else {
//                    Log.d(ContentValues.TAG, "El cliente con ID: ${clientId} no existe")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.d(ContentValues.TAG, "Error al obtener el cliente con ID: ${clientId}", e)
//            }
//
//        return code
//    }

    fun saveFavoriteInFirestore(newFavorite: String, clientId: String, callback: (Int) -> Unit) {
        val db = Firebase.firestore
        var code: Int = 0

        val client = db.collection("client").document(clientId)
        client.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentFavorites = documentSnapshot.get("favorites") as? ArrayList<String>
                    if (currentFavorites != null) {
                        if (currentFavorites.contains(newFavorite)) {
                            currentFavorites.remove(newFavorite)
                            code = -1 // Indicates favorite was removed
                        } else {
                            currentFavorites.add(newFavorite)
                            code = 1 // Indicates favorite was added
                        }

                        client.update("favorites", currentFavorites)
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "Nueva comida favorita agregada al cliente con ID: $clientId")
                                callback(code) // Return the code via callback
                            }
                            .addOnFailureListener {
                                Log.d(ContentValues.TAG, "Error al agregar la nueva comida favorita al cliente con ID: $clientId")
                                callback(0) // Return 0 (indicating no change) via callback
                            }
                    }
                } else {
                    Log.d(ContentValues.TAG, "El cliente con ID: $clientId no existe")
                    callback(0) // Return 0 (indicating no change) via callback
                }
            }
            .addOnFailureListener { e ->
                Log.d(ContentValues.TAG, "Error al obtener el cliente con ID: $clientId", e)
                callback(0) // Return 0 (indicating no change) via callback
            }
    }

    fun isFavorite(newFavorite: String, clientId: String, callback: (Int) -> Unit) {
        val db = Firebase.firestore
        var code: Int = 0

        val client = db.collection("client").document(clientId)
        client.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentFavorites = documentSnapshot.get("favorites") as? ArrayList<String>
                    if (currentFavorites != null) {
                        if (currentFavorites.contains(newFavorite)) {
                            code = 1
                        }
                        callback(code)
                    }
                } else {
                    Log.d(ContentValues.TAG, "El cliente con ID: $clientId no existe")
                    callback(0) // Return 0 (indicating no change) via callback
                }
            }
            .addOnFailureListener { e ->
                Log.d(ContentValues.TAG, "Error al obtener el cliente con ID: $clientId", e)
                callback(0) // Return 0 (indicating no change) via callback
            }
    }

    fun getDocumentsFromFireStore() : List<ClientModel>? {
        var clientList: MutableList<ClientModel>? = null
        val db = Firebase.firestore

        db.collection("client")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "DOCUMENT: ${document.id} => ${document.data}")
                    var cliente: ClientModel = ClientModel()
                    //Log.d(ContentValues.TAG, "Meal: ${document.data.get("mealName")}")
                    cliente.nombre = document.data.get("nombre").toString()
                    cliente.apellido = document.data.get("apellido").toString()
                    cliente.edad = document.data.get("edad").toString().toInt()
                    cliente.sexo = document.data.get("sexo").toString()
                    cliente.favorites = document.data.get("favorites") as? ArrayList<String>
                    Log.d(ContentValues.TAG, "ArrayList: ${cliente.favorites}")
                    clientList?.add(cliente)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error recuperando los documentos.", exception)
            }

        return clientList
    }

//    fun getClientFromFireStore() : ClientModel {
//        val db = Firebase.firestore
//        var cliente = ClientModel()
//
//        db.collection("client")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(ContentValues.TAG, "DOCUMENT: ${document.id} => ${document.data}")
//
//                    //Log.d(ContentValues.TAG, "Meal: ${document.data.get("mealName")}")
//                    cliente.nombre = document.data.get("nombre").toString()
//                    cliente.apellido = document.data.get("apellido").toString()
//                    cliente.edad = document.data.get("edad").toString().toInt()
//                    cliente.sexo = document.data.get("sexo").toString()
//                    cliente.favorites = document.data.get("favorites") as? ArrayList<String>
//                    Log.d(ContentValues.TAG, "ArrayList: ${cliente.favorites}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(ContentValues.TAG, "Error recuperando los documentos.", exception)
//            }
//
//        return cliente
//    }

    fun getClientFromFireStore(callback: (ClientModel?) -> Unit) {
        val db = Firebase.firestore

        db.collection("client")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cliente = ClientModel()

                    cliente.nombre = document.data.get("nombre").toString()
                    cliente.apellido = document.data.get("apellido").toString()
                    cliente.edad = document.data.get("edad").toString().toInt()
                    cliente.sexo = document.data.get("sexo").toString()
                    cliente.favorites = document.data.get("favorites") as? ArrayList<String>

                    Log.d(ContentValues.TAG, "ArrayList: ${cliente.favorites}")
                    callback(cliente)
                    return@addOnSuccessListener
                }
                // If no documents found, invoke the callback with null
                callback(null)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error recuperando los documentos.", exception)
                callback(null) // Invoke the callback with null on failure
            }
    }

}