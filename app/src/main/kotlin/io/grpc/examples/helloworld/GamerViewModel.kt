package io.grpc.examples.helloworld

import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.grpc.examples.helloworld.model.BoxModel
import io.grpc.examples.helloworld.model.GamerModel
import io.grpc.examples.helloworld.model.Status
import kotlinx.coroutines.launch

class GamerViewModel : ViewModel() {
    //    private val uri by lazy { Uri.parse(resources.getString(R.string.server_url)) }
//    private val uri by lazy { Uri.parse("http://2.tcp.eu.ngrok.io:12377") }
//    private val ticTacToeRcpService by lazy { TicTacToeRcp(uri) }

    private val uri = Uri.parse("http://2.tcp.eu.ngrok.io:12377")
    private val ticTacToeRcpService = TicTacToeRcp(uri)

    val gameState = ticTacToeRcpService.gameStateFlow.value

    init {
//        loadGamer()

//        viewModelScope.launch {
//            ticTacToeRcpService.gameStateFlow
//        }

        viewModelScope.launch {
            ticTacToeRcpService.findAndConnect()
        }
    }

    private val gamerStatus: MutableLiveData<GamerModel> by lazy {
        MutableLiveData<GamerModel>()
    }

    private val boxes: MutableLiveData<MutableList<MutableList<BoxModel>>> by lazy {
        MutableLiveData<MutableList<MutableList<BoxModel>>>()
    }

    fun getGamerStatus(): LiveData<GamerModel> {
        return gamerStatus
    }

    fun getBoxes(): LiveData<MutableList<MutableList<BoxModel>>> {
        return boxes
    }

    private fun loadGamer() {
        var indexColumn: Int = 0
        var indexRow: Int = 0

        boxes.value = MutableList(5) {
            indexRow = 0

            MutableList(5) {
                BoxModel(
                    indexColumn = indexColumn++ / 5,
                    indexRow = indexRow++
                )
            }
        }

        gamerStatus.value = GamerModel()
    }

    fun selectBox(box: BoxModel) {
        var currantPlayer: Status = gamerStatus.value?.currentPlayer!!
        var hasModification: Boolean = false

        var list: MutableList<MutableList<BoxModel>> = boxes.value?.map { columns ->
            var newColumns = columns.map { row ->
                if (box.indexColumn == row.indexColumn && box.indexRow == row.indexRow) {
                    if (row.status == Status.Empty) {
                        hasModification = true
                        row.status = currantPlayer
                    }
                }

                row
            }

            newColumns
        } as MutableList<MutableList<BoxModel>>

        if (hasModification && gamerStatus.value?.isGamerEnding == false) {
            gamerStatus.value?.currentPlayer = gamerStatus.value?.currentPlayer!!.next()
            boxes.value?.removeAll { true }
            boxes.value = list
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.NextMove -> viewModelScope.launch { ticTacToeRcpService.nextMove() }
        }
    }

    override fun onCleared() {
        ticTacToeRcpService.close()
        super.onCleared()
    }

    sealed class Event {
        object NextMove : Event()
    }
}