package opencds.benchmarking;

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


import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/7/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpenCdsBenchmarkingTrait extends JapexDriverBase implements JapexDriver {

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
                "import org.opencds.vmr.v1_0.internal.datatypes.*;\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
//                "import org.drools.factmodel.traits.Thing;\n" +
                "import java.util.*;\n" +
                "\n" +
                "declare ObservationResult2\n" +
                "@Traitable\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "   observationFocus : CD \n " +
                "   subjectIsFocalPerson : boolean \n" +
                "   observationValue : ObservationValue \n" +
                "" +
                "end\n" +
                "" +
                "" +
                "declare trait opencdsConcept\n" +
                "@propertyReactive\n" +
                "    openCdsConceptCode : String\n" +
                "    id : String\n" +
                "end\n" +
                "" +
                "";


        String trait = "";
        for(int i=0; i<=maxStep; i++){

            trait +=

                            "" +
                            "declare trait " + "autoTrait001" + i + "  extends opencdsConcept\n" +
                            "@propertyReactive\n" +
                            "    openCdsConceptCode : String = \"C001" + i + "\"\n" +
                            "end\n"  +
                    "declare trait " + "autoTrait002" + i + "  extends opencdsConcept\n" +
                            "@propertyReactive\n" +
                            "    openCdsConceptCode : String = \"C002" + i + "\"\n" +
                            "end\n";
        }

        String rule = "";

        for(int i=0; i<maxStep; i++){

            rule +=
                    "rule \"ObservationFocusConcept by concept 001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult2(  $id : id==\"001"+i+"\" , " +
                            "    $code : observationFocus.code == \"10220\",\n" +
                            "        $codeSystem : observationFocus.codeSystem )\n" +
                            "then\n" +
                            "    //System.out.println( \"Claxified \" + $id + \" as auto Trait1 \" + " + i + " ); \n" +
                            "    Object x1 = don( $obs, autoTrait001"+i+".class );\n" +
                            "    //insert(new CD()); \n" +
                            "end\n" +

                            "rule \"ObservationFocusConcept by concept 002"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult2(  $id : id==\"001"+(i)+"\" , " +
                            "$code : observationValue.concept.code == \"34254\",\n" +
                            "        $codeSystem : observationFocus.codeSystem )\n" +
                            "then\n" +
                            "    //System.out.println( \"Claxified \" + $id + \" as auto Trait2 \" + " + i + " ); \n" +
                            "    Object x1 = don( $obs, autoTrait002"+(i)+".class );\n" +
                            "    //insert(new CD()); \n" +
                            "end\n";

            rule +=
                    "rule \"IsReportableInfluenza001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
//                            "    $obj : autoTrait001"+i+"($obj2 : this isA autoTrait002"+(i)+".class, $obj3 : getCore())\n" +
                            "    $obj : autoTrait001"+i+"( this isA autoTrait002"+(i)+".class )\n" +
                            "then\n" +
                            "    //System.out.println(\":: InfluenzaTestForOrganism ResultPositive \"+$obj.getId());\n" +
                            "//System.out.println($obj.getId());\n" +
                            " //retract($obj3);\n" +
                            "end\n";

            ///Thing ( this isA autoTrait002 && isA autoTrait001 )


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
                "  //System.out.println(\"removed\");\n"+
                "end  ";

        drl += trait + rule;

        ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        ksession.fireAllRules();
        ksession.getAgenda().clear();



    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        FactType observationResult = ksession.getKnowledgeBase().getFactType( "opencds.test", "ObservationResult2" );
        try {
            for ( int j = 0; j < maxStep; j++ ) {

                Object obs = null;
                obs = observationResult.newInstance();
                CD cdFocus = new CD();
                cdFocus.setCodeSystem("AHRQ v4.3");
                cdFocus.setCode("10220");
                observationResult.set( obs, "id", "001" + Integer.toString(j) );//UUID.randomUUID().toString());
                observationResult.set( obs, "observationFocus", cdFocus );
                observationResult.set( obs, "subjectIsFocalPerson", true );

                CD cdCoded = new CD();
                cdCoded.setCodeSystem("AHRQ v4.3");
                cdCoded.setCode("34254");
                ObservationValue obsValue = new ObservationValue();
                obsValue.setConcept(cdCoded);
                obsValue.setIdentifier(UUID.randomUUID().toString());
                observationResult.set( obs, "observationValue", obsValue );

                facts.add(obs);
                ksession.insert(obs);

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
        assertEquals(0, clearVM2());
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
        assertEquals(0, clearVM2());
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
