package org.ligi;

import android.support.test.rule.ActivityTestRule;
import com.jraska.falcon.FalconSpoon;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.blexplorer.HelpActivity;

public class TheHelpActivity {

    @Rule
    public ActivityTestRule<HelpActivity> rule = new ActivityTestRule<>(HelpActivity.class);

    @Test
    public void testThatHelpOpens() {
        FalconSpoon.screenshot(rule.getActivity(), "help");
    }
}
