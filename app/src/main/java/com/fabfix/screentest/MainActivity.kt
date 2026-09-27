package com.fabfix.screentest

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

/** Fonds plein ecran unis pour tester l'affichage (pixels morts, uniformite,
 * retro-eclairage) - fleches gauche/droite (ou haut/bas) de la telecommande
 * pour changer de couleur. Le nom de la couleur s'affiche brievement dans un
 * coin puis disparait, pour ne jamais gener l'inspection de l'ecran. */
class MainActivity : Activity() {

    private data class ColorEntry(val name: String, val color: Int)

    private val colors = listOf(
        ColorEntry("Blanc", Color.WHITE),
        ColorEntry("Gris 90%", Color.rgb(230, 230, 230)),
        ColorEntry("Gris 75%", Color.rgb(191, 191, 191)),
        ColorEntry("Gris 50%", Color.rgb(128, 128, 128)),
        ColorEntry("Gris 25%", Color.rgb(64, 64, 64)),
        ColorEntry("Gris 10%", Color.rgb(26, 26, 26)),
        ColorEntry("Noir", Color.BLACK),
        ColorEntry("Gris bleuté", Color.rgb(90, 105, 125)),
        ColorEntry("Rouge", Color.RED),
        ColorEntry("Vert", Color.GREEN),
        ColorEntry("Bleu", Color.BLUE),
        ColorEntry("Jaune", Color.YELLOW),
        ColorEntry("Cyan", Color.CYAN),
        ColorEntry("Magenta", Color.MAGENTA),
    )
    private var index = 0

    private lateinit var root: FrameLayout
    private lateinit var label: TextView
    private val hideLabelHandler = Handler(Looper.getMainLooper())
    private val hideLabelRunnable = Runnable { label.visibility = View.INVISIBLE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        root = FrameLayout(this)
        label = TextView(this).apply {
            textSize = 18f
            setPadding(24, 12, 24, 12)
            setBackgroundColor(Color.argb(140, 0, 0, 0))
        }
        root.addView(
            label,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                marginEnd = 24
                bottomMargin = 24
            }
        )

        setContentView(root)
        hideSystemBars()
        applyColor()
    }

    private fun hideSystemBars() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
    }

    private fun applyColor() {
        val entry = colors[index]
        root.setBackgroundColor(entry.color)
        // Texte noir sur fonds clairs, blanc sur fonds fonces - reste lisible sur toutes les couleurs.
        label.setTextColor(if (isLight(entry.color)) Color.BLACK else Color.WHITE)
        label.text = entry.name
        label.visibility = View.VISIBLE
        hideLabelHandler.removeCallbacks(hideLabelRunnable)
        hideLabelHandler.postDelayed(hideLabelRunnable, 1500)
    }

    private fun isLight(color: Int): Boolean {
        val luminance = 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)
        return luminance > 150
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                index = (index + 1) % colors.size
                applyColor()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP -> {
                index = (index - 1 + colors.size) % colors.size
                applyColor()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }
}
