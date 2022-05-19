package com.example.restapisampleapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.restapisampleapp.data.Library

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.restapisampleapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.LatLngBounds
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMarkerClickListener {
            if (it.tag != null){
                var url = it.tag as String
                if (!url.startsWith("http")){
                    url = "http://${url}"

                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            true
        }
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        loadLibraries()
    }

    private fun loadLibraries(){
        val retrofit = Retrofit.Builder().baseUrl(LibraryOpenApi.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val openApiService = retrofit.create(LibraryOpenService::class.java)

        openApiService
            .getLibrary(LibraryOpenApi.API_KEY)
            .enqueue(object : retrofit2.Callback<Library> {
                override fun onResponse(call: Call<Library>, response: Response<Library>) {
                    showLibraries(response.body() as Library)
                }

                override fun onFailure(call: Call<Library>, t: Throwable) {
                    Toast.makeText(baseContext, "에러발생", Toast.LENGTH_LONG).show()
                }

            })
    }
    fun showLibraries(libraries : Library){
        val latLnglist = LatLngBounds.Builder()

        for (lib in libraries.SeoulPublicLibraryInfo.row){
            val position = LatLng(lib.XCNTS.toDouble(), lib.YDNTS.toDouble())
            val marker = MarkerOptions().position(position).title(lib.LBRRY_NAME)
            var obj = mMap.addMarker(marker)
            obj?.tag = lib.HMPG_URL

            latLnglist.include(marker.position)
        }

        val bounds = latLnglist.build()
        val padding = 0
        val updated = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(updated)
    }
}