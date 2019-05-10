/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.opendis.reference.entitygenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*import edu.nps.moves.dis.enumerations.MunitionDomain;
import edu.nps.moves.dis.enumerations.PlatformDomain;
import edu.nps.moves.dis.enumerations.SupplyDomain;
import edu.nps.moves.dis.enumerations.Country;
import edu.nps.moves.dis.enumerations.EntityKind;
*/
/**
 * Main.java created on Apr 16, 2019 MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Main
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
    
    public Main(String xmlPath, String outputDir, String packageName)
    {
        this.packageName = packageName;
        this.xmlPath = xmlPath;
        outputDirectory = new File(outputDir);
    }
    
    Enum platformDomains;
    Enum countries;
    Enum kinds;
    
    Method platformDomainFromInt;
    Method platformDomainName;
    Method platformDomainDescription;
    Method countryFromInt;
    Method countryName;
    Method countryDescription;
    Method kindFromInt;
    Method kindName;
    Method kindDescription;
    
    Method munitionDomainFromInt;
    Method munitionDomainName;
    Method munitionDomainDescription;
    Method supplyDomainFromInt;
    Method supplyDomainName;
    Method supplyDomainDescription;
    
    // Don't put imports in code for this, needs to have enumerations built first; do it this way
    private void buildKindDomainCountryInstances()
    {
        Method[] ma;
        try {
        ma = getEnumMethods("edu.nps.moves.dis.enumerations.PlatformDomain");
        platformDomainFromInt = ma[FORVALUE];
        platformDomainName = ma[NAME];
        platformDomainDescription = ma[DESCRIPTION];
        
         ma = getEnumMethods("edu.nps.moves.dis.enumerations.Country");
         countryFromInt = ma[FORVALUE];
         countryName = ma[NAME];
         countryDescription = ma[DESCRIPTION];
         
         ma = getEnumMethods("edu.nps.moves.dis.enumerations.EntityKind");
         kindFromInt = ma[FORVALUE];
         kindName = ma[NAME];
         kindDescription = ma[DESCRIPTION];
         
         ma = getEnumMethods("edu.nps.moves.dis.enumerations.MunitionDomain");
         munitionDomainFromInt = ma[FORVALUE];
         munitionDomainName = ma[NAME];
         munitionDomainDescription = ma[DESCRIPTION];
         
          ma = getEnumMethods("edu.nps.moves.dis.enumerations.SupplyDomain");
         supplyDomainFromInt = ma[FORVALUE];
         supplyDomainName = ma[NAME];
         supplyDomainDescription = ma[DESCRIPTION];
        }
        catch(Exception ex) {
            throw new RuntimeException(ex.getLocalizedMessage());
        }
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
/*
        Object blah = ma[FORVALUE].invoke(null, 1);
        String name = (String) ma[NAME].invoke(blah, null);

        String description = (String) ma[DESCRIPTION].invoke(blah, null);
*/
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
        buildKindDomainCountryInstances();
        //uid2ClassName = new Properties();
        //uid2ClassName.load(getClass().getResourceAsStream("Uid2ClassName.properties"));
        //interfaceInjection = new Properties();
        //interfaceInjection.load(getClass().getResourceAsStream("interfaceInjection.properties"));
      //  loadEnumTemplates();

        /*
        for (Entry<Object, Object> ent : uid2ClassName.entrySet()) {
            System.out.println(ent.getKey() + " " + ent.getValue());
        }
        */
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
    /*
    class EnumElem
    {
        String uid;
        String name;
        String size;
        ArrayList<EnumRowElem> elems = new ArrayList<>();
    }
    
    class EnumRowElem
    {
        String value;
        String description;
    }

    class DictionaryElem
    {
        String name;
        String uid;
        ArrayList<DictionaryRowElem> elems = new ArrayList<>();
    }
    
    class DictionaryRowElem
    {
        String value;
        String description;
    }
    */
    class EntityElem
    {
        String kind;
        String domain;
        String country;
        String uid;
        ArrayList<CategoryElem> categories = new ArrayList<>();
    }
    class CategoryElem
    {
        String value;
        String description;
        String uid;
        ArrayList<SubCategoryElem> subcategories = new ArrayList<>();
        EntityElem parent;
    }
    class SubCategoryElem
    {
        String value;
        String description;
        String uid;
        ArrayList<SpecificElem> specifics = new ArrayList<>();
        CategoryElem parent;
    }
    class SpecificElem
    {
        String value;
        String description;
        String uid;
        ArrayList<ExtraElem> extras = new ArrayList<>();
        SubCategoryElem parent;
    }
    class ExtraElem
    {
        String value;
        String description;
        String uid;
        SpecificElem parent;
    }
   
    public class MyHandler extends DefaultHandler
    {
        ArrayList<EntityElem> entities = new ArrayList<>();
        EntityElem currentEntity;
        CategoryElem currentCategory;
        SubCategoryElem currentSubCategory;
        SpecificElem currentSpecific;
        ExtraElem currentExtra;
              
        HashSet<String> testElems = new HashSet<>();
        
        boolean inCot = false;   // we don't want categories, subcategories, etc. from this group
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            //String nuts = attributes.getValue("deprecated");
            //if(qName.equals("category") && "true".equals(attributes.getValue("deprecated")))
            //    return;
            if(inCot)   // don't want anything in this group
                return;
            
            switch (qName) {
                case "cot":
                    inCot=true;
                    break;
                    
                case "entity":
                    currentEntity=new EntityElem();
                    currentEntity.kind = attributes.getValue("kind");
                    currentEntity.domain = attributes.getValue("domain");
                    currentEntity.country = attributes.getValue("country");
                    currentEntity.uid = attributes.getValue("uid");
                    entities.add(currentEntity);
                    break;
                    
                case "category":
                    if(currentEntity == null)
                        break;
                    currentCategory = new CategoryElem();
                    currentCategory.value = attributes.getValue("value");
                    currentCategory.description = attributes.getValue("description");
                    currentCategory.uid = attributes.getValue("uid");
                    currentCategory.parent = currentEntity;
                    currentEntity.categories.add(currentCategory);
                    break;
                    
                case "subcategory":
                case "subcategory_range":
                case "subcategory_xref":
                    if(currentCategory == null)
                        break;
                    currentSubCategory = new SubCategoryElem();
                    if(qName.equals("subcategory_xref"))
                        currentSubCategory.value = attributes.getValue("xref");
                    else
                        currentSubCategory.value = attributes.getValue("value");

                    currentSubCategory.description = attributes.getValue("description");
                    currentSubCategory.uid = attributes.getValue("uid");
                    currentSubCategory.parent = currentCategory;
                    currentCategory.subcategories.add(currentSubCategory);
                    break;
                    
                case "specific":
                case "specific_range":
                    if(currentSubCategory == null)
                        break;
                    currentSpecific = new SpecificElem();
                    currentSpecific.value = attributes.getValue("value");
                    currentSpecific.description = attributes.getValue("description");
                    currentSpecific.uid = attributes.getValue("uid");
                    currentSpecific.parent = currentSubCategory;
                    currentSubCategory.specifics.add(currentSpecific);
                    break;
                    
                case "extra":
                    if(currentSpecific == null)
                        break;
                    currentExtra = new ExtraElem();
                    currentExtra.value = attributes.getValue("value");
                    currentExtra.description = attributes.getValue("description");
                    currentExtra.uid = attributes.getValue("uid");
                    currentExtra.parent = currentSpecific;
                    currentSpecific.extras.add(currentExtra);
                    break;
                    
                default:
                    testElems.add(qName);

            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        {
            if(!qName.equals("cot") && inCot)
                return;

            try {
                switch (qName) {
                    case "cot":
                        inCot = false;
                        break;
                        
                    case "entity":
                        if (currentEntity != null) {
                            writeBaseClasses(currentEntity);
                            currentEntity = null;
                        }
                        else
                            System.out.println("endElement with no current element " + qName);

                        break;

                    case "category":
                        if (currentCategory != null) {
                            writeBaseClasses(currentCategory);
                            currentCategory = null;
                        }
                        else
                            System.out.println("endElement with no current element " + qName);

                        break;

                    case "subcategory":
                    case "subcategory_range":
                        if (currentSubCategory != null) {
                            writeBaseClasses(currentSubCategory);
                            currentSubCategory = null;
                        }
                        else
                            System.out.println("endElement with no current element " + qName);

                        break;

                    case "specific":
                    case "specific_range":
                        if (currentSpecific != null) {
                            if (currentSpecific.extras.isEmpty())
                                writeBaseClasses(currentSpecific);
                            else
                                writeFile(currentSpecific);
                            currentSpecific = null;
                        }
                        else
                            System.out.println("endElement with no current element " + qName);

                        break;

                    case "extra":
                        if (currentExtra != null) {
                            writeFile(currentExtra);
                            currentExtra = null;
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
            if (entityFileWriter != null) {
                try {
                    entityFileWriter.append("}\n");
                    entityFileWriter.flush();
                    entityFileWriter.close();
                }
                catch (IOException ex) {
                }
            }
        }
        
        private void writeBaseClasses(EntityElem ent) throws Exception
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
        
        private void writeBaseClasses(CategoryElem cat) throws Exception
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
        
        private void writeBaseClasses(SubCategoryElem sub) throws Exception
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
        
        private void writeBaseClasses(SpecificElem spec) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(spec, sb);
            
            File dir = new File(outputDirectory,sb.toString());
            dir.mkdirs();
            File base = new File(dir,"Base.java");
            base.createNewFile();
            File category = new File(dir, "Extra.java");
            category.createNewFile();
           
        }
        private void writeFile(SpecificElem spec) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(spec, sb);
            
            File dir = new File(outputDirectory,sb.toString());
            dir.mkdirs();  
            
            File f = new File(dir,fixName(spec.description)+".java");
            f.createNewFile();
            
            //todo fill properly
            
        }
        private void writeFile(ExtraElem extra)
        {
            
        }
        Pattern regex = Pattern.compile("\\((.*?)\\)");
        
        private String buildCountryPackagePart(String s)
        {
            Matcher m = regex.matcher(s);
            String fnd = null;
            while(m.find()) {
                fnd = m.group(1);
                if(fnd.length()==3)
                  break;
                fnd=null;
            }
            if(fnd != null)
                return fnd.toLowerCase();
            
           s = fixName(s);
           s = s.toLowerCase();
           if(s.length()>3)
               return s.substring(0, 3);
           else
               return s;  
        }
        private String buidKindOrDomainPackagePart(String s)
        {
            s = fixName(s);
            s = s.replaceAll("_", "");
            s = s.toLowerCase();
            return s;
        }
            
        //Country, kind, domain 
        private void buildPackagePath(EntityElem ent, StringBuilder sb) throws Exception
        {
           String countryname = Main.this.getName(countryFromInt, countryName, Integer.parseInt(ent.country));
           String countrydesc = Main.this.getDescription(countryFromInt, countryDescription, Integer.parseInt(ent.country));
           countryname = buildCountryPackagePart(countrydesc);
           sb.append(buildCountryPackagePart(countrydesc));
           sb.append("/");
   
           
           String kindname = getName(kindFromInt, kindName, Integer.parseInt(ent.kind));
           String kinddesc = getDescription(kindFromInt, kindDescription, Integer.parseInt(ent.kind));
           kindname = buidKindOrDomainPackagePart(kindname);
           sb.append(buidKindOrDomainPackagePart(kindname));
           sb.append("/");
           
           String domainname;
           String kindnamelc = kindname.toLowerCase();
           
           if(kindnamelc.equals("munition")) 
               domainname = getName(munitionDomainFromInt, munitionDomainName, Integer.parseInt(ent.domain));
           else if(kindnamelc.equals("supply"))
               domainname = getName(supplyDomainFromInt, supplyDomainName, Integer.parseInt(ent.domain));
            else
               domainname = getName(platformDomainFromInt, platformDomainName, Integer.parseInt(ent.domain));
           
           //String domaindesc = getDescription(platformDomainFromInt, platformDomainDescription, Integer.parseInt(ent.domain));
           domainname = buidKindOrDomainPackagePart(domainname);
           sb.append(buidKindOrDomainPackagePart(domainname));
           sb.append("/");
        }
        
        private void buildPackagePath(CategoryElem elm, StringBuilder sb) throws Exception
        {
            try {
           buildPackagePath(elm.parent, sb);
            }
            catch(NullPointerException ex) {
                System.out.println("bp");
            }
           sb.append(fixName(elm.description));
           sb.append("/");
        }
        
        private String buildPackagePath(SubCategoryElem sub, StringBuilder sb) throws Exception
        {
           buildPackagePath(sub.parent,sb);
           sb.append(fixName(sub.description));
           sb.append("/");
           return sb.toString();
          
        }
        
        private String buildPackagePath(SpecificElem spec, StringBuilder sb) throws Exception
        {
           buildPackagePath(spec.parent, sb);
           sb.append(fixName(spec.description));
           sb.append("/");
           return sb.toString();

        }
        // mostly identical to writeOutEnum  
      /*  HashSet<String> dictNames = new HashSet<>();
        private void writeOutDict(DictionaryElem el)
        {
            String clsName = Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null) {
                System.err.println("Didn't find a class name for uid = "+el.uid);
                return;
            }
            StringBuilder sb = new StringBuilder();
            
            // Header section
            String additionalInterface = "";
            String otherIf = interfaceInjection.getProperty(clsName);
            if(otherIf != null)
                additionalInterface = ", "+otherIf;
            
            sb.append(String.format(dictEnumTemplate1, specTitleDate, packageName, "UID "+el.uid, clsName, additionalInterface));
            
            dictNames.clear();
            // enum section
            el.elems.forEach((row) -> {
                String name = row.value.replaceAll("[^a-zA-Z0-9]",""); // only chars and numbers
                if(!dictNames.contains(name)) {
                  sb.append(String.format(dictEnumTemplate2, name, row.description.replace('"','\'')));
                  dictNames.add(name);
                }
                else
                    System.out.println("Duplicate dictionary entry for "+name+" in "+clsName);
            });

            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");
           
            // footer section
            sb.append(String.format(dictEnumTemplate3, clsName));

            // save file
            File target = new File(outputDirectory, clsName + ".java");
            FileWriter fw = null;
            try {
                target.createNewFile();
                fw = new FileWriter(target);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        */
        /*
        private void writeOutBitfield(BitfieldElem el)
        {
           String clsName = Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null)
                return;
            StringBuilder sb = new StringBuilder(); 
            
            sb.append(String.format(bitsetTemplate1, specTitleDate, packageName, "UID "+el.uid, el.size, clsName));
            enumNames.clear();
            el.elems.forEach((row) -> {
                String xrefName = null;
                if(row.xrefclassuid != null)
                    xrefName = Main.this.uid2ClassName.getProperty(row.xrefclassuid);
                if(xrefName != null)
                    sb.append(String.format(bitsetTemplate16, createEnumName(row.name),row.bitposition,row.length,xrefName));
                else
                    sb.append(String.format(bitsetTemplate15, createEnumName(row.name),row.bitposition,row.length));
            });
            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");

            sb.append(String.format(bitsetTemplate2,clsName,clsName,clsName));
            
            // save file
            File target = new File(outputDirectory, clsName + ".java");
            FileWriter fw = null;
            try {
                target.createNewFile();
                fw = new FileWriter(target);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        */
        /*
        HashSet<String> enumNames = new HashSet<>();
        
        private void writeOutEnum(EnumElem el)
        {
            String clsName = Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null)
                return;
            StringBuilder sb = new StringBuilder();
            
            // Header section
            String additionalInterface = "";
            String otherIf = interfaceInjection.getProperty(clsName);
            if(otherIf != null)
                additionalInterface = ", "+otherIf;
            
            sb.append(String.format(enumTemplate1, specTitleDate, packageName, "UID "+el.uid, el.size, clsName, additionalInterface));
            
            enumNames.clear();
            // enum section
            if(el.elems.isEmpty())
                sb.append(String.format(enumTemplate2,"NOT_SPECIFIED","0","undefined by SISO spec"));
            else
                el.elems.forEach((row) -> {
                    sb.append(String.format(enumTemplate2, createEnumName(row.description),row.value,row.description.replace('"','\'')));
                });

            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");
           
            sb.append(String.format(enumTemplate25, clsName, clsName, clsName, clsName));
            
            // footer section
            if(el.size == null)
                el.size = "8"; 
            if(el.size.equals("16"))
              sb.append(String.format(enumTemplate3_16, clsName));
            else if(el.size.equals("32"))             
              sb.append(String.format(enumTemplate3_32, clsName));
            else //if(el == null || el.size == null || el.size.equals("8"))
              sb.append(String.format(enumTemplate3_8, clsName));
           /*
            else {
                System.out.println("no class created for "+clsName+" with marshal size = "+el.size);
                return;
            }

            // save file
            File target = new File(outputDirectory, clsName + ".java");
            FileWriter fw = null;
            try {
                target.createNewFile();
                fw = new FileWriter(target);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
*/
        private void writeOutEntity(EntityElem elem)
        {
          /* try {
                if (entityTypeFactory == null) {
                    entityTypeFactory = new File(outputDirectory, "EntityTypeFactory.java");

                    entityTypeFactory.createNewFile();
                    entityFileWriter = new FileWriter(entityTypeFactory);
                    entityFileWriter.write(String.format(entityTemplate1, "edu.nps.moves.dis.enumerations"));
                }
                
                EntityKind kind = EntityKind.getEnumerationForValue(Integer.parseInt(elem.kind));

                for (CategoryElem category : elem.categories) {
                    for (SubCategoryElem subcategory : category.subcategories) {
                        for (SpecificElem specific : subcategory.specifics) {
                            String methodComment = makeComment(kind, elem, category, subcategory, specific);

                            String methodName1 = firstCharUpper(EntityKind.getEnumerationForValue(Integer.parseInt(elem.kind)).name());
                            String methodName2=null;
                            String countryNameSmall = null;
                            String constructorArg1=null, constructorArg2=null;
                            String constructorArg3 = null;
                            
                            try {
                                if (kind == EntityKind.MUNITION) {
                                    methodName2 = firstCharUpper(MunitionDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).name());
                                }
                                else if (kind == EntityKind.SUPPLY) {
                                    methodName2 = maybeSpecialCase(firstCharUpper(SupplyDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).name()), elem.domain);
                                }
                                else {//if(kind == EntityKind.PLATFORM)
                                    methodName2 = firstCharUpper(PlatformDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).name());
                                }

                                countryNameSmall = smallCountryName(Country.getEnumerationForValue(Integer.parseInt(elem.country)).getDescription(), elem.country);

                                if (kind == EntityKind.MUNITION) {
                                    constructorArg1 = "MunitionDomain";
                                    constructorArg2 = MunitionDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).toString();
                                }
                                else if (kind == EntityKind.SUPPLY) {
                                    constructorArg1 = "SupplyDomain";
                                    constructorArg2 = SupplyDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).toString();
                                }
                                else { //if(kind == EntityKind.PLATFORM
                                    constructorArg1 = "PlatformDomain";
                                    constructorArg2 = PlatformDomain.getEnumerationForValue(Integer.parseInt(elem.domain)).toString();
                                }

                                constructorArg3 = Country.getEnumerationForValue(Integer.parseInt(elem.country)).name();
                            }
                            catch (EnumNotFoundException ex) {
                                throw new RuntimeException("Missing enum for "+methodComment);
                            }
                            
                            entityFileWriter.write(String.format(entityTemplate2,
                                methodComment,
                                methodName1, methodName2, countryNameSmall, category.value, subcategory.value, specific.value, 
                                kind.name(), constructorArg1, constructorArg2, constructorArg3,
                                
                                category.value, category.description,
                                subcategory.value, subcategory.description,
                                specific.value, specific.description)); 
                        }
                    }
                }
            }
            catch (EnumNotFoundException | IOException ex) {
                ex.printStackTrace();
            }
*/
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
            new Main(args[0], args[1], args[2]).run();
        }
        catch (SAXException | IOException | ParserConfigurationException ex) {
            ex.printStackTrace();
        }
    }
}
