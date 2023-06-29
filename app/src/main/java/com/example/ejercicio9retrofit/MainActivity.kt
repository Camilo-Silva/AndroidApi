package com.example.ejercicio9retrofit

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BreedsAdapter
    private var imagesList = mutableListOf<String>()
    private var breedsList = mutableListOf<String>()

//    private lateinit var searchView: SearchView
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler)
        spinner = findViewById(R.id.spinner)
//        searchView = findViewById(R.id.searchview)

//        searchView.setOnQueryTextListener(this)


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BreedsAdapter(imagesList)
        recyclerView.adapter = adapter

        getListOfBreeds()
    }

    private fun getListOfBreeds() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getBreedsList("breeds/list/all")
            val response: Breeds? = call.body()

            runOnUiThread {
                if (call.isSuccessful) {

                    val breedsMap = response?.breed
                    if (breedsMap!= null) {
                        for (breed in breedsMap.keys) {
                            breedsList.add(breed)

                        }
                        setSpinner()
                    }else {
                        showError()
                    }
                }
            }

        }
    }

    private fun setSpinner() {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, breedsList)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getImagesBy(breedsList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun getImagesBy(breed: String?) {

        CoroutineScope(Dispatchers.IO).launch {
            val call =
                getRetrofit().create(ApiService::class.java)
                    .getImagesByBreeds("breed/$breed/images")
            val response = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    val images = response?.images ?: emptyList()
                    imagesList.clear()
                    imagesList.addAll(images)
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
                hideKeyboard()
            }

        }
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = this.currentFocus
        if (view == null) {
            view = View(this);
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0);
    }

    private fun showError() {
        Toast.makeText(this, "Error al recuperar el listado de razas", Toast.LENGTH_SHORT).show()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}