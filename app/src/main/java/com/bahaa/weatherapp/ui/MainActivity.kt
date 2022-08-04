package com.bahaa.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bahaa.weatherapp.R
import com.bahaa.weatherapp.databinding.ActivityMainBinding
import com.bahaa.weatherapp.model.ApiWeather
import com.bahaa.weatherapp.util.Constants
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        fetchJson()
    }

    private fun fetchJson() {
        val request = Request.Builder().url(Constants.URL).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string().let { jsonString ->
                    val result = Gson().fromJson(jsonString, ApiWeather::class.java)
                    result.list[0].let { item ->
                        Log.e("bha", "Response: ==> $item")
                        runOnUiThread {
                            binding?.cityNam?.text = result.city.name
                            binding?.country?.text = result.city.country
                            val updatedAt: Long = item.dt.toLong()
                            val timDateText =
                                SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(
                                    Date(updatedAt * 1000)
                                )
                            binding?.timDateText?.text = timDateText

                            val sunrise = SimpleDateFormat(
                                "hh:mm a",
                                Locale.ENGLISH
                            ).format(Date(result.city.sunrise.toLong() * 1000))
                            binding?.sunrise?.text = sunrise
                            val sunset = SimpleDateFormat(
                                "hh:mm a",
                                Locale.ENGLISH
                            ).format(Date(result.city.sunset.toLong() * 1000))
                            binding?.sunset?.text = sunset
                            binding?.temperature?.text =
                                (item.main.temp.toInt() / 10).toString() + "°C"
                            binding?.humidity?.text = item.main.humidity.toString()
                            binding?.wind?.text = item.wind.speed.toString() + "km/h"
                            binding?.pressure?.text = item.main.pressure.toString()

                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "onFailure: ${e.message}")
            }
        }
        )
    }


}