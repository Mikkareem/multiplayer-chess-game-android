package com.techullurgy.chess.data.events.serializers

import com.techullurgy.chess.data.events.CellSelection
import com.techullurgy.chess.data.events.Disconnected
import com.techullurgy.chess.data.events.EnterRoomHandshake
import com.techullurgy.chess.data.events.GameLoading
import com.techullurgy.chess.data.events.GameUpdate
import com.techullurgy.chess.data.events.PieceMove
import com.techullurgy.chess.data.events.ReceiverBaseEvent
import com.techullurgy.chess.data.events.ResetSelection
import com.techullurgy.chess.data.events.ResetSelectionDone
import com.techullurgy.chess.data.events.SelectionResult
import com.techullurgy.chess.data.events.SenderBaseEvent
import com.techullurgy.chess.data.events.TimerUpdate
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

val receiverBaseEventJson = Json {
    serializersModule = SerializersModule {
        polymorphic(ReceiverBaseEvent::class, EnterRoomHandshake::class, EnterRoomHandshake.serializer())
        polymorphic(ReceiverBaseEvent::class, CellSelection::class, CellSelection.serializer())
        polymorphic(ReceiverBaseEvent::class, PieceMove::class, PieceMove.serializer())
        polymorphic(ReceiverBaseEvent::class, ResetSelection::class, ResetSelection.serializer())
        polymorphic(ReceiverBaseEvent::class, Disconnected::class, Disconnected.serializer())
    }
}

val senderBaseEventJson = Json {
    serializersModule = SerializersModule {
        polymorphic(SenderBaseEvent::class, GameUpdate::class, GameUpdate.serializer())
        polymorphic(SenderBaseEvent::class, TimerUpdate::class, TimerUpdate.serializer())
        polymorphic(SenderBaseEvent::class, GameLoading::class, GameLoading.serializer())
        polymorphic(SenderBaseEvent::class, SelectionResult::class, SelectionResult.serializer())
        polymorphic(SenderBaseEvent::class, ResetSelectionDone::class, ResetSelectionDone.serializer())
    }
}