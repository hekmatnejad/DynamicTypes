package drools.traits.benchmarks;

import com.sun.japex.JapexDriver;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import drools.traits.util.KBLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import com.jprofiler.api.agent.Controller;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/30/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicDonMultiObject extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 100;
    private static KieSession ksession = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);



    @Override
    public void initializeDriver() {
        System.out.println("\ninitializeDriver");

        drl = "package opencds.test;\n" +
                "\n" +
                "import org.drools.core.factmodel.traits.Traitable;\n" +
                "import java.util.*;\n" +
                "\n" +
                "declare InputObject\n" +
                "@Traitable\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "end\n" +
                "\n" ;

        for(int i=0; i<maxStep; i++){
            drl +=
                    "declare trait Trait00"+i+"\n" +
                            "@propertyReactive\n" +
                            "end\n" +
                            "\n" ;
        }

        String rule = "";

        rule +=
                "rule \"Initiate Traits\"\n" +
                        "no-loop\n" +
                        "when\n" +
                        "    $obj : InputObject( $id : id == \"00A001\" )\n" +
                        "then\n";
        for(int i=0; i< maxStep; i++)
            rule +=     "    don( $obj, Trait00"+i+".class );\n";
        rule += "end\n";


        drl += rule;
//        System.out.println(drl);
        ksession = KBLoader.createKBfromDrlSource(drl);
        ksession.fireAllRules();
        ksession.getAgenda().clear();



    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        FactType inputObject = ksession.getKieBase().getFactType( "opencds.test", "InputObject" );
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
    }

    @Override
    public void warmup(TestCase testCase) {

//        System.out.println("warmup");
        long start = System.nanoTime();
        ksession.fireAllRules();
        assertEquals(0, clearVM());
        for ( Object o : facts ) {
            ksession.insert(o);
        }
        System.out.println("WT: " + (System.nanoTime() - start) / 1000000);
    }

    @Override
    public void run(TestCase testCase) {

//        if(testCase.getName().equalsIgnoreCase("test3"))
//        startProfiler();
        int fired = ksession.fireAllRules();
        System.out.println(fired);
//        if(testCase.getName().equalsIgnoreCase("test3"))
//        stopProfiler(testCase.getName());
    }

    @Override
    public void finish(TestCase testCase) {

        assertEquals(0, clearVM());
    }


    private long clearVM()
    {
        Collection<DefaultFactHandle> factHandles = ksession.getFactHandles();
        Collection<DefaultFactHandle> cloneFactHandles = new ArrayList<DefaultFactHandle>(factHandles);

        for(DefaultFactHandle factHandle : cloneFactHandles){
            if(factHandle.isTraitable())
                ksession.retract(factHandle);
        }

//        if(ksession.getFactCount()!=0)  {
//            Collection<FactHandle> fh = ksession.getFactHandles();
//            for(FactHandle factHandle : fh){
//                System.out.println("ERRRRRRRRRRRRR");
//                ksession.retract(factHandle);
//            }
//        }
        return ksession.getFactCount();
    }

    private long clearVM2()
    {
        FactHandle c = ksession.insert( "clean-all" );
        ksession.fireAllRules();
        ksession.retract( c );
        ksession.fireAllRules();
        return ksession.getObjects().size();
    }

    private void startProfiler()
    {
        Controller.startCPURecording(true);
        Controller.addBookmark("Start calculating rule-firing-don");
    }

    private void stopProfiler(String postfix)
    {
        Controller.saveSnapshot(new File("jprofiler/after_don_BasicMulti_"+postfix+".jps"));
        Controller.stopCPURecording();
    }
}
