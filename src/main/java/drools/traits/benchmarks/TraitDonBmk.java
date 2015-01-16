package drools.traits.benchmarks;

import com.jprofiler.api.agent.Controller;
import com.sun.japex.JapexDriver;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import drools.traits.util.KBLoader;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/15/13
 * Time: 1:27 AM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/15/13
 * Time: 1:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class TraitDonBmk extends JapexDriverBase implements JapexDriver {

    private static String drlHeader = "";
    private static String drl = "";
    private static String rule = "";
    private static int maxStep = 100;
    private static KieSession ksession = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);



    @Override
    public void initializeDriver() {
        maxStep = Integer.parseInt(getParam("maxStep"));
        System.out.println("\ninitializeDriver");

        drl = "package drools.traits.benchmarks;\n" +
                "\n" +
                "import org.drools.core.factmodel.traits.Traitable;\n" +
                "" +
                "";


        rule =
                "\n" +
                "rule \"match and don\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $obj : TestClass( $id : hField0 == \"val-000\")    // , hiddenField0 == \"0\" )\n" +
                "then\n" +
//                "    TestTrait tt = don( $obj , TestTrait.class );\n" +
                        "don( $obj , TestTrait.class );\n" +
                "end\n" +
                "\n" +
                "rule \"match trait\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $obj : TestTrait( hField0 == \"val-000\" ) //, sField0 == 0)\n" +
                "then\n" +
                "    //System.out.println($obj);\n" +
                "end\n" +
                "\n" +
                "";
        drlHeader = drl;
    }

    @Override
    public void prepare(TestCase testCase) {
        int hardFieldNum = testCase.getIntParam("HardFieldNum");
        int softFieldNum = testCase.getIntParam("SoftFieldNum");
        int hiddenFieldNum = testCase.getIntParam("HiddenFieldNum");
        String strClass = "";
        String strTrait = "";
        strClass =
                "\n" +
                "declare TestClass\n" +
                "@Traitable\n" +
                "@propertyReactive\n";
        strTrait =
                "\n" +
                "declare trait TestTrait\n" +
                "@propertyReactive\n" ;

        for(int s=0; s< hardFieldNum; s++)
        {
            strClass += "   hField"+s+" : String = \"" + s + "\"\n";
            strTrait += "   hField"+s+" : String = \"" + s + "\"\n";

        }
        for(int hd=0; hd< hiddenFieldNum; hd++)
        {
            strClass += "   hiddenField"+hd+" : String = \"" + hd + "\"\n";
        }
        strClass += "end\n";
        for(int h=0; h< softFieldNum; h++)
        {
            strTrait += "   sField"+h+" : Integer = " + h + "\n";
        }
        strTrait += "end\n" ;

        drl = drlHeader + strClass + strTrait + rule;

//        System.out.println(drl);

        ksession = KBLoader.createKBfromDrlSource(drl);
        ksession.fireAllRules();
        ksession.getAgenda().clear();

        facts = new ArrayList<Object>(maxStep);

        try {
            for ( int j = 0; j < maxStep; j++ ) {
                FactType inputObject = ksession.getKieBase().getFactType( "drools.traits.benchmarks", "TestClass" );

                Object obj = null;
                obj = inputObject.newInstance();
                for(int s = 0; s< hardFieldNum; s++)
                    inputObject.set(obj,"hField"+s,"val-00"+(0));
                facts.add(obj);
                ksession.insert(obj);

//                for ( Field fld : inputObject.getFactClass().getFields() ) {
//                    System.out.println( fld );
//                }
            }
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
        if(testCase.getName().equals("test3"))
        startProfiler();
        int fired = ksession.fireAllRules();
        if(testCase.getName().equals("test3"))
        stopProfiler(getParam("maxStep")+testCase.getName());
        System.out.println(fired);
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
        Controller.saveSnapshot(new File("jprofiler/after_list_trait_Donbmk_"+postfix+".jps"));
        Controller.stopCPURecording();
    }
}
