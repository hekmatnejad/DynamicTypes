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
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;


public class HighlyUsedJoinNative extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 500;
    private static StatefulKnowledgeSession ksession = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);




    @Override
    public void initializeDriver() {
        System.out.println("\ninitializeDriver");

        drl = "package opencds.test;\n" +
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

        ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        ksession.fireAllRules();
        ksession.getAgenda().clear();


    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        FactType inputObject = ksession.getKnowledgeBase().getFactType( "opencds.test", "InputObject" );

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
    }


    private KnowledgeBase loadKnowledgeBaseFromString( String drlSource ){
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource(drlSource.getBytes()), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
//            fail( knowledgeBuilder.getErrors().toString() );
            System.err.print( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;

    }

    private long clearVM()
    {
        Collection<FactHandle> factHandles = ksession.getFactHandles();
        for(FactHandle factHandle : factHandles){
            ksession.retract(factHandle);
        }

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
