package org.ligi.blexplorer

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_with_textview.*
import org.ligi.compat.HtmlCompat
import java.io.IOException

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.activity_with_textview)

        try {
            val open = assets.open("help.html")
            content_text.movementMethod = LinkMovementMethod.getInstance()
            content_text.text = HtmlCompat.fromHtml(open.bufferedReader().readText(), Html.ImageGetter {
                ContextCompat.getDrawable(this, R.drawable.ic_launcher).apply {
                    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                }
            }, null)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}
