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

import java.util.*;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 8/14/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class testTraitMapAlias {
    @Test
    public void testDrools216(){

        StatefulKnowledgeSession ksession = buildKB( "openehr/test/drools-216.drl" ).newStatefulKnowledgeSession();

        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student is donned"));
        assertTrue(list.contains("worker is donned"));

    }

    @Test
    public void testDrools217(){

        StatefulKnowledgeSession ksession = buildKB( "openehr/test/drools-217.drl" ).newStatefulKnowledgeSession();

        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();

        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student is donned"));
        assertTrue(list.contains("worker is donned"));
    }

    @Test
    public void testDrools218(){

        StatefulKnowledgeSession ksession = buildKB( "openehr/test/drools-218.drl" ).newStatefulKnowledgeSession();

        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student is donned"));
        assertTrue(list.contains("worker is donned"));
        assertTrue(list.contains("studentworker is donned"));
        assertTrue(list.contains("tuitionWaiver is true"));
    }

    @Test
    public void testDrools219(){

        StatefulKnowledgeSession ksession = buildKB( "openehr/test/drools-219.drl" ).newStatefulKnowledgeSession();

        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student is donned"));
        assertTrue(list.contains("student has ID and SSN"));
        assertTrue(list.contains("student has personID and socialSecurity"));
        assertTrue(list.contains("citizen has socialSecurity"));
        assertTrue(list.contains("person has personID"));
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
