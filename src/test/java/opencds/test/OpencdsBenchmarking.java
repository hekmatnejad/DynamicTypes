package opencds.test;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AgendaItem;
import org.drools.common.InternalFactHandle;
import org.drools.common.NamedEntryPoint;
import org.drools.common.PropagationContextImpl;
import org.drools.definition.type.FactType;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.reteoo.*;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.junit.Test;
import org.opencds.vmr.v1_0.internal.*;
import org.opencds.vmr.v1_0.internal.concepts.ObservationCodedValueConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationFocusConcept;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLQTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 8/22/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpencdsBenchmarking {

    @Test
    public void testTraitOpencdsBenchmark() {

//        if(true) return;

//        KnowledgeBase kBase = buildKB( "opencds/test/opencdsTraitBenchmark.drl" );
        KnowledgeBase kBase = buildKB( "opencds/test/opencdsBenchmark.drl" );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();


        for ( int j = 0; j < 20; j++ ) {

            ObservationResult obs = new ObservationResult();
            CD cdFocus = new CD();
            cdFocus.setCodeSystem("AHRQ v4.3");
            cdFocus.setCode("10220");
            obs.setId(UUID.randomUUID().toString());
            obs.setObservationFocus(cdFocus);
            obs.setSubjectIsFocalPerson(true);

            CD cdCoded = new CD();
            cdCoded.setCodeSystem("AHRQ v4.3");
            cdCoded.setCode("34254");
            ObservationValue obsValue = new ObservationValue();
            obsValue.setConcept(cdCoded);
            obsValue.setIdentifier(UUID.randomUUID().toString());
            obs.setObservationValue(obsValue);

            knowledgeSession.insert(obs);

        }

        knowledgeSession.fireAllRules();
        knowledgeSession.getAgenda().clear();
        long st = System.currentTimeMillis();


        for ( int j = 0; j < 1000; j++ ) {

            ObservationResult obs = new ObservationResult();
            CD cdFocus = new CD();
            cdFocus.setCodeSystem("AHRQ v4.3");
            cdFocus.setCode("10220");
            obs.setId(UUID.randomUUID().toString());
            obs.setObservationFocus(cdFocus);
            obs.setSubjectIsFocalPerson(true);

            CD cdCoded = new CD();
            cdCoded.setCodeSystem("AHRQ v4.3");
            cdCoded.setCode("34254");
            ObservationValue obsValue = new ObservationValue();
            obsValue.setConcept(cdCoded);
            obsValue.setIdentifier(UUID.randomUUID().toString());
            obs.setObservationValue(obsValue);

            knowledgeSession.insert(obs);

        }

        knowledgeSession.fireAllRules();
        long ed = System.currentTimeMillis();

        System.out.println( ed - st );


    }

    @Test
    public void testTraitOpencdsBenchmark2(){
        String drl = "package opencds.test;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import java.util.*;\n" +
                "\n" +
                "declare ObservationResult\n" +
                "@Traitable\n" +
                "@propertyReactive\n" +
                "end\n" +
                "" +
                "" +
                "declare trait opencdsConcept\n" +
                "@propertyReactive\n" +
                "    openCdsConceptCode : String\n" +
                "    id : String\n" +
                "end\n";


        int maxStep = 1000;
        String trait = "";
        System.out.println("Making DRL file.");
        for(int i=0; i<=maxStep; i++){

            trait +=
                    "declare trait " + "autoTrait001" + i + "  extends opencdsConcept\n" +
                            "@propertyReactive\n" +
                            "    openCdsConceptCode : String = \"C001" + i + "\"\n" +
                            "end\n";
        }

        String rule = "";

        for(int i=0; i<maxStep; i++){

            rule +=
                    "rule \"ObservationFocusConcept by concept 001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult(  $id : id==\"001"+i+"\" || id==\"001"+(i+1)+"\", " +
                            "$code : observationFocus.code == \"10220\",\n" +
                            "        $codeSystem : observationFocus.codeSystem )\n" +
                            "then\n" +
                            "    //System.out.println( \"Claxified \" + $id + \" as auto Trait \" + " + i + " ); \n" +
                            "    Object x1 = don( $obs, autoTrait001"+i+".class );\n" +
                            "end\n";

            rule +=
                    "rule \"IsReportableInfluenza001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : autoTrait001"+i+"(this isA autoTrait001"+(i+1)+".class)\n" +
                            "then\n" +
                            "    //System.out.println(\":: InfluenzaTestForOrganism ResultPositive \"+$obj.getId());\n" +
                            "//System.out.println($obj.getId());\n" +
                            "end\n";
        }

        rule += "rule \"Clean\"\n" +
                "salience -999"+
                "when\n" +
                "  $s : String()\n" +
                "  $o : ObservationResult( )\n" +
                "then\n" +
                "  retract( $o );\n" +
                "//System.out.println(\"000\");\n"+
                "end  ";

        drl += trait + rule;
        System.out.println("Initializing KB.");
        long st2 = System.currentTimeMillis();

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        ksession.fireAllRules();
        ksession.getAgenda().clear();
        long ed2 = System.currentTimeMillis();
        System.out.println( "KB Initialization time : " + (ed2 - st2));


        Collection<FactHandle> handles = new ArrayList<FactHandle>(maxStep);
        Collection<Object> facts = new ArrayList<Object>(maxStep);
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

            facts.add( obs );
            handles.add(ksession.insert(obs));

        }

        ksession.fireAllRules();
        System.out.println(" FIrst run insert + fire : " + (System.currentTimeMillis() - ed2) );



        for ( Object fact : facts ) {
            ksession.insert(fact);
        }
        ksession.fireAllRules();
        FactHandle handle = ksession.insert("clean-all");
        ksession.fireAllRules();
        ksession.retract( handle );
        ksession.fireAllRules();
        assertEquals(0, ksession.getObjects().size());

        //   ksession.addEventListener( new DebugAgendaEventListener( ));

        System.out.println("Instantiating and inserting facts.");
        long st = System.currentTimeMillis();
        for ( Object fact : facts ) {
            ksession.insert(fact);
        }


        long ed3 = System.currentTimeMillis();
        System.err.println( "Second insertion round " + (ed3 - st));
        System.err.println("Firring rules.");
        ksession.fireAllRules();
        long ed = System.currentTimeMillis();
        long ed4 = System.currentTimeMillis();
        System.err.println("Second fire " + (ed4 - ed3));

        System.err.println("Total time: " + (ed - st));
    }

    @Test
    public void testTraitOpencdsBenchmark2Native() throws IllegalAccessException, InstantiationException {

/*
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
*/

        String drl = "package opencds.test;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import org.opencds.vmr.v1_0.internal.datatypes.*;\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
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
                "end\n";


        int maxStep = 1000;
        String trait = "";
        System.out.println("Making DRL file.");
        for(int i=0; i<=maxStep; i++){

            trait +=
                    "declare trait " + "autoTrait001" + i + "  extends opencdsConcept\n" +
                            "@propertyReactive\n" +
                            "    openCdsConceptCode : String = \"C001" + i + "\"\n" +
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
                    "    //System.out.println( \"Claxified \" + $id + \" as auto Trait \" + " + i + " ); \n" +
                    "    Object x1 = don( $obs, autoTrait001"+i+".class );\n" +
                    "    //insert(new CD()); \n" +
                    "end\n" +

                    "rule \"ObservationFocusConcept by concept 002"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obs : ObservationResult2(  $id : id==\"001"+(i+1)+"\" , " +
                    "$code : observationFocus.code == \"10220\",\n" +
                    "        $codeSystem : observationFocus.codeSystem )\n" +
                    "then\n" +
                    "    //System.out.println( \"Claxified \" + $id + \" as auto Trait \" + " + i + " ); \n" +
                    "    Object x1 = don( $obs, autoTrait001"+i+".class );\n" +
                    "    //insert(new CD()); \n" +
                    "end\n";

            rule +=
                    "rule \"IsReportableInfluenza001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : autoTrait001"+i+"(this isA autoTrait001"+(i+1)+".class)\n" +
                    "then\n" +
                    "    //System.out.println(\":: InfluenzaTestForOrganism ResultPositive \"+$obj.getId());\n" +
                    "//System.out.println($obj.getId());\n" +
                    "end\n";

        }

//        rule += "rule \"Clean\"\n" +
//                "salience -999"+
//                "when\n" +
//                "  $s : String()\n" +
//                "  $o : ObservationResult2( )\n" +
//                "then\n" +
//                "  retract( $o );\n" +
//                "//System.out.println(\"000\");\n"+
//                "end  ";
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

        System.out.println("Initializing KB.");
        long st2 = System.currentTimeMillis();

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        ksession.fireAllRules();
        ksession.getAgenda().clear();
        long ed2 = System.currentTimeMillis();
        System.out.println( "KB Initialization time : " + (ed2 - st2));


        Collection<FactHandle> handles = new ArrayList<FactHandle>(maxStep);
        Collection<Object> facts = new ArrayList<Object>(maxStep);
        FactType observationResult = ksession.getKnowledgeBase().getFactType( "opencds.test", "ObservationResult2" );
        for ( int j = 0; j < maxStep; j++ ) {

            Object obs = observationResult.newInstance();
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

        }

        ksession.fireAllRules();
        System.out.println(" First run insert + fire : " + (System.currentTimeMillis() - ed2) );

        for ( int j = 0; j < 10; j++ ) {
            System.out.println("Warmup cycle " + j );
            long start = System.currentTimeMillis();
            for ( Object o : facts ) {
                ksession.insert(o);
            }
            ksession.fireAllRules();
            long end = System.currentTimeMillis();
            System.out.println( "Warmup time " + (end-start) );
            FactHandle c = ksession.insert( "clean-all" );
            ksession.fireAllRules();
            ksession.retract( c );
            ksession.fireAllRules();
            assertEquals(0, ksession.getObjects().size());
        }



        //   ksession.addEventListener( new DebugAgendaEventListener( ));

        System.out.println("Instantiating and inserting facts.");
        long st = System.currentTimeMillis();
        for ( Object fact : facts ) {
            ksession.insert(fact);
        }


        long ed3 = System.currentTimeMillis();
        System.err.println( "Second insertion round " + (ed3 - st));
        System.err.println("Firring rules.");
        int fired = ksession.fireAllRules();
        long ed = System.currentTimeMillis();
        long ed4 = System.currentTimeMillis();
        System.err.println("Second fire ["+ fired +"] " + (ed4 - ed3));

        System.err.println("Total time: " + (ed - st));
    }

    @Test
    public void testTraitOpencdsBenchmark3(){

        String drl = "package opencds.test;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import java.util.*;\n" +
                "\n";
        int maxStep =1000;
        String rule = "";
        System.out.println("Making DRL file.");
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
        System.out.println("Initializing KB.");
        long st2 = System.currentTimeMillis();

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        ksession.fireAllRules();
        ksession.getAgenda().clear();
        long ed2 = System.currentTimeMillis();
        System.out.println((ed2 - st2));

        Collection<Object> facts = new ArrayList<Object>(maxStep);
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

        }

        for ( int j = 0; j < 10; j++ ) {
            System.out.println("Warmup cycle " + j );
            long start = System.currentTimeMillis();
            for ( Object o : facts ) {
                ksession.insert(o);
            }
            ksession.fireAllRules();
            long end = System.currentTimeMillis();
            System.out.println( "Warmup time " + (end-start) );
            FactHandle c = ksession.insert( "clean-all" );
            ksession.fireAllRules();
            ksession.retract( c );
            assertEquals(0, ksession.getObjects().size());
        }


        System.out.println("inserting facts.");
        long st = System.currentTimeMillis();
        for ( Object o : facts ) {
            ksession.insert(o);
        }

        long ed3 = System.currentTimeMillis();
        System.out.println((ed3 - st));
        System.out.println("Firring rules.");
        int fired = ksession.fireAllRules();
        long ed4 = System.currentTimeMillis();
        System.out.println((ed4 - ed3));

        System.out.println("Total time: " + (ed4 - st) + " fired " + fired );

    }

    public static class Counter {
        private int j = 0;
        public void inc() { j++; }
        public String toString() { return "" + j; }
    }

    @Test
    public void testDonPerformance( ) throws IllegalAccessException, InstantiationException, InterruptedException {

        String source = "package org.test; \n" +
                "import org.drools.factmodel.traits.Traitable; \n" +
                "import org.drools.spi.KnowledgeHelper; \n" +
                "import opencds.test.OpencdsBenchmarking.Counter;" +
                "" +
                "global Counter counter; \n" +
                "global KnowledgeHelper helper; \n" +
                "" +
                "declare Person \n" +
                " @Traitable \n" +
                "end \n" +
                "" +
                "declare trait Student \n" +
                "end \n" +
                "declare trait Worker \n" +
                "end \n" +
                "" +
                "rule X when\n" +
                "then\n" +
                " kcontext.getKnowledgeRuntime().setGlobal( \"helper\", drools ); \n" +
                " kcontext.getKnowledgeRuntime().setGlobal( \"counter\", new Counter() ); \n" +
                "end\n" +
                "" +
                "rule Foo when \n" +
                "  Integer() \n" +
                "then \n" +
                "end \n" +
                "" +
                "rule A when Student( this isA Worker.class )  then counter.inc(); end \n" +
                "rule B when Worker() String() then end \n" +
                "";


        StatefulKnowledgeSession ks = loadKnowledgeBaseFromString( source ).newStatefulKnowledgeSession();
        ks.fireAllRules();
        FactHandle handle = ks.insert( "Foo" );

        KnowledgeHelper helper = (KnowledgeHelper) ks.getGlobal( "helper" );

        assertNotNull( helper );

        ReteooRuleBase rb = ((ReteooRuleBase) ((KnowledgeBaseImpl) ks.getKnowledgeBase()).getRuleBase());
        EntryPointNode epn = rb.getAddedEntryNodeCache().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( Integer.class ) );
        LeftInputAdapterNode lia = ( LeftInputAdapterNode) otn.getSinkPropagator().getSinks()[0];
        RuleTerminalNode rtn = (RuleTerminalNode) lia.getSinkPropagator().getSinks()[0];

        org.drools.rule.Rule r = rtn.getRule();
        LeftTuple t = new LeftTupleImpl(  );

        PropagationContext ctx = new PropagationContextImpl(
                42,
                PropagationContext.ASSERTION,
                r,
                t,
                (InternalFactHandle) handle,
                0,
                0,
                ((NamedEntryPoint) ks.getWorkingMemoryEntryPoint( "DEFAULT" )).getEntryPoint()
        );


        AgendaItem item = new AgendaItem( 42, t, 0, ctx, rtn );

        helper.setActivation( item );

        FactType person = ks.getKnowledgeBase().getFactType( "org.test", "Person" );
        FactType student = ks.getKnowledgeBase().getFactType( "org.test", "Student" );
        FactType worker = ks.getKnowledgeBase().getFactType( "org.test", "Worker" );

        Object p = person.newInstance();
        Object st = helper.don( p, student.getFactClass() );


        System.out.println( "Starting" );
        long now = System.currentTimeMillis();
        for ( int j = 0; j < 100000; j++ ) {
            Object q = person.newInstance();
            helper.don( q, student.getFactClass() );
            helper.don( q, worker.getFactClass() );
        }
        ks.fireAllRules();
        System.out.println( "Starting done " + ( System.currentTimeMillis() - now ) );
        System.out.println( ks.getGlobal( "counter" ) );


    }

    private KnowledgeBase buildKB( String drlPath ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newClassPathResource(drlPath), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;
    }

    private KnowledgeBase loadKnowledgeBaseFromString( String drlSource ){
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource(drlSource.getBytes()), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;

    }

}
