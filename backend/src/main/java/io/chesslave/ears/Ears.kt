package io.chesslave.ears

import io.chesslave.model.MoveDescription
import io.chesslave.model.MoveDescription.Status
import io.chesslave.model.Piece.Type
import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Tokens
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Patterns

typealias UtteranceParser = (String) -> MoveDescription?

val parseUtterance: UtteranceParser = { utterance ->
    fun term(text: String): Parser<Boolean> = Parsers.token { if (it.value().toString() == text) true else null }
    val words = Patterns.isChar(CharPredicates.IS_ALPHA).many1().toScanner("word").source()
    val nums = Patterns.isChar(CharPredicates.IS_DIGIT).many1().toScanner("num").source()
    val tokenizer = Parsers.or(
        words.map { Tokens.fragment(it, "WORD") },
        nums.map { Tokens.fragment(it, "NUM") })
    val queenParser = Parsers.or(term("donna"), term("regina"))
    val kingParser = term("re")
    val pieceParser = Parsers.or(
        term("pedone").map { Type.PAWN },
        term("cavallo").map { Type.KNIGHT },
        term("alfiere").map { Type.BISHOP },
        term("torre").map { Type.ROOK },
        queenParser.map { Type.QUEEN },
        kingParser.map { Type.KING })
    val colParser = Parsers.or(
        term("a").map { 0 },
        term("b").map { 1 },
        term("c").map { 2 },
        term("d").map { 3 },
        term("e").map { 4 },
        term("f").map { 5 },
        term("g").map { 6 },
        term("h").map { 7 })
    val rowParser = Parsers.or(
        term("1").map { 0 },
        term("2").map { 1 },
        term("3").map { 2 },
        term("4").map { 3 },
        term("5").map { 4 },
        term("6").map { 5 },
        term("7").map { 6 },
        term("8").map { 7 })
    val squareParser = Parsers.or(
        Parsers.sequence(
            pieceParser.followedBy(term("in").optional(null)),
            colParser.optional(null),
            rowParser.optional(null),
            { piece, col, row -> MoveDescription.Square(piece, col, row) }),
        Parsers.sequence(
            colParser,
            rowParser.optional(null),
            { col, row -> MoveDescription.Square(null, col, row) }))
    val captureParser = Parsers.or(term("mangia"), term("cattura"))
    val statusParser = Parsers
        .or(Parsers.or(term("scacco").next(term("matto")), term("matto"), term("scaccomatto")).map { Status.CHECKMATE },
            term("scacco").map { Status.CHECK })
        .optional(Status.RELAX)
    val regularMoveParser = Parsers.sequence(
        squareParser,
        captureParser.optional(false),
        squareParser.optional(null),
        statusParser,
        { fst, isCapture, snd, status ->
            if (snd == null) MoveDescription.Regular(toSquare = fst, status = status)
            else MoveDescription.Regular(fromSquare = fst, toSquare = snd, capture = isCapture, status = status)
        })
    val castlingParser = Parsers.sequence(
        term("arrocco"),
        Parsers.or(
            Parsers.or(term("corto"), term("di").next(kingParser)).map { true },
            Parsers.or(term("lungo"), term("di").next(queenParser)).map { false }
        ).optional(null),
        statusParser,
        { _, short, status -> MoveDescription.Castling(short, status) })
    val parser = Parsers.or(regularMoveParser, castlingParser)
    parser
        .from(tokenizer.lexer(Scanners.WHITESPACES.optional(null)))
        .parse(utterance)
}