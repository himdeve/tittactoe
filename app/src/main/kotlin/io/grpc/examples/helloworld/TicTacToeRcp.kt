package io.grpc.examples.helloworld

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import gameSearchHandshake
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last
import java.io.Closeable
import java.util.*

class TicTacToeRcp(uri: Uri) : Closeable {
    private val moves = MutableSharedFlow<Game.Move>()
    private val responseState = mutableStateOf<Game.GameFoundHandshake>(Game.GameFoundHandshake.getDefaultInstance())
//    var gameStateFlow = mutableStateOf<Game.GameState>(Game.GameState.getDefaultInstance())
    var gameStateFlow = MutableStateFlow<Game.GameState>(Game.GameState.getDefaultInstance())

    private val channel = let {
        println("Connecting to ${uri.host}:${uri.port}")

        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }

        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val speedTacToe = SpeedTacToeGrpcKt.SpeedTacToeCoroutineStub(channel)

    suspend fun findAndConnect() {
        try {
            val uuid = UUID.randomUUID().toString()
            val request = gameSearchHandshake { this.clientUuid = uuid }
            val response = speedTacToe.findGame(request = request)
            responseState.value = Game.GameFoundHandshake.newBuilder(response).build()

            val responseConnect = speedTacToe.connect(requests = moves)
            gameStateFlow.value = responseConnect.last()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun nextMove() {
        val position = Game.Position.newBuilder()
            .setColumn(1)
            .setRow(1)
            .build()

        val move = Game.Move.newBuilder()
            .setGameUuid(responseState.value.gameUuid)
            .setPosition(position)
            .build()

        moves.tryEmit(move)
    }

    override fun close() {
        channel.shutdownNow()
    }
}