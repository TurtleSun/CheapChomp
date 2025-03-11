package com.example.cheapchomp.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.cheapchomp.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.Base64
import java.util.concurrent.TimeUnit
import com.example.cheapchomp.network.models.TokenResponse
import com.example.cheapchomp.network.models.LocationResponse
import com.example.cheapchomp.network.models.ProductResponse

/**
 * Retrofit interface for the Kroger API.
 * Handles all direct API communications with Kroger's endpoints.
 */
interface KrogerApiService {

    // Authenticates with Kroger's OAuth2 system to get an access token
    @FormUrlEncoded
    @POST("v1/connect/oauth2/token")
    suspend fun getAccessToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("scope") scope: String = "product.compact"
    ): Response<TokenResponse>

    // Finds the nearest Kroger store to given coordinates
    @GET("v1/locations")
    suspend fun findNearestStore(
        @Header("Authorization") authorization: String,
        @Query("filter.lat.near") latitude: Double,
        @Query("filter.lon.near") longitude: Double,
        @Query("filter.limit") limit: Int = 1
    ): Response<LocationResponse>

    // Searches for products at a specific store location
    @GET("v1/products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String,
        @Query("filter.term") term: String,
        @Query("filter.locationId") locationId: String,
        @Query("filter.limit") limit: Int = 50
    ): Response<ProductResponse>

    companion object {
        private const val BASE_URL = "https://api.kroger.com/"

        // Creates an instance of the KrogerApiService with configured Retrofit client
        @RequiresApi(Build.VERSION_CODES.O)
        fun create(): KrogerApiService {
            // Configure logging
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Configure OkHttp client
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            // Configure Moshi JSON parser
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            // Build and return Retrofit service
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(KrogerApiService::class.java)
        }

        // Generates Basic Auth header from client credentials
        @RequiresApi(Build.VERSION_CODES.O)
        fun getBasicAuthHeader(): String {
            val credentials = "${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"
            return "Basic ${Base64.getEncoder().encodeToString(credentials.toByteArray())}"
        }
    }
}