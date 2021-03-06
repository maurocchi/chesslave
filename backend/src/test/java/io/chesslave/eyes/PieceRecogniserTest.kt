package io.chesslave.eyes

import io.chesslave.eyes.sikuli.SikuliVision
import io.chesslave.model.Game
import io.chesslave.model.Piece
import io.chesslave.model.Position
import io.chesslave.model.Square
import io.chesslave.visual.rendering.BoardRenderer
import io.chesslave.visual.rendering.ChessSet
import io.vavr.control.Option
import org.junit.Before
import org.junit.Assert.assertEquals

class PieceRecogniserTest(chessSet: ChessSet) : SinglePieceRecognitionTest(chessSet) {

    lateinit var recogniser: PieceRecogniser

    @Before
    fun setUp() {
        val initialPosition = Game.initialPosition().position()
        val initialBoard = BoardRenderer(chessSet).withPosition(initialPosition).render()
        val config = analyzeBoardImage(initialBoard.image)
        this.recogniser = PieceRecogniser(SikuliVision(), config)
    }

    override fun withPieceOnSquare(square: Square, piece: Piece) {
        val position = Position.Builder().withPiece(square, piece).build()
        val board = BoardRenderer(chessSet).withPosition(position).render()
        val got = recogniser.piece(board.squareImage(square), Piece.all.toList())
        assertEquals(Option.of(piece), got)
    }
}