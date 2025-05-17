package com.techullurgy.chess.di

import androidx.room.Room
import com.techullurgy.chess.data.ChessGameApi
import com.techullurgy.chess.data.GameRepositoryImpl
import com.techullurgy.chess.data.GameRoomMessageBroker
import com.techullurgy.chess.data.db.ChessGameDatabase
import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.domain.repository.GameRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.generateNonce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ClientId = generateNonce()

val Intercept = createClientPlugin("Intercept") {
    onRequest { request, _ ->
        request.parameter("client_id", ClientId)
    }
}

val DataModule = module {
    single<HttpClient>(named("socket")) {
        HttpClient {
            install(Intercept)
            install(HttpCookies)
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
                pingIntervalMillis = 20_000
            }
        }
    }

    single<HttpClient>(named("http")) {
        HttpClient {
            install(Intercept)
            install(HttpCookies)
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    single {
        ChessGameApi(
            socketClient = get<HttpClient>(named("socket")),
            httpClient = get<HttpClient>(named("http")),
        )
    }

    single<ChessGameDatabase> {
        Room.databaseBuilder(
            get(), ChessGameDatabase::class.java, "chess-game-database"
        ).build()
    }

    single(named("app_scope")) {
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    single<GameDao> {
        get<ChessGameDatabase>().gameDao
    }

    singleOf(::GameRoomMessageBroker)

    single<GameRepository> {
        GameRepositoryImpl(get(), get(), get(named("app_scope")))
    }
}