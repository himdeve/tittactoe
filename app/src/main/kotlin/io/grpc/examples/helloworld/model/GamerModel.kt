package io.grpc.examples.helloworld.model

class GamerModel(
    var currentPlayer: Status = Status.PlayerX,
    var winingPlayer: Status = Status.Empty,
    var isGamerEnding: Boolean = false,
) {}