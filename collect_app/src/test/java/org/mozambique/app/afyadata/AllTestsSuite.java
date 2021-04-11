package org.mozambique.app.afyadata;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mozambique.app.afyadata.activities.MainActivityTest;
import org.mozambique.app.afyadata.utilities.CompressionTest;
import org.mozambique.app.afyadata.utilities.PermissionsTest;
import org.mozambique.app.afyadata.utilities.TextUtilsTest;

/**
 * Suite for running all unit tests from one place
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        //Name of tests which are going to be run by suite
        MainActivityTest.class,
        PermissionsTest.class,
        TextUtilsTest.class,
        CompressionTest.class
})

public class AllTestsSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
