package com.example.cheapchomp.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Simplified product data for UI display
@JsonClass(generateAdapter = true)
data class ProductPrice(
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: String,
    @Json(name = "imageUrl") val imageUrl: String?
)

// OAuth token response
@JsonClass(generateAdapter = true)
data class TokenResponse(
    @Json(name = "access_token") val accessToken: String
)

// Store location response
@JsonClass(generateAdapter = true)
data class LocationResponse(
    @Json(name = "data") val data: List<LocationData>?
)

// Individual store location data
@JsonClass(generateAdapter = true)
data class LocationData(
    @Json(name = "locationId") val locationId: String
)

// Product search response
@JsonClass(generateAdapter = true)
data class ProductResponse(
    @Json(name = "data") val data: List<ProductData>?
)

// Individual product data
@JsonClass(generateAdapter = true)
data class ProductData(
    @Json(name = "description") val description: String,
    @Json(name = "items") val items: List<ProductItem>?,
    @Json(name = "images") val images: List<ProductImage>?
)

// Product image metadata
@JsonClass(generateAdapter = true)
data class ProductImage(
    @Json(name = "perspective") val perspective: String?,
    @Json(name = "featured") val featured: Boolean?,
    @Json(name = "sizes") val sizes: List<ImageSize>?
)

// Product image size variants
@JsonClass(generateAdapter = true)
data class ImageSize(
    @Json(name = "size") val size: String?,
    @Json(name = "url") val url: String?
)

// Product item details including pricing
@JsonClass(generateAdapter = true)
data class ProductItem(
    @Json(name = "price") val price: ProductItemPrice?
)

// Product pricing information
@JsonClass(generateAdapter = true)
data class ProductItemPrice(
    @Json(name = "regular") val regular: String?,
    @Json(name = "promo") val promo: String?
)