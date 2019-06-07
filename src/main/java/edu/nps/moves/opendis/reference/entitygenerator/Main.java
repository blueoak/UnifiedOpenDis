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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main.java created on Apr 16, 2019 MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class Main
{
    private final File outputDirectory;
    private final String basePackageName;
    private final String xmlPath;

    private final FileWriter entityFileWriter = null;

    String entityClassTemplate;
    String entityBaseTemplate;
    String entityEnumTemplate;
    String entityTypeTemplate;

    public Main(String xmlPath, String outputDir, String packageName)
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
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        
        System.out.println("listing uids: ");
        factory.newSAXParser().parse(new File(xmlPath), new UidListHandler());
        
        loadEnumTemplates();
        buildKindDomainCountryInstances();
        System.out.println("Generating entities: ");
        factory.newSAXParser().parse(new File(xmlPath), new MyHandler());
    }

    private void loadEnumTemplates()
    {
        try {
            entityClassTemplate = loadOneTemplate("class.txt");
            entityBaseTemplate = loadOneTemplate("base.txt");
            entityEnumTemplate = loadOneTemplate("enum.txt");
            entityTypeTemplate = loadOneTemplate("entitytype.txt");
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
        ArrayList<DescriptionElem> children = new ArrayList<>();
        String value;
        String uid;
    }

    class EntityElem
    {
        String kind;
        String domain;
        String country;
        String uid;
        ArrayList<CategoryElem> categories = new ArrayList<>();
    }

    class CategoryElem extends DescriptionElem
    {
        //ArrayList<SubCategoryElem> subcategories = new ArrayList<>();
        EntityElem parent;
    }

    class SubCategoryElem extends DescriptionElem
    {
        //ArrayList<SpecificElem> specifics = new ArrayList<>();
        CategoryElem parent;
    }

    class SpecificElem extends DescriptionElem
    {
        //ArrayList<ExtraElem> extras = new ArrayList<>();
        SubCategoryElem parent;
    }

    class ExtraElem extends DescriptionElem
    {
        SpecificElem parent;
    }

    public class UidListHandler extends DefaultHandler
    {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            String uid = attributes.getValue("uid");
            if(uid != null) {
                String name = attributes.getValue("name");
                System.out.println(uid+"\t"+name+"\t("+qName+")");
            }
        }
    }
    
    public class MyHandler extends DefaultHandler
    {
        ArrayList<EntityElem> entities = new ArrayList<>();
        EntityElem currentEntity;
        CategoryElem currentCategory;
        SubCategoryElem currentSubCategory;
        SpecificElem currentSpecific;
        ExtraElem currentExtra;

        boolean inCot = false;   // we don't want categories, subcategories, etc. from this group
        boolean inCetUid30 = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            if (inCot)   // don't want anything in this group
                return;

            if (qName.equalsIgnoreCase("cet")) {
                if (attributes.getValue("uid").equalsIgnoreCase("30"))
                    inCetUid30 = true;
                return;
            }

            if (!inCetUid30) // only want entities in this group, uid 30
                return;

            if (attributes.getValue("deprecated") != null)
                return;

            switch (qName) {
                case "cot":
                    inCot = true;
                    break;

                case "entity":
                    currentEntity = new EntityElem();
                    currentEntity.kind = attributes.getValue("kind");
                    currentEntity.domain = attributes.getValue("domain");
                    currentEntity.country = attributes.getValue("country");
                    currentEntity.uid = attributes.getValue("uid");
                    entities.add(currentEntity);
                    break;

                case "category":
                    if (currentEntity == null)
                        break;

                    currentCategory = new CategoryElem();
                    currentCategory.value = attributes.getValue("value");
                    currentCategory.description = attributes.getValue("description");
                    setUniquePkgAndEmail(currentCategory, (List) currentEntity.categories);
                    currentCategory.uid = attributes.getValue("uid");
                    currentCategory.parent = currentEntity;
                    currentEntity.categories.add(currentCategory);
                    break;

                case "subcategory_xref":
                    printUnsupportedMessage("subcategory_xref", attributes.getValue("description"), currentCategory);
                    break;

                case "subcategory_range":
                    printUnsupportedMessage("subcategory_range", attributes.getValue("description"), currentCategory);
                    break;
                case "subcategory":
                    if (currentCategory == null)
                        break;
                    currentSubCategory = new SubCategoryElem();
                    currentSubCategory.value = attributes.getValue("value");
                    currentSubCategory.description = attributes.getValue("description");
                    setUniquePkgAndEmail(currentSubCategory, (List) currentCategory.children);
                    currentSubCategory.uid = attributes.getValue("uid");
                    currentSubCategory.parent = currentCategory;
                    currentCategory.children.add(currentSubCategory);
                    break;

                case "specific_range":
                    printUnsupportedMessage("specific_range", attributes.getValue("description"), currentSubCategory);
                    break;

                case "specific":
                    if (currentSubCategory == null)
                        break;
                    currentSpecific = new SpecificElem();
                    currentSpecific.value = attributes.getValue("value");
                    currentSpecific.description = attributes.getValue("description");
                    setUniquePkgAndEmail(currentSpecific, (List) currentSubCategory.children);
                    currentSpecific.uid = attributes.getValue("uid");
                    currentSpecific.parent = currentSubCategory;
                    currentSubCategory.children.add(currentSpecific);
                    break;

                case "extra":
                    if (currentSpecific == null)
                        break;
                    currentExtra = new ExtraElem();
                    currentExtra.value = attributes.getValue("value");
                    currentExtra.description = attributes.getValue("description");
                    setUniquePkgAndEmail(currentExtra, (List) currentSpecific.children);
                    currentExtra.uid = attributes.getValue("uid");
                    currentExtra.parent = currentSpecific;
                    currentSpecific.children.add(currentExtra);
                    break;

                default:
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        {
            if (!qName.equals("cot") && inCot)
                return;

            try {
                switch (qName) {
                    case "cot":
                        inCot = false;
                        break;

                    case "cet":
                        if (inCetUid30)
                            inCetUid30 = false;
                        break;

                    case "entity":
                        if (currentEntity != null) {
                            if (!currentEntity.categories.isEmpty())
                                writeBaseClasses(currentEntity);
                            currentEntity = null;
                        }
                        break;

                    case "category":
                        if (currentCategory != null) {
                            if (currentCategory.children.isEmpty())
                                writeFile(currentCategory);
                            else
                                writeBaseClasses(currentCategory);
                            currentCategory = null;
                        }
                        break;

                    case "subcategory_xref":
                    case "subcategory_range":
                    case "specific_range":
                        break;

                    case "subcategory":
                        if (currentSubCategory != null) {
                            if (currentSubCategory.children.isEmpty())
                                writeFile(currentSubCategory);
                            else
                                writeBaseClasses(currentSubCategory);
                            currentSubCategory = null;
                        }
                        break;

                    case "specific":
                        if (currentSpecific != null) {
                            if (currentSpecific.children.isEmpty())
                                writeFile(currentSpecific);
                            else
                                writeBaseClasses(currentSpecific);
                            currentSpecific = null;
                        }
                        break;

                    case "extra":
                        if (currentExtra != null) {
                            writeFile(currentExtra);
                            currentExtra = null;
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

    private void writeBaseClasses(EntityElem ent) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(ent, sb);
        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        int countryInt = Integer.parseInt(ent.country);
        String countryNm = getName(countryFromInt, countryName, countryInt);

        int domainInt = Integer.parseInt(ent.domain);
        int kindInt = Integer.parseInt(ent.kind);

        String entKindNm = getName(kindFromInt, kindName, kindInt);

        String domainName;
        String domainVal;
        switch (entKindNm) {
            case "MUNITION":
                domainName = "MunitionDomain";
                domainVal = getName(munitionDomainFromInt, munitionDomainName, domainInt);
                break;
            case "SUPPLY":
                domainName = "SupplyDomain";
                domainVal = getName(supplyDomainFromInt, supplyDomainName, domainInt);
                break;
            case "OTHER":
            case "PLATFORM":
            case "LIFE_FORM":
            case "ENVIRONMENTAL":
            case "CULTURAL_FEATURE":
            case "RADIO":
            case "EXPENDABLE":
            case "SENSOR_EMITTER":
            default:
                domainName = "PlatformDomain";
                domainVal = getName(platformDomainFromInt, platformDomainName, domainInt);
                break;
        }

        String contents = String.format(entityTypeTemplate, pkg, countryNm, entKindNm, domainName, domainVal);
        saveFile(dir, "Base.java", contents);

        sb.setLength(0);
        
        if (ent.categories.isEmpty())
            System.out.println("Entity w/ no cats: " + ent.country + " " + ent.domain + " " + ent.kind + " " + ent.uid);
        
        ent.categories.forEach((cat) -> {
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

        contents = String.format(entityEnumTemplate, pkg, "Category", sb.toString());
        saveFile(dir, "Category.java", contents);
    }
    
    private void writeBaseClasses(CategoryElem cat) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(cat, sb);
        writeBaseClasses(cat,sb,"Category","SubCategory");
    }

    private void writeBaseClasses(SubCategoryElem sub) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(sub, sb);
        writeBaseClasses(sub,sb, "SubCategory", "Specific");
    }

    private void writeBaseClasses(SpecificElem spec) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(spec, sb);
        writeBaseClasses(spec,sb, "Specific", "Extra");
    }

    private void writeBaseClasses(DescriptionElem elem, StringBuilder sb, String className, String classNameBelow)
    {
        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String pkgBackOne = parentPackage(pkg);
        String subnm = elem.enumFromDescription; //fixName(cat.description).toUpperCase();

        String contents = String.format(entityBaseTemplate, pkg, pkgBackOne, className, pkgBackOne + ".Base", className, className, subnm);
        saveFile(dir, "Base.java", contents);

        if (elem.children.isEmpty())
            return;
        sb.setLength(0);

        elem.children.forEach((child) -> {
            String nm = child.enumFromDescription;
            if (!(nm.equals("DEPRECATED"))) {
                sb.append("    public static final byte ");
                sb.append(nm);
                sb.append(" = ");
                sb.append(possiblyCast2Byte(child.value));
                sb.append("; // ");
                sb.append(child.description); //.replace('"', '\''));
                sb.append("\n");
            }
        });
        sb.setLength(sb.length() - 2);

        contents = String.format(entityEnumTemplate, pkg, classNameBelow, sb.toString());
        saveFile(dir, classNameBelow+".java", contents);
    }

    //todo refactor for code reuse ala writeBaseClasses
    private void writeFile(CategoryElem cat) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(cat.parent, sb);

        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String classNm = fixName(cat.description);
        String specEnum = classNm.toUpperCase();

        String contents = String.format(entityClassTemplate, pkg, classNm, classNm, "Category", "Category", specEnum);
        saveFile(dir, classNm + ".java", contents);
    }

    private void writeFile(SubCategoryElem subc) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(subc.parent, sb);

        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String classNm = fixName(subc.description);

        //Hack...how to fix to avoid package name clashing with file name?
        if (classNm.equalsIgnoreCase("Other"))
            if (subc.parent.description.equalsIgnoreCase("Other"))
                classNm = classNm + "1";

        String specEnum = classNm.toUpperCase();

        String contents = String.format(entityClassTemplate, pkg, classNm, classNm, "SubCategory", "SubCategory", specEnum);
        saveFile(dir, classNm + ".java", contents);
    }

    private void writeFile(SpecificElem spec) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(spec.parent, sb);

        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        File f = new File(dir, fixName(spec.description) + ".java");
        f.createNewFile();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String classNm = fixName(spec.description);
        String specEnum = classNm.toUpperCase();

        String contents = String.format(entityClassTemplate, pkg, classNm, classNm, "Specific", "Specific", specEnum);
        saveFile(dir, classNm + ".java", contents);
    }

    private void writeFile(ExtraElem extra) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        buildPackagePath(extra.parent, sb);

        File dir = new File(outputDirectory, sb.toString());
        dir.mkdirs();

        String pkg = basePackageName + "." + pathToPackage(sb.toString());
        String classNm = fixName(extra.description);
        String specEnum = classNm.toUpperCase();

        String contents = String.format(entityClassTemplate, pkg, classNm, classNm, "Extra", "Extra", specEnum);
        saveFile(dir, classNm + ".java", contents);
    }

    private void saveFile(File parentDir, String name, String contents)
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
    }
    
    private String possiblyCast2Byte(String s)
    {
        if(s == null) 
            return "0";
        int i = Integer.parseInt(s);
        return (i > Byte.MAX_VALUE)? "(byte)"+s : s;
    }

    private void setUniquePkgAndEmail(DescriptionElem elem, List<DescriptionElem> lis)
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

    Pattern regex = Pattern.compile("\\((.*?)\\)");

    private String buildCountryPackagePart(String s)
    {
        Matcher m = regex.matcher(s);
        String fnd = null;
        while (m.find()) {
            fnd = m.group(1);
            if (fnd.length() == 3)
                break;
            fnd = null;
        }
        if (fnd != null)
            return fnd.toLowerCase();

        s = fixName(s);
        s = s.toLowerCase();
        if (s.length() > 3)
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
        kindname = buidKindOrDomainPackagePart(kindname);
        sb.append(buidKindOrDomainPackagePart(kindname));
        sb.append("/");

        String domainname;
        String kindnamelc = kindname.toLowerCase();

        switch(kindnamelc) {
            case "munition":
                domainname = getName(munitionDomainFromInt, munitionDomainName, Integer.parseInt(ent.domain));
                break;
            case "supply":
                domainname = getName(supplyDomainFromInt, supplyDomainName, Integer.parseInt(ent.domain));
                break;
            default:
                domainname = getName(platformDomainFromInt, platformDomainName, Integer.parseInt(ent.domain));
                break;
        }

        domainname = buidKindOrDomainPackagePart(domainname);
        sb.append(buidKindOrDomainPackagePart(domainname));
        sb.append("/");
    }

    private void buildPackagePath(CategoryElem elm, StringBuilder sb) throws Exception
    {
        try {
            buildPackagePath(elm.parent, sb);
        }
        catch (NullPointerException ex) {
            System.out.println("bp");
        }
        sb.append(elm.pkgFromDescription);
        sb.append("/");
    }

    private String buildPackagePath(SubCategoryElem sub, StringBuilder sb) throws Exception
    {
        buildPackagePath(sub.parent, sb);
        sb.append(sub.pkgFromDescription);
        sb.append("/");
        return sb.toString();
    }

    private String buildPackagePath(SpecificElem spec, StringBuilder sb) throws Exception
    {
        buildPackagePath(spec.parent, sb);
        sb.append(spec.pkgFromDescription);
        sb.append("/");
        return sb.toString();
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

    private void printUnsupportedMessage(String elname, String eldesc, CategoryElem cat)
    {
        StringBuilder bldr = new StringBuilder();
        bldr.append(cat.description);
        bldr.append("/");
        bldr.append(cat.parent.kind);
        bldr.append("/");
        bldr.append(cat.parent.domain);
        bldr.append("/");
        bldr.append(cat.parent.country);

        System.out.println("XML element " + elname + " {" + eldesc + "in " + bldr.toString() + " not supported");
    }

    private void printUnsupportedMessage(String elname, String eldesc, SubCategoryElem sub)
    {
        StringBuilder bldr = new StringBuilder();
        bldr.append(sub.description);
        bldr.append("/");
        bldr.append(sub.parent.description);
        bldr.append("/");
        bldr.append(sub.parent.parent.kind);
        bldr.append("/");
        bldr.append(sub.parent.parent.domain);
        bldr.append("/");
        bldr.append(sub.parent.parent.country);

        System.out.println("XML element " + elname + " {" + eldesc + "in " + bldr.toString() + " not supported");
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
        System.out.println("In: "+s+" out: "+r);
        return r;
    }

    public static void main(String[] args)
    {
        try {
            new Main(args[0], args[1], args[2]).run();
        }
        catch (SAXException | IOException | ParserConfigurationException ex) {
            System.err.println(ex.getClass().getSimpleName()+": "+ex.getLocalizedMessage());
        }
    }
}
