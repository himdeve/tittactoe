package io.grpc.examples.helloworld

import SpeedTacToeGrpcKt
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import gameSearchHandshake
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.Closeable
import java.util.*

class MainActivity : AppCompatActivity() {

    private val uri by lazy { Uri.parse(resources.getString(R.string.server_url)) }
    private val greeterService by lazy { TicTacToeRcp(uri) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(color = MaterialTheme.colors.background) {
                Greeter(greeterService)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        greeterService.close()
    }
}

class TicTacToeRcp(uri: Uri) : Closeable {
    private val moves = MutableSharedFlow<Game.Move>()
    val responseState = mutableStateOf<Game.GameFoundHandshake>(Game.GameFoundHandshake.getDefaultInstance())
    var gameStateFlow = mutableStateOf<Game.GameState>(Game.GameState.getDefaultInstance())


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

@Composable
fun Greeter(ticTacToeRcp: TicTacToeRcp) {

    val scope = rememberCoroutineScope()

    val nameState = remember { mutableStateOf(TextFieldValue()) }

    val gameState = ticTacToeRcp.gameStateFlow.value

    LaunchedEffect("key1") {
        ticTacToeRcp.findAndConnect()
    }

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(), Arrangement.Top, Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.name_hint), modifier = Modifier.padding(top = 10.dp))
        OutlinedTextField(nameState.value, { nameState.value = it })

        Button({ scope.launch { ticTacToeRcp.nextMove() } }, Modifier.padding(10.dp)) {
            Text(stringResource(R.string.send_request))
        }

        Text(gameState.toString())
    }
}