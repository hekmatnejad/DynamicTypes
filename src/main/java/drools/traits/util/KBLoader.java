package drools.traits.util;

import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;


import java.util.List;

/**
 * Created by mamad on 12/11/14.
 */
public class KBLoader {

    public static KieSession createKBfromDrlFile(String drlFileName)
    {
        KieHelper helper = new KieHelper();
        helper.addResource( KieServices.Factory.get().getResources()
                .newClassPathResource(drlFileName), ResourceType.DRL );
        Results res = helper.verify();
        if ( res.hasMessages( Message.Level.ERROR ) ) {

        }
        KieSession ksession = helper.build().newKieSession();
        TraitFactory.setMode(VirtualPropertyMode.MAP, ksession.getKieBase());
        return ksession;
    }
    public static KieSession createKBfromDrlSource(String drlSource)
    {
        KieHelper helper = new KieHelper();
        helper.addContent(drlSource,ResourceType.DRL);
        Results res = helper.verify();
        if ( res.hasMessages( Message.Level.ERROR ) ) {

        }
        KieSession ksession = helper.build().newKieSession();
        TraitFactory.setMode(VirtualPropertyMode.MAP, ksession.getKieBase());
        return ksession;

    }
    public static KieSession createDemoKBfromFile(String drlFileName)
    {
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();

        kfs.write(KieServices.Factory.get().getResources()
                .newClassPathResource(drlFileName, KBLoader.class));

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);

        kbuilder.buildAll();

        List<Message> res = kbuilder.getResults().getMessages(Message.Level.ERROR);
        if(res.size()>0){
            System.out.println("Error in loading DRL file.");
            return null;
        }

        KieBase kbase = KieServices.Factory.get()
                .newKieContainer(kbuilder.getKieModule().getReleaseId())
                .getKieBase();

        KieSession ksession = kbase.newKieSession();
        return ksession;
    }
    /*
    public static void main(String[] args) {
        KieSession ks = createDemoKBfromFile("test.drl");
    }
    */
    @Test
    public void testDSLExpansion_MessageImplNPE() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "dsl-test", "1.0-SNAPSHOT" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/resources/KBase1/test-dsl.dsl", createDSL() )
                .write( "src/main/resources/KBase1/test-rule.dslr", createDRL() );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        if ( !messages.isEmpty() ) {
            for ( final Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
        assertTrue( messages.isEmpty() );
    }

    @Test
    public void testDSLExpansion_NoExpansion() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "dsl-test", "1.0-SNAPSHOT" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/resources/KBase1/test-dsl.dsl", createDSL() )
                .write( "src/main/resources/KBase1/test-rule.drl", createDRL() );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        if ( !messages.isEmpty() ) {
            for ( final Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
        assertFalse( messages.isEmpty() );
    }

    private String createDSL() {
        return "[when]There is a smurf=Smurf()\n";
    }

    private String createDRL() {
        return "package org.kie.test\n" +
                "declare Smurf\n" +
                "    name : String\n" +
                "end\n" +
                "rule Smurfs\n" +
                "when\n" +
                "    There is a smurf\n" +
                "then\n" +
                "    >System.out.println(\"Smurfs rock!\");\n" +
                "end\n";
    }
}
