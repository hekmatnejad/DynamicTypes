package openehr.test;

import kb.tester.Patient;
import kb.tester.Problem;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.opencds.vmr.v1_0.internal.BodySite;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.ProcedureEvent;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.openehr.rm.datatypes.basic.DataValue;
import org.openehr.rm.datatypes.text.*;
import se.cambio.cds.model.facade.execution.vo.ArchetypeReference;
import se.cambio.cds.model.facade.execution.vo.ContainerInstance;
import se.cambio.cds.model.facade.execution.vo.ElementInstance;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
    public void donMapTest() {

        if(true) return;

        String source = "package openehr.test; \n" +
                "import java.util.*\n;" +
                "" +
                "declare org.drools.factmodel.MapCore end \n" +
                "" +
                "global List list; \n" +
                "" +
                "declare trait PersonMap" +
                "@propertyReactive \n" +
                "   name : String \n" +
                "   age  : int \n" +
                "   height : Double \n" +
                "end\n" +
                "" +
                "" +
                "rule Don \n" +
                "when \n" +
                "  $m : Map( this[ \"age\"] == 18 ) " +
                "then \n" +
                "   don( $m, PersonMap.class );\n" +
                "end \n" +
                "" +
                "rule Log \n" +
                "when \n" +
                "   $p : PersonMap( name == \"john\", age > 10 ) \n" +
                "then \n" +
                "   System.out.println( $p ); \n" +
                "   modify ( $p ) { \n" +
                "       setHeight( 184.0 ); \n" +
                "   }" +
                "   System.out.println( $p ); \n" +
                "end \n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(source).newStatefulKnowledgeSession();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Map map = new HashMap();
        map.put("name", "john");
        map.put( "age", 18 );

        ksession.insert(map);
        ksession.fireAllRules();

        assertTrue(map.containsKey("height"));
        assertEquals(map.get("height"), 184.0);

    }

    @Test
    public void testMapCore2_old(  ) {

        if( true) return;

        String source = "package openehr.test;\n" +
            "\n" +
            "import java.util.*;\n" +
            "global List list;\n " +
            "\n" +
            "declare org.drools.factmodel.MapCore " +
            "end\n" +
            "\n" +
            "global List list; \n" +
            "\n" +
            "declare trait PersonMap\n" +
            "@propertyReactive  \n" +
            "   name : String  \n" +
            "   age  : int  \n" +
            "   height : Double  \n" +
            "end\n" +
            "\n" +
            "declare trait StudentMap\n" +
            "@propertyReactive\n" +
            "   ID : String\n" +
            "   GPA : Double = 3.0\n" +
            "end\n" +
            "\n" +
            "rule Don  \n" +
                "no-loop \n" +
            "when  \n" +
            "  $m : Map( this[ \"age\"] == 18 )\n" +
            "then  \n" +
            "   don( $m, PersonMap.class );\n" +
            "   System.out.println( \"done: PersonMap\" );\n" +
            "\n" +
            "end\n" +
            "\n" +
            "rule Log  \n" +
                "no-loop \n" +
            "when  \n" +
            "   $p : PersonMap( name == \"john\", age > 10 )\n" +
            "then  \n" +
            "   modify ( $p ) {  \n" +
            "       setHeight( 184.0 );  \n" +
            "   }\n" +
            "   System.out.println(\"Log: \" +  $p );\n" +
            "end\n" +
            "" +
            "" +
            "rule Don2\n" +
                "no-loop \n" +
            "salience -1\n" +
            "when\n" +
            "   $m : Map( this[ \"age\"] == 18 ) " +
            "then\n" +
            "   don( $m, StudentMap.class );\n" +
            "   System.out.println( \"done2: StudentMap\" );\n" +
            "end\n" +
            "" +
            "" +
            "rule Log2\n" +
            "salience -2\n" +
            "no-loop\n" +
            "when\n" +
            "   $p : StudentMap( $h : fields[ \"height\" ], GPA >= 3.0 ) " +
            "then\n" +
            "   modify ( $p ) {\n" +
            "       setGPA( 4.0 ),\n" +
            "       setID( \"100\" );\n" +
            "   }\n" +
            "   System.out.println(\"Log2: \" + $p );\n" +
            "end\n" +
            "" +
            "" +
            "\n" +
            "rule Shed1\n" +
                "no-loop \n" +
            "salience -5// it seams that the order of shed must be the same as applying don\n" +
            "when\n" +
            "    $m : PersonMap()\n" +
            "then\n" +
            "   shed( $m, PersonMap.class );\n" +
            "   System.out.println( \"shed: PersonMap\" );\n" +
            "end\n" +
            "\n" +
            "rule Shed2\n" +
            "salience -9\n" +
                "no-loop \n" +
            "when\n" +
            "    $m : StudentMap()\n" +
            "then\n" +
            "   shed( $m, StudentMap.class );\n" +
            "   System.out.println( \"shed: StudentMap\" );\n" +
            "end\n" +
            "\n" +
            "\n";

        StatefulKnowledgeSession ks = loadKnowledgeBaseFromString( source ).newStatefulKnowledgeSession();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

                List list = new ArrayList();
        ks.setGlobal( "list", list );

                Map<String,Object> map = new HashMap<String, Object>(  );
        map.put( "name", "john" );
        map.put( "age", 18 );
        ks.insert( map );

        ks.fireAllRules();


        for ( Object o : ks.getObjects() ) {
                System.err.println( o );
            }

        assertEquals( "100", map.get( "ID" ) );
        assertEquals( 184.0, map.get( "height" ) );
        assertEquals( 4.0, map.get( "GPA" ) );

    }

    @Test
    public void testMapCore2_new(  ) {

        if( true) return;

        String source = "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "global List list;\n " +
                "\n" +
                "declare org.drools.factmodel.MapCore \n" +
                "end\n" +
                "\n" +
                "global List list; \n" +
                "\n" +
                "declare trait PersonMap\n" +
                "@propertyReactive  \n" +
                "   name : String  \n" +
                "   age  : int  \n" +
                "   height : Double  \n" +
                "end\n" +
                "\n" +
                "declare trait StudentMap\n" +
                "@propertyReactive\n" +
                "   ID : String\n" +
                "   GPA : Double = 3.0\n" +
                "end\n" +
                "\n" +
                "rule Don  \n" +
                "when  \n" +
                "  $m : Map( this[ \"age\"] == 18, this[ \"ID\" ] != \"100\" )\n" +
                "then  \n" +
                "   don( $m, PersonMap.class );\n" +
                "   System.out.println( \"done: PersonMap\" );\n" +
                "\n" +
                "end\n" +
                "\n" +
                "rule Log  \n" +
                "when  \n" +
                "   $p : PersonMap( name == \"john\", age > 10 )\n" +
                "then  \n" +
                "   modify ( $p ) {  \n" +
                "       setHeight( 184.0 );  \n" +
                "   }\n" +
                "   System.out.println(\"Log: \" +  $p );\n" +
                "end\n" +
                "" +
                "" +
                "rule Don2\n" +
                "salience -1\n" +
                "when\n" +
                "   $m : Map( this[ \"age\"] == 18, this[ \"ID\" ] != \"100\" ) " +
                "then\n" +
                "   don( $m, StudentMap.class );\n" +
                "   System.out.println( \"done2: StudentMap\" );\n" +
                "end\n" +
                "" +
                "" +
                "rule Log2\n" +
                "salience -2\n" +
                "no-loop\n" +
                "when\n" +
                "   $p : StudentMap( $h : fields[ \"height\" ], GPA >= 3.0 ) " +
                "then\n" +
                "   modify ( $p ) {\n" +
                "       setGPA( 4.0 ),\n" +
                "       setID( \"100\" );\n" +
                "   }\n" +
                "   System.out.println(\"Log2: \" + $p );\n" +
                "end\n" +
                "" +
                "" +
                "\n" +
                "rule Shed1\n" +
                "salience -5// it seams that the order of shed must be the same as applying don\n" +
                "when\n" +
                "    $m : PersonMap()\n" +
                "then\n" +
                "   shed( $m, PersonMap.class );\n" +
                "   System.out.println( \"shed: PersonMap\" );\n" +
                "end\n" +
                "\n" +
                "rule Shed2\n" +
                "salience -9\n" +
                "when\n" +
                "    $m : StudentMap()\n" +
                "then\n" +
                "   shed( $m, StudentMap.class );\n" +
                "   System.out.println( \"shed: StudentMap\" );\n" +
                "end\n" +
                "" +
                "rule Last  \n" +
                "salience -99 \n" +
                "when  \n" +
                "  $m : Map( this not isA StudentMap.class )\n" +
                "then  \n" +
                "   System.out.println( \"Final\" );\n" +
                "   $m.put( \"final\", true );" +
                "\n" +
                "end\n" +
                "\n" +
                "\n";

        StatefulKnowledgeSession ks = loadKnowledgeBaseFromString( source ).newStatefulKnowledgeSession();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        Map<String,Object> map = new HashMap<String, Object>(  );
        map.put( "name", "john" );
        map.put( "age", 18 );
        ks.insert( map );

        ks.fireAllRules();


        for ( Object o : ks.getObjects() ) {
            System.err.println( o );
        }

        assertEquals( "100", map.get( "ID" ) );
        assertEquals( 184.0, map.get( "height" ) );
        assertEquals( 4.0, map.get( "GPA" ) );
        assertEquals( true, map.get( "final" ) );

    }

    @Test
    public void testTraitOpenehr(){

        if(true) return;

        StatefulKnowledgeSession ksession = buildKB( "openehr/test/openehrRule.drl" ).newStatefulKnowledgeSession();

        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase() );


/*
   $archetypeReference1:ArchetypeReference(idDomain=="EHR", idArchetype=="openEHR-EHR-OBSERVATION.respiration.v1", aggregationFunction=="LAST")
      $gt0004:ElementInstance(id=="openEHR-EHR-OBSERVATION.respiration.v1/data[at0001]/events[at0002]/data[at0003]/items[at0009]", archetypeReference==$archetypeReference1)
   $archetypeReference2:ArchetypeReference(idDomain=="CDS", idArchetype=="openEHR-EHR-EVALUATION.recommendation.v1")
      $gt0008:ElementInstance(id=="openEHR-EHR-EVALUATION.recommendation.v1/data[at0001]/items[at0002]", archetypeReference==$archetypeReference2)
   $archetypeReference4:ArchetypeReference(idDomain=="EHR", idArchetype=="openEHR-EHR-EVALUATION.problem-diagnosis.v1", aggregationFunction=="LAST")
      $gt0010:ElementInstance(id=="openEHR-EHR-EVALUATION.problem-diagnosis.v1/data[at0001]/items[at0002.1]", archetypeReference==$archetypeReference4)
*/

        ArchetypeReference arRespiration = new ArchetypeReference("EHR","openEHR-EHR-OBSERVATION.respiration.v1","","LAST");
        //DataValue dv =  new DvCodedText(new String("Acute Respiratory Failure"),new String("local"),new String("at0057"));
//        DvCodedText dv = new DvCodedText("Acute Respiratory Failure","local","at0057");
        DvCodedText dv = new DvCodedText("Acute Respiratory Failure","local","at0057");
        ElementInstance elRespiration = new ElementInstance(
                "openEHR-EHR-OBSERVATION.respiration.v1/data[at0001]/events[at0002]/data[at0003]/items[at0009]",
                dv,
                arRespiration,new ContainerInstance("111",null),null);
        ksession.insert(arRespiration);
        ksession.insert(elRespiration);
//        ksession.insert(dv);

/*
        BodySite body = new BodySite();
        CD bCD = new CD();
        bCD.setCode("10");
        bCD.setCodeSystem("AHRQ v4.3");
        body.setBodySiteCode(bCD);
        ksession.insert(body);
*/


        DvText dvt = new DvText("sample DvText...");

        ksession.insert(dvt);


        //List list = new ArrayList();
        //ksession.setGlobal( "list", list );

//        Map map = new HashMap();
//        map.put("name", "john");
//        map.put( "age", 18 );
//
//        ksession.insert(map);
        ksession.fireAllRules();

        //map.put( "address", "1249 E S" );
        //ksession.insert(map);
        //ksession.fireAllRules();

//        assertTrue(map.containsKey("height"));
//        assertEquals(map.get("height"), 184.0);

    }

    @Test
    public void testTrait(){

        //if(true) return;


        //StatefulKnowledgeSession ksession = buildKB( "openehr/test/tstTrait.drl" ).newStatefulKnowledgeSession();
        StatefulKnowledgeSession ksession = buildKB( "openehr/test/tstMapMemberTrait.drl" ).newStatefulKnowledgeSession();

        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase() );
//        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.TRIPLES, ksession.getKnowledgeBase() );

        //List list = new ArrayList();
        //ksession.setGlobal( "list", list );

//        Map map = new HashMap();
//        map.put("name", "john");
//        map.put( "age", 18 );
//
//        ksession.insert(map);
        ksession.fireAllRules();

        //map.put( "address", "1249 E S" );
        //ksession.insert(map);
        //ksession.fireAllRules();

//        assertTrue(map.containsKey("height"));
//        assertEquals(map.get("height"), 184.0);

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
