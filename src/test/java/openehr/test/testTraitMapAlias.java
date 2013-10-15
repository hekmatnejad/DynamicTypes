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

        String drl = "" +
                "\n" +
                "/*\n" +
                "  Map accessors and custom evaluators do not work together\n" +
                "  Map( this[ \"x\" ] custOp value ) fails.\n" +
                "  The custom evaluator is passed the map itself, rather than the value\n" +
                "  corresponding to the key.\n" +
                "*/\n" +
                "\n" +
                "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.factmodel.traits.Alias\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore  //what is this for\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare trait Citizen\n" +
                "@Traitable\n" +
                "    citizenship : String = \"Unknown\"\n" +
                "end\n" +
                "\n" +
                "declare trait Student extends Citizen\n" +
                "@propertyReactive\n" +
                "   ID : String = \"412314\" @Alias(\"personID\")\n" +
                "   GPA : Double = 3.99\n" +
                "end\n" +
                "\n" +
                "declare Person\n" +
                "@Traitable\n" +
                "    personID : String\n" +
                "    isStudent : boolean\n" +
                "end\n" +
                "\n" +
                "declare trait Worker\n" +
                "@propertyReactive\n" +
                "    hasBenefits : Boolean = true\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"1\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Person p = new Person(\"1020\",true);\n" +
                "    Map map = new HashMap();\n" +
                "    map.put(\"isEmpty\",true);\n" +
                "    insert(p);\n" +
                "    insert(map);\n" +
                "    list.add(\"initialized\");\n" +
                "end\n" +
                "\n" +
                "rule \"2\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Person(isStudent == true)\n" +
                "    $map : Map(this[\"isEmpty\"] == true)\n" +
                "then\n" +
                "    Student s = don( $stu , Student.class );\n" +
                "    $map.put(\"worker\" , s);\n" +
                "    $map.put(\"isEmpty\" , false);\n" +
                "    update($map);\n" +
                "    System.out.println(\"don: Person -> Student \");\n" +
                "    list.add(\"student is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"3\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map($stu : this[\"worker\"] isA Student.class)\n" +
                "then\n" +
                "    Object obj = don( $map , Worker.class );\n" +
                "    System.out.println(\"don: Map -> Worker : \"+obj);\n" +
                "    list.add(\"worker is donned\");\n" +
                "end\n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();

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

        String drl = "" +
                "\n" +
                "/*\n" +
                "MVEL throws an exception when using bound variables and map accessors\n" +
                "Map( $c : this[ \"x\" ], $c op value )\n" +
                "throws an exception as MVEL tries to compile the constraint\n" +
                "*/\n" +
                "\n" +
                "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.factmodel.traits.Alias\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore  //what is this for\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare trait Citizen\n" +
                "@traitable\n" +
                "    citizenship : String = \"Unknown\"\n" +
                "end\n" +
                "\n" +
                "declare trait Student extends Citizen\n" +
                "@propertyReactive\n" +
                "   ID : String = \"412314\" @Alias(\"personID\")\n" +
                "   GPA : Double = 3.99\n" +
                "end\n" +
                "\n" +
                "declare Person\n" +
                "@Traitable\n" +
                "    personID : String\n" +
                "    isStudent : boolean\n" +
                "end\n" +
                "\n" +
                "declare trait Worker\n" +
                "@propertyReactive\n" +
                "    hasBenefits : Boolean = true\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"1\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Person p = new Person(\"1020\",true);\n" +
                "    Map map = new HashMap();\n" +
                "    map.put(\"isEmpty\",true);\n" +
                "    insert(p);\n" +
                "    insert(map);\n" +
                "    list.add(\"initialized\");\n" +
                "end\n" +
                "\n" +
                "rule \"2\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Person(isStudent == true)\n" +
                "    $map : Map(this[\"isEmpty\"] == true)\n" +
                "then\n" +
                "    Student s = don( $stu , Student.class );\n" +
                "    $map.put(\"worker\" , s);\n" +
                "    $map.put(\"isEmpty\" , false);\n" +
                "    update($map);\n" +
                "    System.out.println(\"don: Person -> Student \");\n" +
                "    list.add(\"student is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"3\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map($stu : this[\"worker\"], $stu isA Student.class)\n" +
                "then\n" +
                "    Object obj = don( $map , Worker.class );\n" +
                "    System.out.println(\"don: Map -> Worker : \"+obj);\n" +
                "    list.add(\"worker is donned\");\n" +
                "end\n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();

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

        String drl = "" +
                "\n" +
                "/*\n" +
                "trait field @aliasing does not work when Maps are traited\n" +
                "*/\n" +
                "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.factmodel.traits.Alias\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore  //what is this for\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare trait Citizen\n" +
                "@traitable\n" +
                "    citizenship : String = \"Unknown\"\n" +
                "end\n" +
                "\n" +
                "declare trait Student extends Citizen\n" +
                "@propertyReactive\n" +
                "   ID : String = \"412314\" @Alias(\"personID\")\n" +
                "   GPA : Double = 3.99\n" +
                "end\n" +
                "\n" +
                "declare Person\n" +
                "@Traitable\n" +
                "    personID : String\n" +
                "    isStudent : boolean\n" +
                "end\n" +
                "\n" +
                "declare trait Worker\n" +
                "@propertyReactive\n" +
                "    //customer : Citizen\n" +
                "    hasBenefits : Boolean = true\n" +
                "end\n" +
                "\n" +
                "declare trait StudentWorker extends Worker\n" +
                "@propertyReactive\n" +
                "    //currentStudent : Citizen @Alias(\"customer\")\n" +
                "    tuitionWaiver : Boolean @Alias(\"hasBenefits\")\n" +
                "end\n" +
                "\n" +
                "rule \"1\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Person p = new Person(\"1020\",true);\n" +
                "    Map map = new HashMap();\n" +
                "    map.put(\"isEmpty\",true);\n" +
                "    insert(p);\n" +
                "    insert(map);\n" +
                "    list.add(\"initialized\");\n" +
                "end\n" +
                "\n" +
                "rule \"2\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Person(isStudent == true)\n" +
                "    $map : Map(this[\"isEmpty\"] == true)\n" +
                "then\n" +
                "    Student s = don( $stu , Student.class );\n" +
                "    $map.put(\"worker\" , s);\n" +
                "    $map.put(\"isEmpty\" , false);\n" +
                "    $map.put(\"hasBenefits\",null);\n" +
                "    update($map);\n" +
                "    System.out.println(\"don: Person -> Student \");\n" +
                "    list.add(\"student is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"3\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map($stu : this[\"worker\"])\n" +
                "    Map($stu isA Student.class, this == $map)\n" +
                "then\n" +
                "    Object obj = don( $map , Worker.class );\n" +
                "    System.out.println(\"don: Map -> Worker : \"+obj);\n" +
                "    list.add(\"worker is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"4\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Student()\n" +
                "then\n" +
                "    Object obj = don( $stu , StudentWorker.class );\n" +
                "    System.out.println(\"don: Map -> StudentWorker : \"+obj);\n" +
                "    list.add(\"studentworker is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"5\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    StudentWorker(tuitionWaiver == true)\n" +
                "then\n" +
                "    System.out.println(\"tuitionWaiver == true\");\n" +
                "    list.add(\"tuitionWaiver is true\");\n" +
                "end\n" +
                "\n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();

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

        String drl = "" +
                "\n" +
                "/*\n" +
                "@aliasing soft fields may fail silently or throw exceptions\n" +
                "the behavior of @alias is undefined for soft fields and may be erratic at best.\n" +
                "*/\n" +
                "\n" +
                "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.factmodel.traits.Alias\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore  //what is this for\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare trait Citizen\n" +
                "    citizenship : String = \"Unknown\"\n" +
                "    socialSecurity : String = \"0\"\n" +
                "end\n" +
                "\n" +
                "declare trait Student extends Citizen\n" +
                "@propertyReactive\n" +
                "   ID : String = \"412314\" @Alias(\"personID\") //notice: by removing this Alias rule \"4\" would be ok\n" +
                "   GPA : Double = 3.99\n" +
                "   SSN : String = \"888111155555\" @Alias(\"socialSecurity\")\n" +
                "end\n" +
                "\n" +
                "declare Person\n" +
                "@Traitable(logical=true)\n" +
                "    personID : String\n" +
                "    isStudent : boolean\n" +
                "end\n" +
                "\n" +
                "rule \"1\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Person p = new Person(\"1020\",true);\n" +
                "    insert(p);\n" +
                "    list.add(\"initialized\");\n" +
                "end\n" +
                "\n" +
                "rule \"2\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Person(isStudent == true)\n" +
                "then\n" +
                "    Student s = don( $stu , Student.class );\n" +
                "    System.out.println(\"don: Person -> Student \" + s);\n" +
                "    list.add(\"student is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"3\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Student(ID == \"412314\", SSN == \"888111155555\")\n" +
                "then\n" +
                "    list.add(\"student has ID and SSN\");\n" +
                "end\n" +
                "\n" +
                "rule \"4\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    Student(fields[\"personID\"] == \"412314\", fields[\"socialSecurity\"] == \"888111155555\")\n" +
                "    //$stu : Student(personID == \"412314\", socialSecurity == \"888111155555\")//notice: compile error\n" +
                "then\n" +
                "    list.add(\"student has personID and socialSecurity\");\n" +
                "end\n" +
                "\n" +
                "rule \"5\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $ctz : Citizen(socialSecurity == \"888111155555\")\n" +
                "then\n" +
                "    list.add(\"citizen has socialSecurity\");\n" +
                "end\n" +
                "\n" +
                "rule \"6\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $p : Person(personID == \"412314\")\n" +
                "then\n" +
                "    list.add(\"person has personID\");\n" +
                "end\n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();

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
