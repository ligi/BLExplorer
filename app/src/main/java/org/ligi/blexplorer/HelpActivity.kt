package org.ligi.blexplorer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_with_textview.*
import org.ligi.axt.AXT
import java.io.IOException

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.activity_with_textview)

        try {
            val open = assets.open("help.html")
            content_text.gravity = Gravity.CENTER
            content_text.movementMethod = LinkMovementMethod.getInstance()
            content_text.text = Html.fromHtml(AXT.at(open).readToString(), Html.ImageGetter {
                val d = resources.getDrawable(R.drawable.ic_launcher)
                d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
                d
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
