package com.bixuebihui.jsongen;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.codemodel.*;
import org.apache.commons.io.FilenameUtils;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.exception.GenerationException;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.NameHelper;
import org.jsonschema2pojo.util.URLUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * @author xwx
 */
public class EsJsonschema2Pojo {

    /**
         * Reads the contents of the given source and initiates schema generation.
         *
         * @param config
         *            the configuration options (including source and target paths,
         *            and other behavioural options) that will control code
         *            generation
         * @throws FileNotFoundException
         *             if the source path is not found
         * @throws IOException
         *             if the application is unable to read data from the source
         */
        public static void generate(GenerationConfig config, RuleLogger logger,
                                    Consumer<Pair<String, String[]>> consumer,
                                    Set<String> ignoreFields, Set<String> collectionFields ) throws IOException {
            Annotator annotator = getAnnotator(config);
            RuleFactory ruleFactory = createRuleFactory(config);

            ruleFactory.setAnnotator(annotator);
            ruleFactory.setGenerationConfig(config);
            ruleFactory.setLogger(logger);
            ruleFactory.setSchemaStore(new SchemaStore(createContentResolver(config)));

            EsSchemaMapper mapper = new EsSchemaMapper(ruleFactory, createSchemaGenerator(config));

            JCodeModel codeModel = new JCodeModel();

            if (config.isRemoveOldOutput()) {
                removeOldOutput(config.getTargetDirectory());
            }

            boolean mkdir = config.getTargetDirectory().exists() || config.getTargetDirectory().mkdirs();

            for (Iterator<URL> sources = config.getSource(); sources.hasNext();) {
                URL source = sources.next();

                if (URLUtil.parseProtocol(source.toString()) == URLProtocol.FILE && URLUtil.getFileFromURL(source).isDirectory()) {
                    generateRecursive(config, mapper, codeModel, defaultString(config.getTargetPackage()), Arrays.asList(URLUtil.getFileFromURL(source).listFiles(config.getFileFilter())), ignoreFields, collectionFields);
                } else {


                   JType res = mapper.generate(codeModel, getNodeName(source, config),  defaultString(config.getTargetPackage()), source, ignoreFields, collectionFields);


                    String[] fields = findFields((JDefinedClass) res);

                    if(consumer!=null) {
                        consumer.accept(ImmutablePair.of(res.name(), fields));
                    }

                }
            }

            if (mkdir) {
                CodeWriter sourcesWriter = new FileCodeWriterWithEncoding(config.getTargetDirectory(), config.getOutputEncoding());
                CodeWriter resourcesWriter = new FileCodeWriterWithEncoding(config.getTargetDirectory(), config.getOutputEncoding());
                codeModel.build(sourcesWriter, resourcesWriter);
            } else {
                throw new GenerationException("Could not create or access target directory " + config.getTargetDirectory().getAbsolutePath());
            }
        }

    private static String[] findFields(JDefinedClass res) {
        StringBuilderWriter sb = new StringBuilderWriter();
        JFormatter f = new JFormatter(sb);
        for(JAnnotationUse annotation: res.annotations()){
            if("JsonPropertyOrder".equals(annotation.getAnnotationClass().name())){
                List<JAnnotationUse> list = (List<JAnnotationUse>) ((JAnnotationArrayMember) (annotation.getAnnotationMembers().get("value"))).annotations();
                for (JAnnotationValue a: list
                     ) {
                    a.generate(f);
                }
            }
        }

        StringBuilder s = sb.getBuilder();
        return s.substring(1, s.length() - 1).split("\"\"");
    }

    private static ContentResolver createContentResolver(GenerationConfig config) {
            if (config.getSourceType() == SourceType.YAMLSCHEMA || config.getSourceType() == SourceType.YAML) {
                return new ContentResolver(new YAMLFactory());
            } else {
                return new ContentResolver();
            }
        }

        private static SchemaGenerator createSchemaGenerator(GenerationConfig config) {
            if (config.getSourceType() == SourceType.YAMLSCHEMA || config.getSourceType() == SourceType.YAML) {
                return new SchemaGenerator(new YAMLFactory());
            } else {
                return new SchemaGenerator();
            }
        }

        private static RuleFactory createRuleFactory(GenerationConfig config) {
            Class<? extends RuleFactory> clazz = config.getCustomRuleFactory();

            if (!RuleFactory.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("The class name given as a rule factory  (" + clazz.getName() + ") does not refer to a class that implements " + RuleFactory.class.getName());
            }

            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Failed to create a rule factory from the given class. An exception was thrown on trying to create a new instance.", e.getCause());
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Failed to create a rule factory from the given class. It appears that we do not have access to this class - is both the class and its no-arg constructor marked public?", e);
            }
        }

        private static void generateRecursive(GenerationConfig config, EsSchemaMapper mapper, JCodeModel codeModel, String packageName, List<File> schemaFiles, Set<String> ignoreFields, Set<String> collectionFields) throws IOException {

            schemaFiles.sort(config.getSourceSortOrder().getComparator());

            for (File child : schemaFiles) {
                if (child.isFile()) {

                    String className =  getNodeName(child.toURI().toURL(), config);
                    packageName  = defaultString(packageName);

                    mapper.generate(codeModel,className, packageName, child.toURI().toURL(), ignoreFields, collectionFields);


                } else {
                    generateRecursive(config, mapper, codeModel, childQualifiedName(packageName, child.getName()), Arrays.asList(child.listFiles(config.getFileFilter())), ignoreFields, collectionFields);
                }
            }
        }

        private static String childQualifiedName(String parentQualifiedName, String childSimpleName) {
            String safeChildName = childSimpleName.replaceAll(NameHelper.ILLEGAL_CHARACTER_REGEX, "_");
            return isEmpty(parentQualifiedName) ? safeChildName : parentQualifiedName + "." + safeChildName;
        }

        private static void removeOldOutput(File targetDirectory) {
            if (targetDirectory.exists()) {
                for (File f : targetDirectory.listFiles()) {
                    delete(f);
                }
            }
        }

        @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
        private static void delete(File f) {
            if (f.isDirectory()) {
                for (File child : f.listFiles()) {
                    delete(child);
                }
            }
            f.delete();
        }

        private static Annotator getAnnotator(GenerationConfig config) {
            AnnotatorFactory factory = new AnnotatorFactory(config);
            return factory.getAnnotator(factory.getAnnotator(config.getAnnotationStyle()), factory.getAnnotator(config.getCustomAnnotator()));
        }

        public static String getNodeName(URL file, GenerationConfig config) {

            // extract index name
            String url = file.toString();
            if(url.startsWith("http") && url.contains("/_mapping")){
                url = url.substring(0,  url.indexOf("/_mapping"));
            }else if (url.startsWith("http") && url.contains("/_search")){
                url = url.substring(0, url.indexOf("/_search"));
            }

            return getNodeName(url, config);
        }

        public static String getNodeName(String filePath, GenerationConfig config) {
            try {
                String fileName = FilenameUtils.getName(URLDecoder.decode(filePath, StandardCharsets.UTF_8.toString()));
                String[] extensions = config.getFileExtensions() == null ? new String[] {} : config.getFileExtensions();

                boolean extensionRemoved = false;
                for (String s : extensions) {
                    String extension = s;
                    if (extension.length() == 0) {
                        continue;
                    }
                    if (!extension.startsWith(".")) {
                        extension = "." + extension;
                    }
                    if (fileName.endsWith(extension)) {
                        fileName = removeEnd(fileName, extension);
                        extensionRemoved = true;
                        break;
                    }
                }
                if (!extensionRemoved) {
                    fileName = FilenameUtils.getBaseName(fileName);
                }
                return fileName;
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(String.format("Unable to generate node name from URL: %s", filePath), e);
            }
        }



}
