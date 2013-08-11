package openehr.util;

import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.ontology.ArchetypeTerm;
import org.openehr.am.archetype.ontology.OntologyDefinitions;
import org.openehr.rm.support.identification.ArchetypeID;
import se.acode.openehr.parser.ADLParser;
import se.acode.openehr.parser.ParseException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 8/8/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpenehrArchetype {

    private OntologyDefinitions ontologyDef = null;
    private Archetype archetype = null;
    private ArchetypeID archetypeId = null;

    public String getArchetypeName() {
        return archetypeName;
    }

    public ArchetypeID getArchetypeId() {
        return archetypeId;
    }

    private String archetypeName = null;

    public OpenehrArchetype(String archetypeFileName) {

        String ext = archetypeFileName.substring(archetypeFileName.length()-4,archetypeFileName.length());
        if(!ext.equalsIgnoreCase(".adl"))
            archetypeFileName += ".adl";

        try {
            ADLParser adlParser = new ADLParser(ClassLoader.getSystemResourceAsStream(archetypeFileName));
            archetype = adlParser.parse();
            archetypeId = archetype.getArchetypeId();
            archetypeName = archetypeId.getValue();
            System.out.println("<"+archetypeId+"> loaded.");

            List<OntologyDefinitions> odl = archetype.getOntology().getTermDefinitionsList();
            Iterator<OntologyDefinitions> oi = odl.iterator();
            while(oi.hasNext()){
                OntologyDefinitions od = oi.next();
                if(od.getLanguage().equalsIgnoreCase("en")){
                    ontologyDef = od;
                    break;
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getItemPath(String nodeCode){
        return archetype.getPathByNodeId(nodeCode);
    }

    public String getItemCode(String nodePath){
        return (((CComplexObject) archetype.getPathNodeMap().get(
                nodePath.substring(nodePath.indexOf('/')))).getNodeId());
    }

    private String getPathWithoutArchetypeName(String nodePath){
        if(nodePath.startsWith("/")){
            return nodePath;
        }

        return nodePath.substring(nodePath.indexOf('/'));
    }

    public String getItemName(String nodePath){

        String elPath = getPathWithoutArchetypeName(nodePath);// nodePath.substring(nodePath.indexOf('/'));
        Map elMap = archetype.getPathNodeMap();
        String itemName = "";

        if(ontologyDef != null){
            List<ArchetypeTerm> arTerm = ontologyDef.getDefinitions();
            Iterator<ArchetypeTerm> it = arTerm.iterator();
            while(it.hasNext()){
                ArchetypeTerm at = it.next();
                if(at.getCode().equalsIgnoreCase(((CComplexObject) elMap.get(elPath)).getNodeId())){
                    itemName = at.getText();
                    break;
                }
            }

            String[] splits = itemName.split(" ");
            itemName = "";
            for(int i=0; i< splits.length; i++){
                itemName += splits[i].substring(0,1).toUpperCase()+
                        splits[i].substring(1).toLowerCase();
            }
            //System.out.println("<><><START><><>");
            //System.out.println(archetype.getDefinition().getRmTypeName());
            //System.out.println(((CComplexObject) elMap.get(elPath)).getRmTypeName());
            //System.out.println(((CComplexObject) elMap.get(elPath)).getAttributes().get(0).getRmAttributeName());
            //System.out.println(itemName);
            //System.out.println("<><><END><><>");
            itemName = archetype.getDefinition().getRmTypeName() +"_"+
                    ((CComplexObject) elMap.get(elPath)).getRmTypeName() +"_"+
                    itemName;
        }
        return itemName;
    }

    public static void main(String[] args)
    {
        OpenehrArchetype atUtil = new OpenehrArchetype("openEHR-EHR-OBSERVATION.respiration.v1.adl");
//        System.out.println(atUtil.getItemPath("at0004"));
//        System.out.println(atUtil.getItemCode("/data[at0001]/events[at0002]/data[at0003]/items[at0004]"));
        System.out.println(atUtil.getItemName("openEHR-EHR-OBSERVATION.respiration.v1/data[at0001]/events[at0002]/data[at0003]/items[at0004]"));
        System.out.println(atUtil.getItemName(atUtil.getItemPath("at0005")));


        atUtil = new OpenehrArchetype("openEHR-EHR-EVALUATION.problem.v1.adl");
        System.out.println(atUtil.getItemName(atUtil.getItemPath("at0012")));
        System.out.println(atUtil.getItemName(atUtil.getItemPath("at0028")));
    }
}
