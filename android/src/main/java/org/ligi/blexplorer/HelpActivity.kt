package org.ligi.blexplorer

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.ligi.blexplorer.databinding.ActivityWithTextviewBinding
import org.ligi.compat.HtmlCompat
import java.io.IOException

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWithTextviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityWithTextviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val open = assets.open("help.html")
            binding.contentText.movementMethod = LinkMovementMethod.getInstance()
            binding.contentText.text = HtmlCompat.fromHtml(open.bufferedReader().readText(), Html.ImageGetter {
                ContextCompat.getDrawable(this, R.drawable.ic_launcher)?.apply {
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
