package io.grpc.examples.helloworld

import Game
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import io.grpc.examples.helloworld.model.BoxModel
import io.grpc.examples.helloworld.model.GamerModel
import io.grpc.examples.helloworld.model.Status

@Composable
fun MainScreen(viewModel: GamerViewModel) {
    val gameState = viewModel.gameState
    MainContent(gameState, viewModel::onEvent)
}

@Composable
fun MainContent(gameState: Game.GameState, eventTrigger: ((event: GamerViewModel.Event) -> Unit)?) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(), Arrangement.Top, Alignment.CenterHorizontally
    ) {
        Button({ eventTrigger?.invoke(GamerViewModel.Event.NextMove) }, Modifier.padding(10.dp)) {
            Text(stringResource(R.string.send_request))
        }

        Text(gameState.toString())
    }
}

/*
@Composable
fun GridButtons() {
    val cards: List<List<BoxModel>> by viewModel.getBoxes().observeAsState(listOf())
    val currentGame: LiveData<GamerModel> = viewModel.getGamerStatus()
    var currentPlayer: String = if (currentGame.value?.currentPlayer == Status.PlayerX) "Player X" else "Player O"
    val isGamerEnding: Boolean = currentGame.value?.isGamerEnding == true
    val winingPlayer: String = if (currentGame.value?.winingPlayer == Status.PlayerX) "Player X" else "Player O"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
    ) {
        cards.forEach { rows ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val configuration = LocalConfiguration.current

                rows.forEach { card ->
                    ActionButton(
                        card,
                        configuration.screenWidthDp.dp / rows.size,
                        configuration.screenWidthDp.dp / rows.size
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (isGamerEnding) {
                Text(
                    text = "Wining: $winingPlayer",
                    fontSize = 28.sp,
                    color = Color.Black,
                )
            } else {
                Text(
                    text = "Current: $currentPlayer",
                    fontSize = 28.sp,
                    color = Color.Black,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActionButton(card: BoxModel, width: Dp, height: Dp) {
    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(5.dp),
            )
            .height(height)
            .width(width),
        backgroundColor = Color.White,
        onClick = {
            viewModel.selectBox(card)
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = card.showText(),
                fontSize = 34.sp,
                color = Color.Black,
            )
        }
    }
}*/
