package opencds.test;

import junit.framework.Assert;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.factmodel.FieldDefinition;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 8/27/13
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestTraitsWithPositionalArgs {

    @Test
    public void testTraitWithPositionArgs(){

        KnowledgeBase kBase = buildKB( "opencds/test/testTraitsWithPositionalArgs.drl" );
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        kSession.setGlobal("list", list);

        kSession.fireAllRules();
        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student"));
        assertTrue(list.contains("IR citizen"));
        assertTrue(list.contains("US citizen"));
        assertTrue(list.contains("worker"));
        assertTrue(list.contains("You are working in US as student worker"));
        Assert.assertTrue(list.contains("You are studying and working at ASU"));
    }

    @Test
    public void singlePositionTraitTest(){

/*
        String drl = "package org.drools.test;\n" +
                "\n" +
                "declare trait PosTrait\n" +
                "    field0 : int = 100  //@position(0)\n" +
                "    field1 : int = 101  //@position(1)\n" +
                "    field2 : int = 102  //@position(0)\n" +
                "end\n" +
                "\n" +
                "declare trait MultiInhPosTrait extends PosTrait\n" +
                "    mfield0 : int = 200 //@position(0)\n" +
                "    mfield1 : int = 201 @position(2)\n" +
                "end";
*/

        KnowledgeBase kBase = buildKB( "opencds/test/singlePositionTraitTest.drl" );

        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        FactType type = kBase.getFactType("opencds.test", "PosTrait");
        System.out.println(((FieldDefinition) type.getField("field0")).getIndex());

        List list = new ArrayList();
        kSession.setGlobal("list", list);

        kSession.fireAllRules();
        //assertTrue(list.contains("position order is correct"));
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
