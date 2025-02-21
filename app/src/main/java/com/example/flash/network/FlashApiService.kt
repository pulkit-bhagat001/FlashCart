package com.example.flash.network
import com.example.flash.data.InternetItem
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
//Json.asConverterFactory("application/json".toMediaType()) is a kotlinx.serialization converter factory to convert the json data to images,strings,etc.
private const val BASE_URL=""
@OptIn(ExperimentalSerializationApi::class)
private  val retrofit=Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()
//asConverterFactory is defined in kotlinx.serialization library
interface FlashApiService {
    @GET("")
    suspend fun getItems():List<InternetItem> //it will return the contents as list of type Internet Item data class
    //suspend fun getItems():String //it will return the contents as a string as we have used scalars converter factory
    /*
    here suspend means that fun will be handled by coroutines
    it means that if we suspend this function performing the network operation
    the other parts of the ui will be still functional and the network request
    will be carried out in the background
     */
}// json file is an api(common api format)
// when we will call getItems() then retrofit will combine the base url and the endpoint to start the request

object FlashApi{
    val retrofitService:FlashApiService by lazy {
        retrofit.create(
            FlashApiService::class.java
        )
    }
}
/*
retrofit.create(FlashApiService::class.java) generates and returns an instance of a class that implements FlashApiService

Because the instance returned by retrofit.create(FlashApiService::class.java) implements the FlashApiService interface,
it makes sense to declare the variable with the type FlashApiService. This ensures that the variable can hold any object
that implements the FlashApiService interface, including the dynamically generated one.

here lazy means that if we will open the page that needs data through the internet then only the get request will be executed.
if that page is not opened then it will not happen
 */
//create() is used to implement an interface