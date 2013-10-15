package opencds.test;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
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

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 8/5/13
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpencdsRule{

    @Test
    public void testTraitOpencds02() {

//        if(true) return;

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
