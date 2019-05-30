/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.opendis.reference.entitygenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

public class ObjectTypeMain
{
    private final File outputDirectory;
    private final String basePackageName;
    private final String xmlPath;

    private final FileWriter entityFileWriter = null;

    String classTemplate;
    String categoryBaseTemplate;
    String enumTemplate;
    String entityTypeTemplate;

    String cotBaseTemplate;
    String objectBaseTemplate;
    
    public ObjectTypeMain(String xmlPath, String outputDir, String packageName)
    {
        this.basePackageName = packageName;
        this.xmlPath = xmlPath;
        outputDirectory = new File(outputDir);
    }

    Enum platformDomains;
    Enum countries;
    Enum kinds;

    Method platformDomainFromInt;
    Method platformDomainName;
    Method platformDomainDescription;
    
    Method objKindFromInt;
    Method objKindName;
    Method objKindDescription;

    // Don't put imports in code for this, needs to have enumerations built first; do it this way
    private void buildKindDomainCountryInstances()
    {
        Method[] ma;
        try {
            ma = getEnumMethods("edu.nps.moves.dis.enumerations.PlatformDomain");
            platformDomainFromInt = ma[FORVALUE];
            platformDomainName = ma[NAME];
            platformDomainDescription = ma[DESCRIPTION];

            ma = getEnumMethods("edu.nps.moves.dis.enumerations.ObjectKind");
            objKindFromInt = ma[FORVALUE];
            objKindName = ma[NAME];
            objKindDescription = ma[DESCRIPTION];
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }
    private static final int FORVALUE = 0;
    private static final int NAME = 1;
    private static final int DESCRIPTION = 2;

    private Method[] getEnumMethods(String className) throws Exception
    {
        Method[] ma = new Method[3];

        Class<?> cls = Class.forName(className);
        ma[FORVALUE] = cls.getDeclaredMethod("getEnumerationForValue", int.class);
        ma[NAME] = cls.getMethod("name", (Class[]) null);
        ma[DESCRIPTION] = cls.getDeclaredMethod("getDescription", (Class[]) null);

        return ma;
    }

    String getDescription(Method enumGetter, Method descriptionGetter, int i) throws Exception
    {
        Object enumObj = getEnum(enumGetter, i);
        return (String) descriptionGetter.invoke(enumObj, (Object[]) null);
    }

    String getName(Method enumGetter, Method nameGetter, int i) throws Exception
    {
        Object enumObj = getEnum(enumGetter, i);
        return (String) nameGetter.invoke(enumObj, (Object[]) null);
    }

    Object getEnum(Method enumGetter, int i) throws Exception
    {
        return enumGetter.invoke(null, i);
    }

    private void run() throws SAXException, IOException, ParserConfigurationException
    {
        loadEnumTemplates();
        buildKindDomainCountryInstances();

        MyHandler handler = new MyHandler();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        factory.newSAXParser().parse(new File(xmlPath), handler);
    }

    private void loadEnumTemplates()
    {
        try {
            classTemplate = loadOneTemplate("class.txt");
            categoryBaseTemplate = loadOneTemplate("base.txt");
            enumTemplate = loadOneTemplate("enum.txt");
            cotBaseTemplate = loadOneTemplate("objecttypebase.txt");
            objectBaseTemplate = loadOneTemplate("objecttypeobj.txt");           
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String loadOneTemplate(String s) throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(getClass().getResource(s).toURI())));
    }

    class DescriptionElem
    {
        String description;
        String pkgFromDescription;
        String enumFromDescription;
    }
    class CotElem extends DescriptionElem
    {
        String uid;
        String name;
        String baseClassName;
        ArrayList<ObjectElem> objects = new ArrayList<>();
    }
    class ObjectElem extends DescriptionElem
    {
        String domain;
        String kind;
        CotElem parent;
        ArrayList<CategoryElem> categories = new ArrayList<>();
    }

    class CategoryElem extends DescriptionElem
    {
        String value;
        ArrayList<SubCategoryElem> subcategories = new ArrayList<>();
        ObjectElem parent;
    }

    class SubCategoryElem extends DescriptionElem
    {
        String value;
        CategoryElem parent;
    }

    public class MyHandler extends DefaultHandler
    {
        CotElem currentCot;
        ObjectElem currentObject;
        CategoryElem currentCategory;
        SubCategoryElem currentSubCategory;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {

            switch (qName) {
                case "cot":
                    currentCot = new CotElem();
                    currentCot.name = attributes.getValue("name");
                    currentCot.uid = attributes.getValue("uid");
                    currentCot.description = currentCot.name; 
                    setUniquePkgAndEnum(currentCot, (List) currentCot.objects);
                    currentCot.baseClassName = specialCaseObjectTypeName(currentCot.pkgFromDescription);
                    break;

                case "object":
                    if(currentCot==null)
                        break;
                    currentObject = new ObjectElem();
                    currentObject.domain = attributes.getValue("domain");
                    currentObject.kind = attributes.getValue("kind");
                    currentObject.description = attributes.getValue("description");
                    setUniquePkgAndEnum(currentObject, (List) currentObject.categories);
                    currentObject.parent = currentCot;
                    currentCot.objects.add(currentObject);
                    break;

                case "category":
                    if (currentObject == null)
                        break;

                    currentCategory = new CategoryElem();
                    currentCategory.value = attributes.getValue("value");
                    currentCategory.description = attributes.getValue("description");
                    setUniquePkgAndEnum(currentCategory, (List) currentObject.categories);
                    currentCategory.parent = currentObject;
                    currentObject.categories.add(currentCategory);
                    break;

                case "subcategory":
                    if (currentCategory == null)
                        break;
                    currentSubCategory = new SubCategoryElem();
                    currentSubCategory.value = attributes.getValue("value");
                    currentSubCategory.description = attributes.getValue("description");
                    setUniquePkgAndEnum(currentSubCategory, (List) currentCategory.subcategories);
                    currentSubCategory.parent = currentCategory;
                    currentCategory.subcategories.add(currentSubCategory);
                    break;

                default:
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        {
            try {
                switch (qName) {
                    case "cot":
                        if (currentCot != null) {
                            if (!currentCot.objects.isEmpty())
                                writeBaseClasses(currentCot);
                             currentCot = null;
                        }
                        break;


                    case "object":
                        if (currentObject != null) {
                            if (!currentObject.categories.isEmpty())
                                writeBaseClasses(currentObject);
                            currentObject = null;
                        }
                        break;

                    case "category":
                        if (currentCategory != null) {
                            if (currentCategory.subcategories.isEmpty())
                                writeFile(currentCategory);
                            else
                                writeBaseClasses(currentCategory);
                            currentCategory = null;
                        }
                        break;

                    case "subcategory":
                        if (currentSubCategory != null) {
                            writeFile(currentSubCategory);
                            currentSubCategory = null;
                        }
                        break;

                    default:
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
    }

    private void setUniquePkgAndEnum(DescriptionElem elem, List<DescriptionElem> lis)
    {
        String mangledDescription = fixName(elem.description);
        mangledDescription = makeUnique(mangledDescription, lis);
        elem.pkgFromDescription = mangledDescription;
        elem.enumFromDescription = mangledDescription.toUpperCase();
    }

    private String makeUnique(String s, List<DescriptionElem> lis)
    {
        String news = s;
        for (int i = 1; i < 1000; i++) {
            outer:
            {
                for (DescriptionElem hd : lis) {
                    if (hd.pkgFromDescription.equalsIgnoreCase(news))
                        break outer;
                }
                return news;
            }
            news = s + i;
        }
        throw new RuntimeException("Problem generating unique name for " + s);
    }

    private void writeBaseClasses(CotElem cot) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(cot, sb);
        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();  
        String pkg = basePackageName + "." + pathToPackage(sb.toString()); 
        
        String contents = String.format(cotBaseTemplate, pkg, cot.baseClassName, cot.uid, cot.baseClassName);
        saveFile(dir, cot.baseClassName+".java", contents);
    }
    
    private String specialCaseObjectTypeName(String s)
    {
        switch(s.toLowerCase()) {
            case "objecttypes_arealobject":
                return "ArealObject";
            case "objecttypes_linearobject":
                return "LinearObject";
            case "objecttypes_pointobject":
                return "PointObject";
            default:
                return s;
        }
    }
    
    private void writeBaseClasses(ObjectElem obj) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(obj, sb);
        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();  
         String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String pkgBackOne = parentPackage(pkg);
        
        int objindInt = Integer.parseInt(obj.kind);
        String objKindNm =  getName(objKindFromInt, objKindName, objindInt);
        int platDomainInt = Integer.parseInt(obj.domain);
        String platDomainNm = getName(platformDomainFromInt, platformDomainName, platDomainInt);
        
        String contents = String.format(objectBaseTemplate, pkg, pkgBackOne, obj.parent.baseClassName, obj.parent.baseClassName, platDomainNm, objKindNm);      
        saveFile(dir, "Base.java", contents);

        if (obj.categories.isEmpty())
            return;
        sb.setLength(0);

        obj.categories.forEach((cat) -> {
            String nm = cat.enumFromDescription;
            if (!(nm.equals("DEPRECATED"))) {           
                sb.append("    public static final byte ");
                sb.append(nm);
                sb.append(" = ");
                sb.append(possiblyCast2Byte(cat.value));
                sb.append("; // ");
                sb.append(cat.description); //.replace('"', '\''));
                sb.append("\n");
            }
        });
        sb.setLength(sb.length() - 2);

        contents = String.format(enumTemplate, pkg, "Category", sb.toString());
        saveFile(dir, "Category.java", contents);
    }

    private void writeBaseClasses(CategoryElem cat) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(cat, sb);
        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String pkgBackOne = parentPackage(pkg);
        String subnm = cat.enumFromDescription;

        String contents = String.format(categoryBaseTemplate, pkg, pkgBackOne, "Category", pkgBackOne + ".Base", "Category", "Category", subnm);
        saveFile(dir, "Base.java", contents);

        if (cat.subcategories.isEmpty())
            return;
        sb.setLength(0);

        cat.subcategories.forEach((subcat) -> {
            String nm = subcat.enumFromDescription;
            if (!(nm.equals("DEPRECATED"))) {
                sb.append("    public static final byte ");
                sb.append(nm);
                sb.append(" = ");
                sb.append(possiblyCast2Byte(subcat.value));
                sb.append("; // ");
                sb.append(subcat.description); //.replace('"', '\''));
                sb.append("\n");
            }
        });
        sb.setLength(sb.length() - 2);

        contents = String.format(enumTemplate, pkg, "SubCategory", sb.toString());
        saveFile(dir, "SubCategory.java", contents);
    }
    
    private String possiblyCast2Byte(String s)
    {
        int i = Integer.parseInt(s);
        return (i > Byte.MAX_VALUE)? "(byte)"+s : s;
    }
    
    private void writeFile(CategoryElem cat) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(cat.parent, sb);

        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String classNm = fixName(cat.description);
        String specEnum = classNm.toUpperCase();

        String contents = String.format(classTemplate, pkg, classNm, classNm, "Category", "Category", specEnum);
        File f = saveFile(dir, classNm + ".java", contents);
        System.out.println("Wrote "+f.getPath());
    }

    private void writeFile(SubCategoryElem extra) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(extra.parent, sb);

        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String classNm = fixName(extra.description);
        String specEnum = classNm.toUpperCase();

        String contents = String.format(classTemplate, pkg, classNm, classNm, "Subcategory", "SubCategory", specEnum);
        File f = saveFile(dir, classNm + ".java", contents);
        System.out.println("Wrote "+f.getPath());
    }

    private File saveFile(File parentDir, String name, String contents)
    {
        // save file
        File target = new File(parentDir, name);
        try {
            target.createNewFile();
            try (FileWriter fw = new FileWriter(target)) {
                fw.write(contents);
                fw.flush();
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error saving "+name+": "+ex.getLocalizedMessage(), ex);
        }
        return target;
    }

    private void buildPackagePath(CotElem cot, StringBuilder sb) throws Exception
    {
      sb.append(cot.pkgFromDescription);
      sb.append("/");
                
    }
    private void buildPackagePath(ObjectElem obj, StringBuilder sb) throws Exception
    {
        buildPackagePath(obj.parent, sb);

        sb.append(obj.pkgFromDescription);
        sb.append("/");
    }
    
    private void buildPackagePath(CategoryElem elm, StringBuilder sb) throws Exception
    {
        buildPackagePath(elm.parent, sb);
        sb.append(elm.pkgFromDescription);
        sb.append("/");
    }

    private String pathToPackage(String s)
    {
        s = s.replace("/", ".");
        if (s.endsWith("."))
            s = s.substring(0, s.length() - 1);
        return s;
    }

    private String parentPackage(String s)
    {
        return s.substring(0, s.lastIndexOf('.'));
    }

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

    private String fixName(String s)
    {
        String r = s.trim();
        // Convert any of these chars to underbar (u2013 is a hyphen observed in source XML):
        r = r.replaceAll(" ", "");

        r = r.replaceAll("[\\h-/,\";:\\u2013]", "_");

        // Remove any of these chars (u2019 is an apostrophe observed in source XML):
        r = r.replaceAll("[\\[\\]()}{}'.#&\\u2019]", "");

        // Special case the plus character:
        r = r.replace("+", "PLUS");

        // Collapse all contiguous underbars:
        r = r.replaceAll("_{2,}", "_");

        r = r.replace("<=", "LTE");
        r = r.replace("<", "LT");
        r = r.replace(">=", "GTE");
        r = r.replace(">", "GT");
        r = r.replace("=", "EQ");
        r = r.replace("%", "pct");

        // If there's nothing there, put in something:
        if (r.isEmpty() || r.equals("_"))
            r = "undef";

        // Java identifier can't start with digit
        if (Character.isDigit(r.charAt(0)))
            r = "_" + r;
        return r;
    }

    public static void main(String[] args)
    {
        try {
            new ObjectTypeMain(args[0], args[1], args[2]).run();
        }
        catch (SAXException | IOException | ParserConfigurationException ex) {
            System.err.println(ex.getClass().getSimpleName()+": "+ex.getLocalizedMessage());
        }
    }
}
