package com.vitalo.markrun.di

import com.google.gson.GsonBuilder
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.data.remote.api.*
import com.vitalo.markrun.data.remote.interceptor.*
import com.vitalo.markrun.service.LoginManager
import com.vitalo.markrun.util.DeviceInfoUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private fun baseClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
    }

    private fun buildRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        @Suppress("DEPRECATION")
        val gson = GsonBuilder()
            .disableHtmlEscaping()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ─── Training ───
    @Provides
    @Singleton
    @Named("trainingClient")
    fun provideTrainingClient(): OkHttpClient {
        return baseClientBuilder()
            .addInterceptor(TrainingInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideTrainingApi(@Named("trainingClient") client: OkHttpClient): TrainingApi {
        return buildRetrofit(AppConfig.trainingBaseUrl, client)
            .create(TrainingApi::class.java)
    }

    // ─── AccountCenter ───
    @Provides
    @Singleton
    @Named("accountClient")
    fun provideAccountClient(deviceInfoUtils: DeviceInfoUtils): OkHttpClient {
        return baseClientBuilder()
            .addInterceptor(AccountCenterInterceptor { deviceInfoUtils.getDeviceInfoMap() })
            .build()
    }

    @Provides
    @Singleton
    fun provideAccountApi(@Named("accountClient") client: OkHttpClient): AccountApi {
        return buildRetrofit(AppConfig.accountBaseUrl, client)
            .create(AccountApi::class.java)
    }

    // ─── Coin ───
    @Provides
    @Singleton
    @Named("coinClient")
    fun provideCoinClient(
        loginManager: dagger.Lazy<LoginManager>,
        deviceInfoUtils: DeviceInfoUtils
    ): OkHttpClient {
        return baseClientBuilder()
            .addInterceptor(CoinInterceptor(
                tokenProvider = { loginManager.get().currentAccessToken },
                deviceInfoProvider = { deviceInfoUtils.getDeviceInfoMap() }
            ))
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinApi(@Named("coinClient") client: OkHttpClient): CoinApi {
        return buildRetrofit(AppConfig.coinBaseUrl, client)
            .create(CoinApi::class.java)
    }

    // ─── Game ───
    @Provides
    @Singleton
    fun provideGameApi(@Named("coinClient") client: OkHttpClient): GameApi {
        return buildRetrofit(AppConfig.gameBaseUrl, client)
            .create(GameApi::class.java)
    }

    // ─── ABTest ───
    @Provides
    @Singleton
    @Named("abTestClient")
    fun provideABTestClient(): OkHttpClient {
        return baseClientBuilder()
            .addInterceptor(ABTestInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideABTestApi(@Named("abTestClient") client: OkHttpClient): ABTestApi {
        return buildRetrofit(AppConfig.abTestBaseUrl, client)
            .create(ABTestApi::class.java)
    }

    // ─── NewStoreLite (广告) ───
    @Provides
    @Singleton
    @Named("newStoreClient")
    fun provideNewStoreClient(): OkHttpClient {
        return baseClientBuilder()
            .addInterceptor(NewStoreInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewStoreApi(@Named("newStoreClient") client: OkHttpClient): NewStoreApi {
        return buildRetrofit(AppConfig.adBaseUrl, client)
            .create(NewStoreApi::class.java)
    }
}
