/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.opendis.reference.entitygenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Method;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main.java created on Apr 16, 2019 MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class JammerMain
{
    private File outputDirectory;
    private Properties uid2ClassName;
    private Properties interfaceInjection;
    private String packageName;
    private String xmlPath;
    
    private String enumTemplate1;
    private String enumTemplate2;
    private String enumTemplate25;
    private String enumTemplate3_8;
    private String enumTemplate3_16;
    private String enumTemplate3_32;
    private String dictEnumTemplate1;
    private String dictEnumTemplate2;
    private String dictEnumTemplate3;
    private String bitsetTemplate1;
    private String bitsetTemplate15;
    private String bitsetTemplate16;
    private String bitsetTemplate2;
    
    private String entityTemplate1;
    private String entityTemplate2; 
    
    private File entityTypeFactory = null;
    private FileWriter entityFileWriter = null;
    
    private String specTitleDate = null;
    
    public JammerMain(String xmlPath, String outputDir, String packageName)
    {
        this.packageName = packageName;
        this.xmlPath = xmlPath;
        outputDirectory = new File(outputDir);
    }
    
    private static int FORVALUE = 0;
    private static int NAME = 1;
    private static int DESCRIPTION = 2;
    
    private Method[] getEnumMethods(String className) throws Exception
    {
        Method[] ma = new Method[3];

        Class<?> cls = Class.forName(className);
        ma[FORVALUE]    = cls.getDeclaredMethod("getEnumerationForValue", int.class);
        ma[NAME]        = cls.getMethod("name", (Class[])null);
        ma[DESCRIPTION] = cls.getDeclaredMethod("getDescription", (Class[])null);

        return ma;

    }
    
    String getDescription(Method enumGetter, Method descriptionGetter, int i) throws Exception
    {
        Object enumObj = enumGetter.invoke(null, i);
        return (String)descriptionGetter.invoke(enumObj, null);
    }
    
    String getName(Method enumGetter, Method NameGetter, int i) throws Exception
    {
        Object enumObj = enumGetter.invoke(null, i);
        return (String)NameGetter.invoke(enumObj, null);
    }
   
    private void run() throws SAXException, IOException, ParserConfigurationException
    {
        MyHandler handler = new MyHandler();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        factory.newSAXParser().parse(new File(xmlPath), handler);

        //System.out.println("Complete. " + handler.enums.size() + " enums created.");
        /*
        for (EnumElem ee : handler.enums) {
            System.out.println("Created "+ee.uid + " \t" + ee.name);
        }
        
        System.out.println("XML elements other than enum and enumrow :");
        handler.testElems.forEach((s)->System.out.println(s));
        */
    }
    
    private void loadEnumTemplates()
    {
        try {
            enumTemplate1 = loadOneTemplate("disenumpart1.txt");
            enumTemplate2 = loadOneTemplate("disenumpart2.txt");
            enumTemplate25 = loadOneTemplate("disenumpart25.txt");
            enumTemplate3_32 = loadOneTemplate("disenumpart3_32.txt");
            enumTemplate3_16 = loadOneTemplate("disenumpart3_16.txt");
            enumTemplate3_8 = loadOneTemplate("disenumpart3_8.txt");
            dictEnumTemplate1 = loadOneTemplate("disdictenumpart1.txt");
            dictEnumTemplate2 = loadOneTemplate("disdictenumpart2.txt");
            dictEnumTemplate3 = loadOneTemplate("disdictenumpart3.txt");
            entityTemplate1 = loadOneTemplate("entityTypeFactoryTemplate1.txt");
            entityTemplate2 = loadOneTemplate("entityTypeFactoryTemplate2.txt");
            bitsetTemplate1 = loadOneTemplate("disbitset1.txt");
            bitsetTemplate15= loadOneTemplate("disbitset15.txt");
            bitsetTemplate16= loadOneTemplate("disbitset16.txt");
            bitsetTemplate2 = loadOneTemplate("disbitset2.txt");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private String loadOneTemplate(String s) throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(getClass().getResource(s).toURI())));
    }
    
    class JammerKindElem
    {
        String value;
        String description;
        ArrayList<JammerCategoryElem> categories = new ArrayList<>();
    }
    class JammerCategoryElem
    {
        String value;
        String description;
        ArrayList<JammerSubCategoryElem> subcategories = new ArrayList<>();
        JammerKindElem parent;
    }
    class JammerSubCategoryElem
    {
        String value;
        String description;
        ArrayList<JammerSpecificElem> specifics = new ArrayList<>();
        JammerCategoryElem parent;
    }
    class JammerSpecificElem
    {
        String value;
        String description;
        JammerSubCategoryElem parent;
    }
   
    public class MyHandler extends DefaultHandler
    {
        ArrayList<JammerKindElem> entities = new ArrayList<>();
        JammerKindElem currentKind;
        JammerCategoryElem currentCategory;
        JammerSubCategoryElem currentSubCategory;
        JammerSpecificElem currentSpecific;
              
        HashSet<String> testElems = new HashSet<>();
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {           
            switch (qName) {

                case "jammer_kind":
                    currentKind=new JammerKindElem();
                    currentKind.value = attributes.getValue("value");
                    currentKind.description = attributes.getValue("description");
                    entities.add(currentKind);
                    break;
                    
                case "jammer_category":
                    if(currentKind == null)
                        break;
                    currentCategory = new JammerCategoryElem();
                    currentCategory.value = attributes.getValue("value");
                    currentCategory.description = attributes.getValue("description");
                    currentCategory.parent = currentKind;
                    currentKind.categories.add(currentCategory);
                    break;
                    
                case "jammer_subcategory":

                    if(currentCategory == null)
                        break;
                    currentSubCategory = new JammerSubCategoryElem();
                    currentSubCategory.value = attributes.getValue("value");
                    currentSubCategory.description = attributes.getValue("description");
                    currentSubCategory.parent = currentCategory;
                    currentCategory.subcategories.add(currentSubCategory);
                    break;
                    
                case "jammer_specific":
                    if(currentSubCategory == null)
                        break;
                    currentSpecific = new JammerSpecificElem();
                    currentSpecific.value = attributes.getValue("value");
                    currentSpecific.description = attributes.getValue("description");
 
                    currentSpecific.parent = currentSubCategory;
                    currentSubCategory.specifics.add(currentSpecific);
                    break;
                    
                default:
                    testElems.add(qName);

            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        {
            try {
                switch (qName) {
                        
                    case "jammer_kind":
                        if (currentKind != null) {
                            writeBaseClasses(currentKind);
                            currentKind = null;
                        }
                        else
                            System.out.println("endElement with no current element " + qName);
                        break;

                    case "jammer_category":
                        if (currentCategory != null) {
                            if(currentCategory.subcategories.isEmpty())
                                writeFile(currentCategory);
                            else
                                writeBaseClasses(currentCategory);
                            currentCategory = null;
                        }
                        else
                            System.out.println("endElement with no current element " + qName);
                        break;

                    case "jammer_subcategory":
                        if (currentSubCategory != null) {
                            if(currentSubCategory.specifics.isEmpty())
                                writeFile(currentSubCategory);
                            else
                                writeBaseClasses(currentSubCategory);
                            currentSubCategory = null;
                        }
                        else
                            System.out.println("endElement with no current element " + qName);
                        break;

                    case "jammer_specific":
                        if (currentSpecific != null) {
                            writeFile(currentSpecific);
                            currentSpecific = null;
                        }
                        else
                            System.out.println("endElement with no current element " + qName);
                        break;

                    default:
                        testElems.add(qName);
                }
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        
        @Override
        public void endDocument() throws SAXException
        {

        }
        
        private void writeBaseClasses(JammerKindElem ent) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(ent, sb);
            File dir = new File(outputDirectory,sb.toString());
            dir.mkdirs();
            File base = new File(dir,"Base.java");
            base.createNewFile();
            File category = new File(dir, "Category.java");
            category.createNewFile();
            System.out.println(sb.toString());
        }
        
        private void writeBaseClasses(JammerCategoryElem cat) throws Exception
        {
             StringBuilder sb = new StringBuilder();
            buildPackagePath(cat, sb);
            File dir = new File(outputDirectory,sb.toString());
            dir.mkdirs();
            File base = new File(dir,"Base.java");
            base.createNewFile();
            File category = new File(dir, "SubCategory.java");
            category.createNewFile();
        }
        
        private void writeBaseClasses(JammerSubCategoryElem sub) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(sub, sb);
            File dir = new File(outputDirectory,sb.toString());
            dir.mkdirs();
            File base = new File(dir,"Base.java");
            base.createNewFile();
            File category = new File(dir, "Specific.java");
            category.createNewFile();
        }
        
        private void writeFile(JammerSpecificElem spec) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(spec.parent, sb);
            
            File dir = new File(outputDirectory,sb.toString());
            dir.mkdirs();  
            
            File f = new File(dir,fixName(spec.description)+".java");
            f.createNewFile();
            
            //todo fill properly
            
        }
       private void writeFile(JammerSubCategoryElem spec) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(spec.parent, sb);
            
            File dir = new File(outputDirectory,sb.toString());
            dir.mkdirs();  
            
            File f = new File(dir,fixName(spec.description)+".java");
            f.createNewFile();
            
            //todo fill properly
            
        }
        private void writeFile(JammerCategoryElem spec) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(spec.parent, sb);
            
            File dir = new File(outputDirectory,sb.toString());
            dir.mkdirs();  
            
            File f = new File(dir,fixName(spec.description)+".java");
            f.createNewFile();
            
            //todo fill properly
            
        }
                  
        private void buildPackagePath(JammerKindElem kind, StringBuilder sb) throws Exception
        {
            sb.append(fixName(kind.description));
            sb.append("/");         
        }
        
        private void buildPackagePath(JammerCategoryElem cat, StringBuilder sb) throws Exception
        {
           buildPackagePath(cat.parent, sb);
           sb.append(fixName(cat.description));
           sb.append("/");
        }
        
        private void buildPackagePath(JammerSubCategoryElem sub, StringBuilder sb) throws Exception
        {
           buildPackagePath(sub.parent,sb);
           sb.append(fixName(sub.description));
           sb.append("/");
        }
        
        private String buildPackagePath(JammerSpecificElem spec, StringBuilder sb) throws Exception
        {
           buildPackagePath(spec.parent, sb);
           sb.append(fixName(spec.description));
           sb.append("/");
           return sb.toString();

        }
      
        private String fixName(String s)
        {
            String r = s.trim();
            // Convert any of these chars to underbar (u2013 is a hyphen observed in source XML):
            r = r.replaceAll(" ", "");
            
            r = r.replaceAll("[\\h-/,\";:\\u2013]", "_");

            // Remove any of these chars (u2019 is an apostrophe observed in source XML):
            r = r.replaceAll("[()}{}'.#&\\u2019]","");

            // Special case the plus character:
            r = r.replace("+", "PLUS");
            
            // Collapse all contiguous underbars:
            r = r.replaceAll("_{2,}", "_");
            
            // If there's nothing there, put in something:
            if(r.isEmpty() || r.equals("_"))
              r = "undef";
            
            // Java identifier can't start with digit
            if(Character.isDigit(r.charAt(0)))
                r =  "_"+r;
            return r;
        }

        /*
        private String firstCharUpper(String s)
        {
            String ret = s.toLowerCase();
            char[] ca = ret.toCharArray();
            ca[0] = Character.toUpperCase(ca[0]);
            return new String(ca);
        }
          */
        private String indent = "    ";
  /*
        private String makeComment(EntityKind kind, EntityElem elem, CategoryElem category, SubCategoryElem subcategory, SpecificElem specific)
        {
            StringBuilder sb = new StringBuilder();//indent);

            sb.append("UID ");
            sb.append(elem.uid);
            sb.append(", ");
            try {
                sb.append(EntityKind.getEnumerationForValue(Integer.parseInt(elem.kind)).name());
            }
            catch(EnumNotFoundException ex) {
                sb.append("Enum not found");
            }
            sb.append(", ");
            try {
                if (kind == EntityKind.MUNITION)
                    sb.append(MunitionDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).name());
                else if (kind == EntityKind.SUPPLY)
                    sb.append(SupplyDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).name());
                else //if(kind == EntityKind.PLATFORM)
                    sb.append(PlatformDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).name());
                
                sb.append(", ");
                sb.append(Country.getEnumerationForValue(Integer.parseInt(elem.country)).getDescription());
            }
            catch (EnumNotFoundException ex) {
                throw new RuntimeException("missing enum for uid = " + elem.uid);
            }
            sb.append(", ");
            sb.append(category.description);
            sb.append(", ");
            sb.append(subcategory.description);
            sb.append(", ");
            sb.append(specific.description);

            String s = sb.toString();
            if(s.length()>120) {
              String s1 = s.substring(0,120);
              String s2 = s.substring(120);
              s = s1+"\n "+indent+s2;
            }
            return s;

        }
*/
        String maybeSpecialCase(String s, String dflt)
        {
            String lc = s.toLowerCase();
            if (lc.equals("united states"))
                return "USA";
            if (lc.equals("not_used"))
                return "";
            return dflt;
        }

        String smallCountryName(String s, String integ)
        {
            if (integ.equals("0"))
                return "";  // "other

            if (s.length() <= 3)
                return s;
            try {
                s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
                if (s.length() > 3) {
                    return maybeSpecialCase(s, integ);
                }
                return s;
            }
            catch (Exception ex) {
                return integ;
            }
        }
    }

    public static void main(String[] args)
    {
        try {
            new JammerMain(args[0], args[1], args[2]).run();
        }
        catch (SAXException | IOException | ParserConfigurationException ex) {
            ex.printStackTrace();
        }
    }
}
