package io.chesslave.eyes.sikuli

import io.chesslave.app.log
import io.chesslave.eyes.Screen
import io.reactivex.Observable
import io.reactivex.Single
import org.sikuli.script.Location
import org.sikuli.util.OverlayCapturePrompt
import org.sikuli.util.ScreenHighlighter
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.util.concurrent.TimeUnit

class SikuliScreen : Screen {

    private val screen = org.sikuli.script.Screen.all()

    override fun captureAll() = screen.capture().image!!

    override fun capture(region: Rectangle) = screen.capture(region).image!!

    override fun select(message: String): Single<BufferedImage> {
        log.info("Selecting board...")
        return Single.create<BufferedImage> { result ->
            OverlayCapturePrompt(screen).apply {
                addObserver { subject ->
                    val me = subject as OverlayCapturePrompt
                    result.onSuccess(me.selection.image)
                }
                prompt(message)
            }
        }!!
    }

    override fun highlight(region: Rectangle, time: Long, unit: TimeUnit) {
        val overlay = ScreenHighlighter(screen, null)
        overlay.highlight(screen.newRegion(Location(region.x, region.y), region.width, region.height),
            unit.convert(time, TimeUnit.SECONDS).toFloat())
    }
}
