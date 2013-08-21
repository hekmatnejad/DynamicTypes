package opencds.test;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
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
 * Date: 8/20/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class VmrTemplateTest {

    @Test
    public void testVmrTemplate() {

        KnowledgeBase kBase = buildKB( "opencds/test/vMRtemplate.drl" );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();

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

}
