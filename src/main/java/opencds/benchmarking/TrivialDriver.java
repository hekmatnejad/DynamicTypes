package opencds.benchmarking;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/7/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
import com.sun.japex.JapexDriver;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

public class TrivialDriver extends JapexDriverBase implements JapexDriver {
    @Override
    public void run(TestCase testCase) {
        byte[] b = new byte[1024 * 10];
        for (int i = 0; i < b.length; i++) {
            b[i] = 0;
        }
    }

    @Override
    public void warmup(TestCase testCase){
        System.out.println("wwwwwwwwwwwwwwwwwww");
    }

}