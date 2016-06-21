package io.chesslave.eyes;

import io.chesslave.eyes.sikuli.SikuliVision;
import io.chesslave.model.*;
import io.chesslave.rendering.BoardRenderer;
import io.chesslave.visual.BoardImage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Optional;

public class FullPositionRecogniserTest extends SinglePieceRecognitionTest {

    private FullPositionRecogniser recogniser;

    @Before
    public void setUp() throws Exception {
        final Position initialPosition = Game.initialPosition().position();
        final BoardImage initialBoard = BoardRenderer.render(initialPosition, chessSet);
        final BoardConfiguration config = new BoardAnalyzer().analyze(initialBoard.image());
        this.recogniser = new FullPositionRecogniser(new SikuliVision(), config);
    }

    @Test
    public void recognisePosition() throws Exception {
        final Position position = Positions.fromText(
                "r|n|b|q|k|b|n|r",
                "p|p| | |p|p|p|p",
                " | | |p| | | | ",
                " | | | | | | | ",
                " | | |p|P| | | ",
                " | | | | |N| | ",
                "P|P|P| | |P|P|P",
                "R|N|B|Q|K|B| |R");
        final BoardImage board = BoardRenderer.render(position, chessSet);
        final Optional<Position> got = recogniser.begin(board);
        Assert.assertEquals(Optional.of(position), got);
    }

    public void withPieceOnSquare(Square square, Piece piece) throws Exception {
        final Position position = new Position.Builder().withPiece(square, piece).build();
        final BoardImage board = BoardRenderer.render(position, chessSet);
        final Optional<Position> got = recogniser.begin(board);
        Assert.assertEquals(Optional.of(position), got);
    }
}