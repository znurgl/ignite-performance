package znurgl.ignitetest.worker;

import org.jetbrains.annotations.TestOnly;
import org.junit.Test;

/**
 * Created by Gergo Bakos (znurgl@gmail.com) on 18/07/2018.
 */
public class WorkerTest {

    @Test
    public void test_ignite() {
        new Worker().load();
    }

}
