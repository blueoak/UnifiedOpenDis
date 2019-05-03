/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.opendis.reference.enumgenerator;

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
    
    private void run() throws SAXException, IOException, ParserConfigurationException
    {
        uid2ClassName = new Properties();
        uid2ClassName.load(getClass().getResourceAsStream("Uid2ClassName.properties"));
    
        loadEnumTemplates();

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

        System.out.println("Complete. " + handler.enums.size() + " enums created.");
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
    }
    class SubCategoryElem
    {
        String value;
        String description;
        String uid;
        ArrayList<SpecificElem> specifics = new ArrayList<>();
    }
    class SpecificElem
    {
        String value;
        String description;
        String uid;
    }
    class BitfieldElem
    {
        String name;
        String size;
        String uid; 
        ArrayList<BitfieldRowElem> elems = new ArrayList<>();
    }
    class BitfieldRowElem
    {
        String name;
        String bitposition;
        String length="1"; // default
        String description;
        String xrefclassuid;
    }
    public class MyHandler extends DefaultHandler
    {
        ArrayList<EnumElem> enums = new ArrayList<>();
        EnumElem currentEnum;
        EnumRowElem currentEnumRow;
        boolean inCot = false;
        ArrayList<EntityElem> entities = new ArrayList<>();
        EntityElem currentEntity;
        CategoryElem currentCategory;
        SubCategoryElem currentSubCategory;
        SpecificElem currentSpecific;
        ArrayList<DictionaryElem> dictionaries = new ArrayList<>();
        DictionaryElem currentDict;
        DictionaryRowElem currentDictRow;
        
        ArrayList<BitfieldElem> bitfields = new ArrayList<>();
        BitfieldElem currentBitfield;
        BitfieldRowElem currentBitfieldRow;
              
        HashSet<String> testElems = new HashSet<>();
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            String nuts = attributes.getValue("deprecated");
            if(qName.equals("category") && "true".equals(attributes.getValue("deprecated")))
                return;
            
            switch (qName) {
                case "enum":
                    currentEnum = new EnumElem();
                    currentEnum.name = attributes.getValue("name");
                    currentEnum.uid = attributes.getValue("uid");
                    currentEnum.size = attributes.getValue("size");
                    enums.add(currentEnum);
                    break;

                case "enumrow":
                    if(currentEnum == null)
                      break;
                    currentEnumRow = new EnumRowElem();
                    currentEnumRow.description = attributes.getValue("description");
                    currentEnumRow.value = attributes.getValue("value");
                    currentEnum.elems.add(currentEnumRow);
                    break;
                    
                case "bitfield":
                    currentBitfield = new BitfieldElem();
                    currentBitfield.name = attributes.getValue("name");
                    currentBitfield.size = attributes.getValue("size");
                    currentBitfield.uid = attributes.getValue("uid");
                    bitfields.add(currentBitfield);
                    break;
                    
                case "bitfieldrow": 
                    if(currentBitfield == null)
                        break;
                    currentBitfieldRow = new BitfieldRowElem();
                    currentBitfieldRow.name = attributes.getValue("name");
                    currentBitfieldRow.description = attributes.getValue("description");
                    currentBitfieldRow.bitposition = attributes.getValue("bit_position");
                    String len = attributes.getValue("length");
                    if(len != null)
                        currentBitfieldRow.length = len; 
                    currentBitfieldRow.xrefclassuid = attributes.getValue("xref");
                    currentBitfield.elems.add(currentBitfieldRow);
                    break;
                    
                case "dict":
                    currentDict = new DictionaryElem();
                    currentDict.name = attributes.getValue("name");
                    currentDict.uid = attributes.getValue("uid");
                    dictionaries.add(currentDict);
                    break;
                    
                case "dictrow":
                    if(currentDict == null)
                        break;
                    currentDictRow = new DictionaryRowElem();
                    currentDictRow.value = attributes.getValue("value");
                    currentDictRow.description = attributes.getValue("description");
                    currentDict.elems.add(currentDictRow);
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
                    currentSubCategory.specifics.add(currentSpecific);
                    break;
                    
                case "cet":
                case "copyright":
                case "cot": //uid 228
                    inCot=true;
                    break;

                case "ebv":
                case "jammer_category":
                case "jammer_kind":
                case "jammer_subcategory":
                case "jammer_technique": //uid 284
                    break;
                case "revision":
                    if(specTitleDate == null) // assume first encountered is latest
                      specTitleDate  = attributes.getValue("title") + ", " + attributes.getValue("date");
                    break;
                case "revisions":

                default:
                    testElems.add(qName);

            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        {
            switch (qName) {
                case "enum":
                    if(currentEnum != null)
                      writeOutEnum(currentEnum);
                    currentEnum = null;
                    break;
                case "enumrow":
                    currentEnumRow = null;
                    break;
                
                case "bitfield":
                    if(currentBitfield != null)
                        writeOutBitfield(currentBitfield);
                    currentBitfield = null;
                    break;
                case "bitfieldrow":
                    currentBitfieldRow = null;
                    break;
                    
                case "dict":
                    if(currentDict != null)
                        writeOutDict(currentDict);
                    currentDict = null;
                    break;
                case "dictrow":
                    currentDictRow = null;
                    break;
                    
                case "entity":
                    if(currentEntity != null)
                      writeOutEntity(currentEntity);
                    currentEntity=null;
                    break;
                case "category":
                    currentCategory = null;
                    break;
                case "subcategory":
                case "subcategory_range":
                    currentSubCategory = null;
                    break;
                case "specific":
                case "specific_range":
                    currentSpecific = null;
                    break;
                
                case "cet":
                case "copyright":
                    break;
                case "cot": //uid 228
                    inCot=false;
                    break;
                case "ebv":
                    if (entityFileWriter != null) {
                        try {
                            entityFileWriter.append("}\n");
                            entityFileWriter.flush();
                            entityFileWriter.close();
                        }
                        catch (IOException ex) {
                        }
                    }
                    break;
                case "jammer_category":
                case "jammer_kind":
                case "jammer_subcategory":
                case "jammer_technique": //uid 284
                case "revision":               
                case "revisions":

                default:
                    testElems.add(qName);
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
        // mostly identical to writeOutEnum  
        HashSet<String> dictNames = new HashSet<>();
        private void writeOutDict(DictionaryElem el)
        {
            String clsName = Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null) {
                System.err.println("Didn't find a class name for uid = "+el.uid);
                return;
            }
            StringBuilder sb = new StringBuilder();
            
            // Header section
            sb.append(String.format(dictEnumTemplate1, specTitleDate, packageName, "UID "+el.uid, clsName));
            
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
        
        
        HashSet<String> enumNames = new HashSet<>();
        
        private void writeOutEnum(EnumElem el)
        {
            String clsName = Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null)
                return;
            StringBuilder sb = new StringBuilder();
            
            // Header section
            sb.append(String.format(enumTemplate1, specTitleDate, packageName, "UID "+el.uid, el.size, clsName));
            
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
*/
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

        private void writeOutEntity(EntityElem elem)
        {}
       /* private void readWriteOutEntity(EntityElem elem)
        {
            try {
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
        }*/
      
        private String createEnumName(String s)
        {
            String r = s.toUpperCase();
            // Convert any of these chars to underbar (u2013 is a hyphen observed in source XML):
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
                r =  "$"+r;
            
            // Handle multiply defined entries in the XML by appending a digit:
            String origR = r;
            int count=2;
            while(enumNames.contains(r))
                r = origR + "_" + Integer.toString(count++);
            enumNames.add(r);
            return r;
        }
        
        private String firstCharUpper(String s)
        {
            String ret = s.toLowerCase();
            char[] ca = ret.toCharArray();
            ca[0] = Character.toUpperCase(ca[0]);
            return new String(ca);
        }
          
        private String indent = "    ";
      /*  private String makeComment(EntityKind kind, EntityElem elem, CategoryElem category, SubCategoryElem subcategory, SpecificElem specific)
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
