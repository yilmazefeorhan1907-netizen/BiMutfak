package com.yilmaz.bimutfak.di

import com.yilmaz.bimutfak.data.remote.api.RecipeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Uygulamanın uzak sunucu bağlantılarını oluşturur.
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val THE_MEAL_DB_BASE_URL =
        "https://www.themealdb.com/api/json/v1/1/"

    // Retrofit yapılandırmasını uygulama boyunca tek örnek olarak sağlar.
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(THE_MEAL_DB_BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    // Retrofit tarafından RecipeApiService uygulamasını oluşturur.
    @Provides
    @Singleton
    fun provideRecipeApiService(
        retrofit: Retrofit
    ): RecipeApiService {
        return retrofit.create(
            RecipeApiService::class.java
        )
    }
}