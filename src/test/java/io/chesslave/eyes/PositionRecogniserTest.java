package io.chesslave.eyes;

import io.chesslave.eyes.sikuli.SikuliVision;
import io.chesslave.model.*;
import io.chesslave.visual.model.BoardImage;
import io.chesslave.visual.rendering.BoardRenderer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@Ignore
public class PositionRecogniserTest extends SinglePieceRecognitionTest {

    private PositionRecogniser recogniser;

    @Before
    public void setUp() throws Exception {
        final Position initialPosition = Game.initialPosition().position();
        final BoardImage initialBoard = BoardRenderer.using(chessSet, initialPosition).toBoardImage();
        final BoardConfiguration config = new BoardAnalyzer().analyze(initialBoard.image());
        this.recogniser = new PositionRecogniser(new SikuliVision(), config);
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
        final BoardImage board = BoardRenderer.using(chessSet, position).toBoardImage();
        final Position got = recogniser.position(board);
        assertEquals(position, got);
    }

    public void withPieceOnSquare(Square square, Piece piece) throws Exception {
        final Position position = new Position.Builder().withPiece(square, piece).build();
        final BoardImage board = BoardRenderer.using(chessSet, position).toBoardImage();
        final Position got = recogniser.position(board);
        assertEquals(position, got);
    }
}