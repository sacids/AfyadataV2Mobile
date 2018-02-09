package org.sacids.afyadataV2.externalintents;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import static org.sacids.afyadataV2.externalintents.ExportedActivitiesUtils.clearDirectories;

class ExportedActivityTestRule<A extends Activity> extends ActivityTestRule<A> {

    ExportedActivityTestRule(Class<A> activityClass) {
        super(activityClass);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();

        clearDirectories();
    }

}
