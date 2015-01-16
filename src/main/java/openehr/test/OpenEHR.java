package openehr.test;


import drools.traits.util.KBLoader;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.junit.Test;

import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
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

        KieHelper helper = new KieHelper();
        helper.addResource( KieServices.Factory.get().getResources()
                .newClassPathResource("openehrRule.drl"), ResourceType.DRL );

        //helper.addResource(ResourceFactory.newClassPathResource("test.drl"),ResourceType.DRL);

        Results res = helper.verify();
        if ( res.hasMessages( Message.Level.ERROR ) ) {

        }
        KieSession ksession = helper.build().newKieSession();
        TraitFactory.setMode(VirtualPropertyMode.MAP, ksession.getKieBase());
        /*
        String strDrl = "\n" +
                "declare FactA\n" +
                "    fieldB: FactB\n" +
                "end\n" +
                "declare FactB extends FactA end\n" +
                "rule R1 when\n" +
                "   $a : FactA( )\n" +
                "   $b : FactB( this == $a.fieldB )\n" +
                "then\n" +
                "end";
        KieSession ksession = KBLoader.createKBfromDrlSource(strDrl);
        //java.lang.RuntimeException: Unknown resource type: ResourceType = 'Drools Business Rule Language'
          */
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
public static void main(String[] args) {
    OpenEHR openEHR = new OpenEHR();
    openEHR.testTraitOpenehr();
}

}
