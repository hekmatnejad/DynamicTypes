package drools.traits.benchmarks;

import com.sun.japex.JapexDriver;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.DefaultFactHandle;
import org.drools.definition.type.FactType;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

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

    private static String drl = "";
    private static String rule = "";
    private static int maxStep = 2;
    private static StatefulKnowledgeSession ksession = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);



    @Override
    public void initializeDriver() {
        System.out.println("\ninitializeDriver");

        drl = "package drools.traits.benchmarks;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "" +
                "";


        rule =
                "\n" +
                "rule \"match and don\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $obj : TestClass( $id : sField0 == \"val-000\" )\n" +
                "then\n" +
                "    TestTrait tt = don( $obj , TestTrait.class );\n" +
                "end\n" +
                "\n" +
                "rule \"match trait\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $obj : TestTrait( sField0 == \"val-000\" , hField0 == 0 )\n" +
                "then\n" +
                "    System.out.println($obj);\n" +
                "end\n" +
                "\n" +
                "";
    }

    @Override
    public void prepare(TestCase testCase) {

        int hardFieldNum = testCase.getIntParam("HardFieldNum");
        int softFieldNum = testCase.getIntParam("SoftFieldNum");
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

        for(int s=0; s< softFieldNum; s++)
        {
            strClass += "   sField"+s+" : String \n";
            strTrait += "   sField"+s+" : String \n";

        }
        strClass += "end\n";
        for(int h=0; h< hardFieldNum; h++)
        {
            strTrait += "   hField"+h+" : Integer = "+h+" \n";
        }
        strTrait += "end\n" ;

        drl += strClass + strTrait + rule;

        System.out.println(drl);

        ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        ksession.fireAllRules();
        ksession.getAgenda().clear();

        facts = new ArrayList<Object>(maxStep);

        try {
            for ( int j = 0; j < maxStep; j++ ) {
                FactType inputObject = ksession.getKnowledgeBase().getFactType( "drools.traits.benchmarks", "TestClass" );

                Object obj = null;
                obj = inputObject.newInstance();
                for(int s = 0; s< softFieldNum; s++)
                    inputObject.set(obj,"sField"+s,"val-00"+(j));
                facts.add(obj);
                ksession.insert(obj);
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

        int fired = ksession.fireAllRules();
        System.out.println(fired);
    }

    @Override
    public void finish(TestCase testCase) {
        assertEquals(0, clearVM());
    }


    private KnowledgeBase loadKnowledgeBaseFromString( String drlSource ){
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource(drlSource.getBytes()), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            System.err.print( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;

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

}
