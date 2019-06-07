/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.opendis.reference.entitygenerator;

//import edu.nps.moves.dis.enumerations.LandPlatformAppearance;
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

/**
 * Main.java created on Apr 16, 2019 MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class JammerMain
{
    private final File outputDirectory;
    private final String basePackageName;
    private final String xmlPath;

    private String jammerEnumTemplate;
    private String jammerClassTemplate;
    private String jammerBaseTemplate;

    public JammerMain(String xmlPth, String outputDir, String packageName)
    {
        basePackageName = packageName;
        xmlPath = xmlPth;
        outputDirectory = new File(outputDir);

        loadFileTemplates();
    }

    private void run() throws SAXException, IOException, ParserConfigurationException
    {
/*        LandPlatformAppearance e = new LandPlatformAppearance();
LandPlatformAppearance.Bits en = LandPlatformAppearance.Bits.TRAILING_DUST_CLOUD;
Class c = en.cls;
Enum<?> nuts = (Enum<?>)c.getEnumConstants()[0];
     System.out.println("nuts");   
*/        
        MyHandler handler = new MyHandler();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        factory.newSAXParser().parse(new File(xmlPath), handler);
    }

    private void loadFileTemplates()
    {
        try {
            jammerEnumTemplate = loadOneTemplate("jammerenum.txt");
            jammerClassTemplate = loadOneTemplate("jammerclass.txt");
            jammerBaseTemplate = loadOneTemplate("jammerbase.txt");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String loadOneTemplate(String s) throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(getClass().getResource(s).toURI())));
    }

    class JammerTechniqueElem
    {
        String name;
        String uid;
        ArrayList<JammerKindElem> kinds = new ArrayList<>();
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
        JammerTechniqueElem currentTechnique;
        JammerKindElem currentKind;
        JammerCategoryElem currentCategory;
        JammerSubCategoryElem currentSubCategory;
        JammerSpecificElem currentSpecific;

        //HashSet<String> unhandledElems = new HashSet<>();
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            switch (qName) {
                case "jammer_technique":
                    currentTechnique = new JammerTechniqueElem();
                    currentTechnique.name = attributes.getValue("name");
                    currentTechnique.uid = attributes.getValue("uid");
                    break;

                case "jammer_kind":
                    currentKind = new JammerKindElem();
                    currentKind.value = attributes.getValue("value");
                    currentKind.description = attributes.getValue("description");
                    currentTechnique.kinds.add(currentKind);
                    break;

                case "jammer_category":
                    if (currentKind == null)
                        break;
                    currentCategory = new JammerCategoryElem();
                    currentCategory.value = attributes.getValue("value");
                    currentCategory.description = attributes.getValue("description");
                    currentCategory.parent = currentKind;
                    currentKind.categories.add(currentCategory);
                    break;

                case "jammer_subcategory":

                    if (currentCategory == null)
                        break;
                    currentSubCategory = new JammerSubCategoryElem();
                    currentSubCategory.value = attributes.getValue("value");
                    currentSubCategory.description = attributes.getValue("description");
                    currentSubCategory.parent = currentCategory;
                    currentCategory.subcategories.add(currentSubCategory);
                    break;

                case "jammer_specific":
                    if (currentSubCategory == null)
                        break;
                    currentSpecific = new JammerSpecificElem();
                    currentSpecific.value = attributes.getValue("value");
                    currentSpecific.description = attributes.getValue("description");

                    currentSpecific.parent = currentSubCategory;
                    currentSubCategory.specifics.add(currentSpecific);
                    break;

                default:
                //unhandledElems.add(qName);

            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        {
            try {
                switch (qName) {
                    case "jammer_technique":
                        writeJammerKindFile(currentTechnique);
                        break;

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
                            if (currentCategory.subcategories.isEmpty())
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
                            if (currentSubCategory.specifics.isEmpty())
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

        private void writeJammerKindFile(JammerTechniqueElem elem)
        {
            StringBuilder sb = new StringBuilder();
            for (JammerKindElem kind : elem.kinds) {
                sb.append("    ");
                sb.append(fixName(kind.description).toUpperCase());  //todo handle values
                sb.append(" (");
                sb.append(kind.value);
                sb.append(", \"");
                sb.append(kind.description.replace('"', '\''));
                sb.append("\"),\n");
            }
            sb.setLength(sb.length() - 2);

            String contents = String.format(jammerEnumTemplate, basePackageName, "JammerKind", "JammerKind", sb.toString(), "JammerKind", "JammerKind", "JammerKind", "JammerKind");
            saveFile(outputDirectory, "JammerKind.java", contents);
        }

        private void writeBaseClasses(JammerKindElem ent) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(ent, sb);
            File dir = new File(outputDirectory, sb.toString());
            dir.mkdirs();

            String pkg = basePackageName + "." + pathToPackage(sb.toString());
            String pkgBackOne = parentPackage(pkg);
            String kind = fixName(ent.description).toUpperCase();

            String contents = String.format(jammerBaseTemplate, pkg, pkgBackOne, "JammerKind", "edu.nps.moves.dis.JammingTechnique", "Kind", "JammerKind", kind);
            saveFile(dir, "Base.java", contents);

            sb.setLength(0);
            for (JammerCategoryElem cat : ent.categories) {
                sb.append("    ");
                sb.append(fixName(cat.description).toUpperCase()); //todo handle values
                sb.append(" (");
                sb.append(cat.value);
                sb.append(", \"");
                sb.append(cat.description.replace('"', '\''));
                sb.append("\"),\n");
            }
            sb.setLength(sb.length() - 2);
            contents = String.format(jammerEnumTemplate, pkg, "Category", "JammerCategory", sb.toString(), "Category", "Category", "Category", "Category");
            saveFile(dir, "Category.java", contents);
        }

        private void writeBaseClasses(JammerCategoryElem cat) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(cat, sb);
            File dir = new File(outputDirectory, sb.toString());
            dir.mkdirs();

            String pkg = basePackageName + "." + pathToPackage(sb.toString());
            String pkgBackOne = parentPackage(pkg);
            String category = fixName(cat.description).toUpperCase();

            String contents = String.format(jammerBaseTemplate, pkg, pkgBackOne, "Category", pkgBackOne + ".Base", "Category", "Category", category);
            saveFile(dir, "Base.java", contents);

            sb.setLength(0);
            for (JammerSubCategoryElem sub : cat.subcategories) {
                sb.append("    ");
                sb.append(fixName(sub.description).toUpperCase());
                sb.append(" (");
                sb.append(sub.value);
                sb.append(", \"");
                sb.append(sub.description.replace('"', '\''));
                sb.append("\"),\n");
            }
            sb.setLength(sb.length() - 2);

            contents = String.format(jammerEnumTemplate, pkg, "SubCategory", "JammerSubCategory", sb.toString(), "SubCategory", "SubCategory", "SubCategory", "SubCategory");
            saveFile(dir, "SubCategory.java", contents);
        }

        private void writeBaseClasses(JammerSubCategoryElem sub) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(sub, sb);
            File dir = new File(outputDirectory, sb.toString());
            dir.mkdirs();

            String pkg = basePackageName + "." + pathToPackage(sb.toString());
            String pkgBackOne = parentPackage(pkg);
            String subcat = fixName(sub.description).toUpperCase();

            String contents = String.format(jammerBaseTemplate, pkg, pkgBackOne, "SubCategory", pkgBackOne + ".Base", "Subcategory", "SubCategory", subcat);
            saveFile(dir, "Base.java", contents);

            sb.setLength(0);
            for (JammerSpecificElem spec : sub.specifics) {
                sb.append("    ");
                sb.append(fixName(spec.description).toUpperCase());
                sb.append(" (");
                sb.append(spec.value);
                sb.append(", \"");
                sb.append(spec.description.replace('"', '\''));
                sb.append("\"),\n");
            }
            sb.setLength(sb.length() - 2);

            contents = String.format(jammerEnumTemplate, pkg, "Specific", "JammerSpecific", sb.toString(), "Specific", "Specific", "Specific", "Specific");
            saveFile(dir, "Specific.java", contents);
        }

        private void writeFile(JammerSpecificElem spec) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(spec.parent, sb);

            File dir = new File(outputDirectory, sb.toString());
            dir.mkdirs();

            String pkg = basePackageName + "." + pathToPackage(sb.toString());
            String classNm = fixName(spec.description);
            String specEnum = classNm.toUpperCase();

            String contents = String.format(jammerClassTemplate, pkg, classNm, classNm, "Specific", "Specific", specEnum);
            saveFile(dir, classNm + ".java", contents);
        }

        private void writeFile(JammerSubCategoryElem spec) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(spec.parent, sb);

            File dir = new File(outputDirectory, sb.toString());
            dir.mkdirs();

            File f = new File(dir, fixName(spec.description) + ".java");
            f.createNewFile();

            String pkg = basePackageName + "." + pathToPackage(sb.toString());
            String classNm = fixName(spec.description);
            String subcatEnum = classNm.toUpperCase();

            String contents = String.format(jammerClassTemplate, pkg, classNm, classNm, "Subcategory", "SubCategory", subcatEnum);  // cap diff not error
            saveFile(dir, classNm + ".java", contents);
        }

        private void writeFile(JammerCategoryElem spec) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            buildPackagePath(spec.parent, sb);

            File dir = new File(outputDirectory, sb.toString());
            dir.mkdirs();

            File f = new File(dir, fixName(spec.description) + ".java");
            f.createNewFile();

            String pkg = basePackageName + "." + pathToPackage(sb.toString());
            String classNm = fixName(spec.description);
            String catEnum = classNm.toUpperCase();

            String contents = String.format(jammerClassTemplate, pkg, classNm, classNm, "Category", "Category", catEnum);
            saveFile(dir, classNm + ".java", contents);
        }

        private void saveFile(File parentDir, String name, String contents)
        {
            // save file
            File target = new File(parentDir, name);
            FileWriter fw = null;
            try {
                target.createNewFile();
                fw = new FileWriter(target);
                fw.write(contents);
                fw.flush();
                fw.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
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
            buildPackagePath(sub.parent, sb);
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

        private String fixName(String s)
        {
            String r = s.trim();
            // Convert any of these chars to underbar (u2013 is a hyphen observed in source XML):
            r = r.replaceAll(" ", "");

            r = r.replaceAll("[\\h-/,\";:\\u2013]", "_");

            // Remove any of these chars (u2019 is an apostrophe observed in source XML):
            r = r.replaceAll("[()}{}'.#&\\u2019]", "");

            // Special case the plus character:
            r = r.replace("+", "PLUS");

            // Collapse all contiguous underbars:
            r = r.replaceAll("_{2,}", "_");

            // If there's nothing there, put in something:
            if (r.isEmpty() || r.equals("_"))
                r = "undef";

            // Java identifier can't start with digit
            if (Character.isDigit(r.charAt(0)))
                r = "_" + r;
            return r;
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
