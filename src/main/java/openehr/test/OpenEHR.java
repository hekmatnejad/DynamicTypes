package openehr.test;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import se.cambio.cds.model.facade.execution.vo.ArchetypeReference;
import se.cambio.cds.model.facade.execution.vo.ContainerInstance;
import se.cambio.cds.model.facade.execution.vo.ElementInstance;
import se.cambio.cds.util.CDSTerminologyService;
import se.cambio.cds.util.DVUtil;
import java.util.UUID;

import static junit.framework.Assert.fail;

/**
 * Created by mamad on 3/12/14.
 */
public class OpenEHR {

    @Test
    public void testTraitOpenehr() {


        System.out.println(System.getProperty("user.dir"));

//        KnowledgeBase kBase = buildKB("openehr/test/openehrRule.drl");
        KnowledgeBase kBase = buildKB("openehrRule.drl");
        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());

        ArchetypeReference arRespiration = new ArchetypeReference("EHR","openEHR-EHR-OBSERVATION.respiration.v1","","LAST");
        DvCodedText dv = new DvCodedText("Acute Respiratory Failure","local","at0057");
        ElementInstance elRespiration = new ElementInstance(
                "openEHR-EHR-OBSERVATION.respiration.v1/data[at0001]/events[at0002]/data[at0003]/items[at0009]",
                dv,
                arRespiration,new ContainerInstance("111",null),null);
        ksession.insert(arRespiration);
        ksession.insert(elRespiration);

        ArchetypeReference arDiagnosis = new ArchetypeReference("EHR","openEHR-EHR-EVALUATION.problem-diagnosis.v1","","LAST");
        DvCodedText dv2 = new DvCodedText("Secondary Diagnoses","local","gt0011");
        ElementInstance elDiagnosis = new ElementInstance(
                "openEHR-EHR-EVALUATION.problem-diagnosis.v1/data[at0001]/items[at0002.1]",
                dv2,
                arDiagnosis,new ContainerInstance("111",null),null);
        ksession.insert(arDiagnosis);
        ksession.insert(elDiagnosis);



        DvText dvt = new DvText("sample DvText...");

        ksession.insert(dvt);


        ksession.fireAllRules();

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


/*
ule "openehr-respiratory-failure/gt0005"
salience 1
no-loop true
when
   $archetypeReference1:ArchetypeReference(idDomain=="EHR", idArchetype=="openEHR-EHR-OBSERVATION.respiration.v1", aggregationFunction=="LAST")
      $gt0004:ElementInstance(id=="openEHR-EHR-OBSERVATION.respiration.v1/data[at0001]/events[at0002]/data[at0003]/items[at0009]", archetypeReference==$archetypeReference1)
   $archetypeReference2:ArchetypeReference(idDomain=="CDS", idArchetype=="openEHR-EHR-EVALUATION.recommendation.v1")
      $gt0008:ElementInstance(id=="openEHR-EHR-EVALUATION.recommendation.v1/data[at0001]/items[at0002]", archetypeReference==$archetypeReference2)
   $archetypeReference4:ArchetypeReference(idDomain=="EHR", idArchetype=="openEHR-EHR-EVALUATION.problem-diagnosis.v1", aggregationFunction=="LAST")
      $gt0010:ElementInstance(id=="openEHR-EHR-EVALUATION.problem-diagnosis.v1/data[at0001]/items[at0002.1]", archetypeReference==$archetypeReference4)
   eval($gt0004.hasValue() && $gt0004.equalDV(new DvCodedText("Acute Respiratory Failure","local","at0057")))
   eval($gt0010.hasValue() && $gt0010.isSubClassOf(new DvCodedText("Secondary Diagnoses","local","gt0011")))
then
   $gt0008.setDataValue(new DvText("DenominatorExcludeAcuteRespiratoryFailureSecondaryPOA"));$gt0008.setNullFlavour(null);$executionLogger.addLog(drools, $gt0008);
   modify($gt0008){};
end
*/

}
