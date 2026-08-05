package com.yilmaz.bimutfak.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Firebase nesnelerinin nasıl oluşturulacağını Hilt'e tarif eden modüldür.
// Bu modülü uygulama boyunca yaşayan Hilt bileşenine bağlar.
// Veri taşıyan veya kullanıcı başına ayrı örnekleri gereken bir sınıf değil. Yalnızca Hilt’e nesne üretme tarifleri veriyor.
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    // FirebaseAuth istendiğinde kullanılacak nesneyi Hilt'e sağlar.
    // Uygulama boyunca aynı DataSource örneğinin kullanılmasını sağlar.
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    // FirebaseAuth istendiğinde kullanılacak nesneyi Hilt'e sağlar.
    // Uygulama boyunca aynı DataSource örneğinin kullanılmasını sağlar.
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}

