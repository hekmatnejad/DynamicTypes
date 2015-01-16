package drools.traits.benchmarks;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/7/13
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */

import com.sun.japex.JapexDriver;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import drools.traits.util.KBLoader;

import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;


public class HighlyUsedJoinNative extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 1000;
    private static KieSession ksession = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);
    double[][] warmups;
    int wCounter = 0;
    int WC = 0;
    int tCounter = -1;
    int TN = 12;




    @Override
    public void initializeDriver() {
        WC = Integer.parseInt(getParam("japex.warmupIterations"));
        warmups = new double[TN][WC];
        System.out.println("\ninitializeDriver");

        drl = "package drools.traits.benchmarks;\n" +
                "\n" +
                "import java.util.*;\n" +
                "\n" +
                "declare InputObject\n" +
                "   id : String \n" +
                "end\n" +
                "\n";
        for(int i=0; i<maxStep; i++){
            drl +=
                "declare JoinC00"+i+"\n" +
                "   hook :  InputObject\n" +
                "end\n" +
                "\n" +
                "";
        }

        String rule = "";
        for(int i=0; i<maxStep; i++){

            rule +=
                    "rule \"Initiate Joins"+i+" \"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00A001\" )\n" +
                            "then\n"+
                            "    JoinC00"+i+" join"+i+" = new JoinC00"+i+"($obj);\n" +
                            "    insert( join"+i+" );\n" +
                            "end\n";
        }


        rule +=
                "rule \"Highly Join Check\"\n" +
                        "no-loop\n" +
                        "when\n" +
                        "    $obj : InputObject( $id : id == \"00A001\" )\n" +
                        "";
        for(int i=0; i<maxStep; i++){

            rule +=
                    "    JoinC00"+i+"( hook == $obj )\n" ;
        }

        rule += "then\n" +
//                "   System.out.println(\">>>fired\");" +
                "end\n";
        drl += rule;

//        System.out.println(drl);

        ksession = KBLoader.createKBfromDrlSource(drl);
        ksession.fireAllRules();
        ksession.getAgenda().clear();


    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        FactType inputObject = ksession.getKieBase().getFactType( "drools.traits.benchmarks", "InputObject" );

        try {
            Object obj = null;
            obj = inputObject.newInstance();
            inputObject.set(obj,"id","00A001");

            facts.add(obj);
            ksession.insert(obj);
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        wCounter = 0;
        tCounter++;

    }

    @Override
    public void warmup(TestCase testCase) {

//        System.out.println("warmup");
        long start = System.nanoTime();
        ksession.fireAllRules();
        warmups[tCounter][wCounter++] += (System.nanoTime()-start)/(double)1e6;
        assertEquals(0,clearVM());
        for ( Object o : facts ) {
            ksession.insert(o);
        }
        System.out.println("WT: "+ (System.nanoTime()-start)/1000000 );
    }

    @Override
    public void run(TestCase testCase) {

        int fired = ksession.fireAllRules();
        System.out.println(fired);
    }

    @Override
    public void finish(TestCase testCase) {

        assertEquals(0,clearVM());
        if(testCase.getName().equals("test12"))
        {
            System.out.println("warmups: ");
            for(int j=0; j<TN; j++)
                for(int i=0; i< WC ; i+=1) {
                    System.out.println(warmups[j][i]);
                }
        }
    }



    private long clearVM()
    {
        Collection<FactHandle> factHandles = ksession.getFactHandles();
        for(FactHandle factHandle : factHandles){
            ksession.retract(factHandle);
        }

        return ksession.getFactCount();
    }
}
