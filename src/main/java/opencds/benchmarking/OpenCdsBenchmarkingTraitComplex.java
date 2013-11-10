package opencds.benchmarking;

import com.jprofiler.api.agent.Controller;
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
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/7/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpenCdsBenchmarkingTraitComplex extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 500;
    private static StatefulKnowledgeSession ksession = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);



    @Override
    public void initializeDriver() {
        System.out.println("\ninitializeDriver");

        drl = "package opencds.test;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import org.opencds.vmr.v1_0.internal.datatypes.*;\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import java.util.*;\n" +
                "" +
                "declare InputObject\n" +
                "@Traitable\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "   sID : String\n" +
                "   tID : String\n" +
                "end\n" +
                "" +
                "declare trait RelationTrait\n" +
                "@propertyReactive\n" +
                "    sID : String @position(0)\n" +
                "    tID : String @position(1)\n" +
                "end\n" +
                "" +
                "declare trait TA\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "end\n" +
                "\n" +
                "declare trait TB extends RelationTrait\n" +
                "@propertyReactive\n" +
                "   //id : String \n" +
                "end\n" +
                "\n" +
                "declare trait TC\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "end\n" +
                "\n" +
                "declare trait TD\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "end\n";

        String rule = "";

        for(int i=0; i<maxStep; i++){

            rule +=
                            "rule \"TA 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00A"+i+"\" )\n" +
                            "then\n" +
                            "    TA ta = don( $obj , TA.class );\n" +
                            "end\n" +
                            "\n" +
                            "rule \"TB 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00B"+i+"\" )\n" +
                            "then\n" +
                            "    TB tb = don( $obj , TB.class );\n" +
                            "end\n" +
                            "\n" +
                            "rule \"TC 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                            "then\n" +
                            "    TC tc = don( $obj , TC.class );\n" +
                            "end\n" +
                            "\n" +
                            "\n" +
                            "rule \"TD 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                            "then\n" +
                            "    TD td = don( $obj , TD.class );\n" +
                            "end\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "rule \"FinalCheck"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $x : TA( $ta := id == \"00A"+i+"\" )\n" +
                            "    $y : TB( $ta, $tc; )\n" +
                            "    $z : TC( $tc := id == \"00C"+i+"\", this isA TD )\n" +
                            "then\n" +
                            "      //System.out.println($x);\n"+
                            "      //System.out.println($y);\n"+
                            "      //System.out.println($z);\n"+
                            "      //System.out.println();\n"+
                            "end\n" +
                                    "";
        }

        drl += rule;

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
            for ( int j = 0; j < maxStep; j++ ) {

                Object obj = null;
                obj = inputObject.newInstance();
                inputObject.set(obj,"id","00A"+j);
                facts.add(obj);
                ksession.insert(obj);
                obj = inputObject.newInstance();
                inputObject.set(obj,"id","00B"+j);
                inputObject.set(obj,"sID","00A"+j);
                inputObject.set(obj,"tID","00C"+j);
                facts.add(obj);
                ksession.insert(obj);
                obj = inputObject.newInstance();
                inputObject.set(obj,"id","00C"+j);
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
    private void startProfiler()
    {
        Controller.startCPURecording(true);
        Controller.addBookmark("Start calculating rule-firing-don");
    }

    private void stopProfiler(String postfix)
    {
        Controller.saveSnapshot(new File("jprofiler/after_list_trait_opencdsbmk_"+postfix+".jps"));
        Controller.stopCPURecording();
    }

}
