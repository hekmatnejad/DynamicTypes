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
import static junit.framework.Assert.fail;


public class OpenCdsBenchmarkingNative extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 100;
    private static StatefulKnowledgeSession ksession = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);




    @Override
    public void initializeDriver() {
        System.out.println("\ninitializeDriver");

        drl = "package opencds.test;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import java.util.*;\n" +
                "\n";
        String rule = "";
        for(int i=0; i<=maxStep; i++){

            rule +=
                    "rule \"ObservationFocusConcept by concept 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult( $id : id == \"001"+i+"\", $code : observationFocus.code == \"10220\",\n" +
                            "        $codeSystem : observationFocus.codeSystem )\n" +
                            "then\n" +
                            "    ObservationFocusConcept x1 = new ObservationFocusConcept();\n" +
                            "    x1.setOpenCdsConceptCode( \"C261001"+i+"\" );\n" +
                            "    x1.setConceptTargetId( $id );\n" +
                            "    insert( x1 );\n" +
                            "end\n" +
                            "\n" +
                            "rule \"ObservationFocusConcept by concept 2001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult( $id : id == \"001"+i+"\", $code : observationValue.concept.code == \"34254\",\n" +
                            "        $codeSystem : observationValue.concept.codeSystem )\n" +
                            "then\n" +
                            "    ObservationCodedValueConcept x1 = new ObservationCodedValueConcept();\n" +
                            "    x1.setOpenCdsConceptCode( \"C87001"+i+"\" );\n" +
                            "    x1.setConceptTargetId( $id );\n" +
                            "    insert( x1 );\n" +
                            "end\n" +
                            "\n" +
                            "rule \"IsReportableInfluenza001"+i+"\"\n" +
                            "dialect \"java\"\n" +
                            "when\n" +
                            "      $y : ObservationFocusConcept( openCdsConceptCode == \"C261001"+i+"\" )\n" +
                            "      $z : ObservationCodedValueConcept( openCdsConceptCode == \"C87001"+i+"\" )\n" +
                            "      $x : ObservationResult( id == $y.conceptTargetId,\n" +
                            "                              id == $z.conceptTargetId,\n" +
                            "                              id == \"001"+i+"\" \n"+
                            "                               )\n" +
                            "then\n" +
                            "      //System.out.println($x.getId());\n"+
                            "end\n";
        }

        rule += "rule \"Clean\"\n" +
                "salience -999"+
                "when\n" +
                "  $s : String()\n" +
                "  $o : Object( this != $s )\n" +
                "then\n" +
                "  if ( ! $o.getClass().getName().contains( \"Initial\" ) ) { \n" +
                "   retract( $o );\n" +
                "  } \n" +
                "end  ";

        drl += rule;

        ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        ksession.fireAllRules();
        ksession.getAgenda().clear();


    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        for ( int j = 0; j < maxStep; j++ ) {

            ObservationResult obs = new ObservationResult();
            CD cdFocus = new CD();
            cdFocus.setCodeSystem("AHRQ v4.3");
            cdFocus.setCode("10220");
            obs.setId("001" + Integer.toString(j));//UUID.randomUUID().toString());
            obs.setObservationFocus(cdFocus);
            obs.setSubjectIsFocalPerson(true);

            CD cdCoded = new CD();
            cdCoded.setCodeSystem("AHRQ v4.3");
            cdCoded.setCode("34254");
            ObservationValue obsValue = new ObservationValue();
            obsValue.setConcept(cdCoded);
            obsValue.setIdentifier(UUID.randomUUID().toString());
            obs.setObservationValue(obsValue);

            facts.add(obs);
            ksession.insert(obs);

        }
    }

    @Override
    public void warmup(TestCase testCase) {
//        System.out.println("warmup");
        long start = System.nanoTime();
        ksession.fireAllRules();
        assertEquals(0,clearVM2());
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
        assertEquals(0,clearVM2());
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
