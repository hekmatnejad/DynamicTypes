package opencds.test;

import kb.tester.*;
import kb.tester.Problem;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.opencds.common.terminology.OpenCDSConceptTypes;
import org.opencds.vmr.v1_0.internal.*;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

import java.io.ByteArrayInputStream;
import java.util.*;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 5/22/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleTest {

    @Test
    public void testCDS() {

        //if(true) return;

        KnowledgeBase kBase = buildKB( "opencds/test/opencdsRule.drl" );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();

        ProcedureEvent proc = new ProcedureEvent();
        proc.setProcedureTime(new IVLDate());
        proc.setId("1001");
        CD pCD = new CD();
        pCD.setCode("20");
        pCD.setCodeSystem("AHRQ v4.3");
        proc.setProcedureCode(pCD);

        BodySite body = new BodySite();
        CD bCD = new CD();
        bCD.setCode("10");
        bCD.setCodeSystem("AHRQ v4.3");
        body.setBodySiteCode(bCD);
        proc.setApproachBodySite(body);

        BodySite bodyTarget = new BodySite();
        CD bCD2 = new CD();
        bCD2.setCode("11");
        bCD2.setCodeSystem("AHRQ v4.3");
        bodyTarget.setBodySiteCode(bCD2);
        proc.setTargetBodySite(bodyTarget);

        knowledgeSession.insert(proc);
        knowledgeSession.insert(body);
        knowledgeSession.insert(bodyTarget);

        knowledgeSession.fireAllRules();

        System.out.println( proc.isToBeReturned() );

    }

    @Test
    public void testSampleCDS() {

        if(true) return;

        KnowledgeBase kBase = buildKB( "opencds/test/opencdsRuleSample.drl" );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();

        ObservationResult obs = new ObservationResult();
        CD cdFocus = new CD();
        cdFocus.setCodeSystem("AHRQ v4.3");
        cdFocus.setCode("C261");
        obs.setObservationFocus(cdFocus);

        CD cdCoded = new CD();
        cdCoded.setCodeSystem("AHRQ v4.3");
        cdCoded.setCode("C87");
        ObservationValue obsValue = new ObservationValue();
        obsValue.setConcept(cdCoded);
        obs.setObservationValue(obsValue);

        knowledgeSession.insert(obs);
        knowledgeSession.insert(obsValue);

        knowledgeSession.fireAllRules();

        //System.out.println( proc.isToBeReturned() );

    }

    @Test
    public void testDrools(){

        if(true) return;

        KnowledgeBase kBase = buildKB( "opencds/test/tstDrools.drl" );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();

        Patient p1 = new Patient("John");
        kb.tester.Problem d1 = new  kb.tester.Problem("Cold");
        d1.setSeverity(Problem.Severity.MODERATE);
        p1.addProblem(d1);
        kb.tester.Problem d2 = new  kb.tester.Problem("Asthma");
        p1.addProblem(d2);
        kb.tester.Problem d3 = new  kb.tester.Problem("Diabetes Mellitus");
        p1.addProblem(d3);
        knowledgeSession.insert(p1);
        List<String> counter = new LinkedList<String>();
        knowledgeSession.setGlobal("counter", counter);
        knowledgeSession.fireAllRules();

/*
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        counter = (LinkedList<String>)knowledgeSession.getGlobal("counter");
        System.out.println("counter: " + counter);
*/
    }

    private KnowledgeBase buildKB( String drlPath ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newClassPathResource( drlPath ), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;
    }

}
