package opencds.benchmarking;

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
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;


public class OpenCdsBenchmarkingNativeComplex extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 100;
    private static StatefulKnowledgeSession ksession = null;
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

        drl = "package opencds.test;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import java.util.*;\n" +
                "\n" +
                "declare InputObject\n" +
                "   id : String \n" +
                "end\n" +
                "\n" +
                "declare CA\n" +
                "   tid : String \n" +
                "end\n" +
                "\n" +
                "declare CB\n" +
                "   tid : String \n" +
                "end\n" +
                "\n" +
                "declare CC\n" +
                "   tid : String \n" +
                "   xid : String \n" +
                "end\n" +
                "\n" +
                "declare CD\n" +
                "   tid : String \n" +
                "   sid : String \n" +
                "end\n" +
                "\n" +
                "declare CCA\n" +
                "   id : String \n" +
                "end\n" +
                "\n" +
                "declare CCB\n" +
                "   id : String \n" +
                "end\n" +
                "\n" +
                "declare CCC\n" +
                "   id : String \n" +
                "end\n" +
                "";

        String rule = "";
        for(int i=0; i<=maxStep; i++){

            rule +=
                    "rule \"CA 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00A"+i+"\" )\n" +
                    "then\n" +
                    "    CA ca = new CA();\n" +
                    "    ca.setTid( $id );\n" +
                    "    insert( ca );\n" +
                    "end\n" +
                    "\n" +
                    "rule \"CB 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00B"+i+"\" )\n" +
                    "then\n" +
                    "    CB cb = new CB();\n" +
                    "    cb.setTid( $id );\n" +
                    "    insert( cb );\n" +
                    "end\n" +
                    "\n" +
                    "rule \"CC 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                    "then\n" +
                    "    CC cc = new CC();\n" +
                    "    cc.setTid( $id );\n" +
                    "    cc.setXid( \"00B"+i+"\" );\n" +
                    "    insert( cc );\n" +
                    "end\n" +
                    "\n" +
                    "\n" +
                    "rule \"CD 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                    "then\n" +
                    "    CD cd = new CD();\n" +
                    "    cd.setTid( \"00B"+i+"\" );\n" +
                    "    cd.setSid( \"00A"+i+"\" );\n" +
                    "    insert( cd );\n" +
                    "end\n" +
                    "\n" +
                    "rule \"CCA 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00A"+i+"\" )\n" +
                    "then\n" +
                    "    CCA ca = new CCA();\n" +
                    "    ca.setId( $id );\n" +
                    "    insert( ca );\n" +
                    "end\n" +
                    "\n" +
                    "rule \"CCB 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00B"+i+"\" )\n" +
                    "then\n" +
                    "    CCB ca = new CCB();\n" +
                    "    ca.setId( $id );\n" +
                    "    insert( ca );\n" +
                    "end\n" +
                    "\n" +
                    "rule \"CCC 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                    "then\n" +
                    "    CCC ca = new CCC();\n" +
                    "    ca.setId( $id );\n" +
                    "    insert( ca );\n" +
                    "end\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "rule \"FinalCheck"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $x : CA( $ca : tid == \"00A"+i+"\" )\n" +
                    "    $y : CB( $cb : tid == \"00B"+i+"\" )\n" +
                    "    $z : CC( $cc : tid, xid == $cb )\n" +
                    "    $w : CD( sid == $ca, tid == $cb)\n" +
                    "    $x2 : CCA( $cca : id == \"00CA"+i+"\" )\n" +
                    "    $y2 : CCB( $ccb : id == \"00CB"+i+"\" )\n" +
                    "    $z2 : CCC( $ccc : id == \"00CC"+i+"\" )\n" +
                    "then\n" +
                    "      //System.out.println($w);\n"+
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
                for(int i=0; i< WC ; i+=4) {
                    System.out.println(warmups[j][i]);
                }
        }
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
