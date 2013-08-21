package opencds.test;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.opencds.vmr.v1_0.internal.*;
import org.opencds.vmr.v1_0.internal.concepts.ObservationCodedValueConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationFocusConcept;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLQTY;

import java.util.UUID;

import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 8/5/13
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpencdsRule {
    @Test
    public void testTraitOpencds02() {

        if(true) return;

        KnowledgeBase kBase = buildKB( "opencds/test/opencdsRule.drl" );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();

        ObservationResult obs = new ObservationResult();
        CD cdFocus = new CD();
        cdFocus.setCodeSystem("AHRQ v4.3");
        cdFocus.setCode("C261");
        obs.setId("261");
        obs.setObservationFocus(cdFocus);
        obs.setSubjectIsFocalPerson(true);

        CD cdCoded = new CD();
        cdCoded.setCodeSystem("AHRQ v4.3");
        cdCoded.setCode("C87");
        ObservationValue obsValue = new ObservationValue();
        obsValue.setConcept(cdCoded);
        obsValue.setIdentifier("87");
        obs.setObservationValue(obsValue);

        //----------------------------------//

        ObservationFocusConcept ofc = new ObservationFocusConcept();
        ofc.setId("261");
        ofc.setOpenCdsConceptCode("C261");
        ofc.setConceptTargetId("261");

        ObservationCodedValueConcept ocvc = new ObservationCodedValueConcept();
        ocvc.setId("87");
        ocvc.setOpenCdsConceptCode("C87");
        ocvc.setConceptTargetId("261");

//        knowledgeSession.insert(ofc);
//        knowledgeSession.insert(ocvc);
        //----------------------------------//

        knowledgeSession.insert(obs);
        knowledgeSession.insert(obsValue);

        knowledgeSession.fireAllRules();

/*
        map.put( "C238", InpatientEncounter.class );
        map.put( "C284", AcuteRespiratoryFailure.class );
        map.put( "C417", Secondary.class );
        map.put( "C439", DxPOA.class );
 */
//    $obs : EncounterEvent( $id : id, $code : encounterType.code, $codeSystem : encounterType.codeSystem)
        CD cdCoded2 = new CD();
        cdCoded2.setCodeSystem("AHRQ v4.3");
        cdCoded2.setCode("C238"); //C238 = Inpatient Encounter
        EncounterEvent elementEvt = new EncounterEvent();
        elementEvt.setEncounterType(cdCoded2);
        elementEvt.setId("238");
//    $obs : Problem( $id : id, $code : problemCode.code, $codeSystem : problemCode.codeSystem)
        CD cdCoded3 = new CD();
        cdCoded3.setCodeSystem("AHRQ v4.3");
        cdCoded3.setCode("C284"); //C284 = Acute Respiratory Failure
        Problem problem = new Problem();
        problem.setProblemCode(cdCoded3);
        problem.setId("284");
//    $obs : Problem( $id : id, $code : importance.code, $codeSystem : importance.codeSystem)
        CD cdCoded4 = new CD();
        cdCoded4.setCodeSystem("AHRQ v4.3");
        cdCoded4.setCode("C417"); //C417 = Secondary
        problem.setImportance(cdCoded4);

 //    $obs : ClinicalStatementRelationship( $id : id, $code : targetRelationshipToSource.code, $codeSystem : targetRelationshipToSource.codeSystem)
        ClinicalStatementRelationship clinicRel = new ClinicalStatementRelationship();
        CD cdCoded5 = new CD();
        cdCoded5.setCodeSystem("AHRQ v4.3");
        cdCoded5.setCode("C439"); //C439 = Dx POA
        clinicRel.setTargetRelationshipToSource(cdCoded5);
        clinicRel.setTargetId(problem.getId());
        clinicRel.setId("439");
        clinicRel.setSourceId( elementEvt.getId());

        knowledgeSession.insert(elementEvt);
        //knowledgeSession.insert(problem);
        knowledgeSession.insert(clinicRel);
        knowledgeSession.insert(problem);

        knowledgeSession.fireAllRules();

    }

    @Test
    public void testTraitOpencdsBenchmark() {

//        if(true) return;

        KnowledgeBase kBase = buildKB( "opencds/test/opencdsTraitBenchmark.drl" );
//        KnowledgeBase kBase = buildKB( "opencds/test/opencdsBenchmark.drl" );

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
                "import java.util.*;\n" +
                "\n" +
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
                    "    $obs : ObservationResult(  $id : id==\"001"+i+"\" || id==\"001"+(i+1)+"\", $code : observationFocus.code == \"10220\",\n" +
                    "        $codeSystem : observationFocus.codeSystem )\n" +
                    "then\n" +
                    "    Object x1 = don( $obs, autoTrait001"+i+".class );\n" +
                    "end\n";

            rule +=
                    "rule \"IsReportableInfluenza001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : autoTrait001"+i+"(this isA autoTrait001"+(i+1)+".class)\n" +
                    "then\n" +
                    "//System.out.println(\":: InfluenzaTestForOrganism ResultPositive \"+$obj.getId());\n" +
                    "end\n";
        }

        drl += trait + rule;
        System.out.println("Initializing KB.");
        long st2 = System.currentTimeMillis();

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        ksession.fireAllRules();
        ksession.getAgenda().clear();
        long ed2 = System.currentTimeMillis();
        System.out.println((ed2 - st2));
        System.out.println("Instantiating and inserting facts.");
        long st = System.currentTimeMillis();

        for ( int j = 0; j < 1000; j++ ) {

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

            ksession.insert(obs);

        }
        long ed3 = System.currentTimeMillis();
        System.out.println((ed3 - ed2));
        System.out.println("Firring rules.");
        ksession.fireAllRules();
        long ed = System.currentTimeMillis();
        long ed4 = System.currentTimeMillis();
        System.out.println((ed4 - ed3));

        System.out.println("Total time: " + (ed - st));

    }


    @Test
    public void testTraitOpencds() {

        KnowledgeBase kBase = buildKB( "opencds/test/opencdsRule.drl" );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        IVLQTY ivlqty = new IVLQTY();

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

        knowledgeSession.fireAllRules();

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
