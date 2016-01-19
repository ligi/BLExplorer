package org.ligi.blexplorer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import java.io.IOException;
import java.io.InputStream;
import org.ligi.axt.AXT;

public class HelpActivity extends AppCompatActivity {

    @Bind(R.id.content_text)
    TextView text;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_with_textview);
        ButterKnife.bind(this);

        try {
            final InputStream open = getAssets().open("help.html");
            text.setGravity(Gravity.CENTER);
            text.setMovementMethod(LinkMovementMethod.getInstance());
            text.setText(Html.fromHtml(AXT.at(open).readToString(), new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(final String source) {
                    Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    return d;
                }
            }, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
