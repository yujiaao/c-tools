package com.bixuebihui.tablegen.generator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to merge generated Java source code with existing user-modified
 * code.
 * This allows regeneration of DTOs while preserving user customizations.
 * 
 * @author xwx
 */
public class JavaSourceMerger {
    private static final Logger LOG = LoggerFactory.getLogger(JavaSourceMerger.class);

    /**
     * Merges generated source code with existing source code.
     * 
     * Strategy (以原文件为基础):
     * 1. 以原文件为基础，保留原文件的结构、格式和注释
     * 2. 更新原文件中已存在的字段（类型、注解）
     * 3. 添加生成代码中的新字段到原文件
     * 4. 保留用户添加的字段和方法
     * 5. 更新原文件中已存在的方法（注解、签名）
     * 6. 添加生成代码中的新方法到原文件
     * 7. 保留用户添加的方法实现和注释
     * 8. 合并导入语句
     * 9. 合并类级别注解
     * 
     * @param existingFile    The existing source file
     * @param generatedSource The newly generated source code
     * @return Merged source code as string (based on existing file structure)
     * @throws IOException if parsing fails
     */
    public static String mergeSource(File existingFile, String generatedSource) throws IOException {
        if (!existingFile.exists()) {
            return generatedSource;
        }

        try {
            String existingSource = Files.readString(existingFile.toPath());
            
            // Check if file is empty or only contains whitespace
            if (existingSource.trim().isEmpty()) {
                LOG.debug("Existing file {} is empty, using generated source", existingFile.getName());
                return generatedSource;
            }

            CompilationUnit existingCu = StaticJavaParser.parse(existingSource);
            CompilationUnit generatedCu = StaticJavaParser.parse(generatedSource);

            // Get the main class/interface from both compilation units
            String existingClassName = getClassName(existingCu);
            if (existingClassName.isEmpty()) {
                LOG.warn("No class or interface found in existing file {}, using generated source", existingFile.getName());
                return generatedSource;
            }
            
            ClassOrInterfaceDeclaration existingClass = existingCu.getClassByName(existingClassName)
                    .or(() -> existingCu.getInterfaceByName(existingClassName))
                    .orElse(null);
            if (existingClass == null) {
                LOG.warn("Could not find class or interface '{}' in existing file {}, using generated source", 
                        existingClassName, existingFile.getName());
                return generatedSource;
            }
            
            String generatedClassName = getClassName(generatedCu);
            if (generatedClassName.isEmpty()) {
                LOG.warn("No class or interface found in generated source, using generated source as-is");
                return generatedSource;
            }
            
            ClassOrInterfaceDeclaration generatedClass = generatedCu.getClassByName(generatedClassName)
                    .or(() -> generatedCu.getInterfaceByName(generatedClassName))
                    .orElse(null);
            if (generatedClass == null) {
                LOG.warn("Could not find class or interface '{}' in generated source, using generated source as-is", 
                        generatedClassName);
                return generatedSource;
            }

            // Merge fields (updates existing fields, adds new ones, keeps user-added fields)
            mergeFields(existingClass, generatedClass);

            // Merge methods (updates existing methods, adds new ones, keeps user-added methods)
            mergeMethods(existingClass, generatedClass);

            // Merge imports (adds missing imports from generated code)
            mergeImports(existingCu, generatedCu);
            
            // Optimize imports (remove unused imports)
            optimizeImports(existingCu);

            // Merge class-level annotations (adds missing annotations from generated code, keeps existing ones)
            mergeClassAnnotations(existingClass, generatedClass);

            // Return the existing compilation unit as base to preserve user's formatting and structure
            return existingCu.toString();

        } catch (Exception e) {
            LOG.warn("Failed to merge sources for {}, falling back to generated source. Error: {}",
                    existingFile.getName(), e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName(), e);
            return generatedSource;
        }
    }

    private static String getClassName(CompilationUnit cu) {
        return cu.getTypes().stream()
                .filter(type -> type instanceof ClassOrInterfaceDeclaration)
                .map(NodeWithSimpleName::getNameAsString)
                .findFirst()
                .orElse("");
    }

    /**
     * Merge fields from generated class into existing class.
     * - Update existing generated fields (by name match)
     * - Add new generated fields
     * - Keep user-added fields
     * - If existing field is collection type and generated is simple type, keep existing
     */
    private static void mergeFields(ClassOrInterfaceDeclaration existingClass,
            ClassOrInterfaceDeclaration generatedClass) {
        // Build a map of generated fields by name for easy lookup
        Map<String, FieldDeclaration> generatedFieldMap = new HashMap<>();
        for (FieldDeclaration field : generatedClass.getFields()) {
            for (VariableDeclarator var : field.getVariables()) {
                generatedFieldMap.put(var.getNameAsString(), field);
            }
        }

        // Track which fields in existing class are user-added
        List<FieldDeclaration> fieldsToKeep = new ArrayList<>();

        for (FieldDeclaration existingField : existingClass.getFields()) {
            for (VariableDeclarator var : existingField.getVariables()) {
                String fieldName = var.getNameAsString();

                if (generatedFieldMap.containsKey(fieldName)) {
                    // This field exists in generated code - update it while preserving user's comments
                    FieldDeclaration generatedField = generatedFieldMap.get(fieldName);
                    
                    // Get types for comparison
                    String existingType = existingField.getCommonType().asString();
                    String generatedType = generatedField.getCommonType().asString();
                    
                    // If existing field is collection type and generated is simple type, keep existing
                    if (isCollectionType(existingType) && !isCollectionType(generatedType)) {
                        LOG.debug("Keeping existing collection type field '{}': {} (generated was: {})", 
                                fieldName, existingType, generatedType);
                        // Keep existing field with all its comments and formatting
                        fieldsToKeep.add(existingField.clone());
                    } else {
                        // Update field: use generated type and annotations, but preserve existing comments
                        FieldDeclaration updatedField = generatedField.clone();
                        // Remove all comments from generated field first (to avoid formatting issues)
                        updatedField.getComment().ifPresent(c -> updatedField.removeComment());
                        updatedField.getVariables().forEach(v -> {
                            v.getComment().ifPresent(c -> v.removeComment());
                        });
                        // Preserve existing field's comments if they exist
                        existingField.getComment().ifPresent(c -> updatedField.setComment(c.clone()));
                        // Preserve variable declarator comments
                        for (int i = 0; i < updatedField.getVariables().size() && i < existingField.getVariables().size(); i++) {
                            VariableDeclarator updatedVar = updatedField.getVariable(i);
                            VariableDeclarator existingVar = existingField.getVariable(i);
                            existingVar.getComment().ifPresent(c -> updatedVar.setComment(c.clone()));
                        }
                        fieldsToKeep.add(updatedField);
                    }
                    generatedFieldMap.remove(fieldName); // Mark as processed
                } else {
                    // This is a user-added field - keep it as-is with all comments and formatting
                    fieldsToKeep.add(existingField.clone());
                }
            }
        }

        // Add any remaining generated fields that weren't in the existing class
        for (FieldDeclaration newField : generatedFieldMap.values()) {
            FieldDeclaration clonedField = newField.clone();
            // Remove all comments that may cause formatting issues
            clonedField.getComment().ifPresent(c -> clonedField.removeComment());
            // Also remove comments from variable declarators
            clonedField.getVariables().forEach(v -> {
                v.getComment().ifPresent(c -> v.removeComment());
            });
            fieldsToKeep.add(clonedField);
        }

        // Replace existing class fields with merged fields
        // Cannot use clear() on unmodifiable collection, so remove each field individually
        List<FieldDeclaration> fieldsToRemove = new ArrayList<>(existingClass.getFields());
        fieldsToRemove.forEach(existingClass::remove);
        fieldsToKeep.forEach(existingClass::addMember);
    }

    /**
     * Merge methods - keep user-added methods, update generated ones if needed.
     * If existing method return type is collection and generated is simple type, keep existing.
     * If existing method has annotations and generated doesn't, keep existing.
     */
    private static void mergeMethods(ClassOrInterfaceDeclaration existingClass,
            ClassOrInterfaceDeclaration generatedClass) {
        // IMPORTANT: Create a copy of existing methods list before processing
        // This ensures we can iterate over all original methods even if the class is modified
        List<MethodDeclaration> allExistingMethods = new ArrayList<>(existingClass.getMethods());
        
        // Build a map of existing methods by signature
        Map<String, MethodDeclaration> existingMethodMap = new HashMap<>();
        for (MethodDeclaration method : allExistingMethods) {
            String signature = getMethodSignature(method);
            existingMethodMap.put(signature, method);
        }

        // Process generated methods
        List<MethodDeclaration> methodsToKeep = new ArrayList<>();
        Set<String> processedSignatures = new HashSet<>();
        // Track method names that have been replaced by existing methods with collection types
        Set<String> replacedMethodNames = new HashSet<>();

        for (MethodDeclaration generatedMethod : generatedClass.getMethods()) {
            String signature = getMethodSignature(generatedMethod);
            String methodName = generatedMethod.getNameAsString();
            
            MethodDeclaration existingMethod = existingMethodMap.get(signature);
            if (existingMethod != null) {
                // Method exists in both - decide which one to keep
                String existingReturnType = existingMethod.getType().asString();
                String generatedReturnType = generatedMethod.getType().asString();
                
                boolean keepExisting = false;
                
                // If existing method return type is collection and generated is simple type, keep existing
                if (isCollectionType(existingReturnType) && !isCollectionType(generatedReturnType)) {
                    LOG.debug("Keeping existing method with collection return type '{}': {} (generated was: {})", 
                            signature, existingReturnType, generatedReturnType);
                    keepExisting = true;
                }
                // If existing method has annotations and generated doesn't, keep existing
                else if (!existingMethod.getAnnotations().isEmpty() && generatedMethod.getAnnotations().isEmpty()) {
                    LOG.debug("Keeping existing method with annotations '{}' (generated has no annotations)", 
                            signature);
                    keepExisting = true;
                }
                // If existing method has more annotations, keep existing
                else if (existingMethod.getAnnotations().size() > generatedMethod.getAnnotations().size()) {
                    LOG.debug("Keeping existing method with more annotations '{}' (existing: {}, generated: {})", 
                            signature, existingMethod.getAnnotations().size(), generatedMethod.getAnnotations().size());
                    keepExisting = true;
                }
                // For interfaces, if existing method has any annotations, always keep existing (preserve user customizations)
                else if (existingClass.isInterface() && !existingMethod.getAnnotations().isEmpty()) {
                    LOG.debug("Keeping existing interface method with annotations '{}' (interface method preservation)", 
                            signature);
                    keepExisting = true;
                }
                
                // Mark signature as processed
                processedSignatures.add(signature);
                
                if (keepExisting) {
                    // Keep existing method with all its comments, annotations, and implementation
                    methodsToKeep.add(existingMethod.clone());
                    if (isCollectionType(existingReturnType) && !isCollectionType(generatedReturnType)) {
                        // Mark that this method name was replaced by existing collection type version
                        replacedMethodNames.add(methodName);
                    }
                } else {
                    // Update method: use generated annotations and signature, but preserve existing comments and body
                    MethodDeclaration updatedMethod = generatedMethod.clone();
                    // Preserve existing method's comments if they exist
                    existingMethod.getComment().ifPresent(c -> updatedMethod.setComment(c.clone()));
                    // For classes with method bodies, preserve the existing implementation
                    if (existingMethod.getBody().isPresent() && !existingMethod.getBody().get().getStatements().isEmpty()) {
                        // Keep existing method body (user's implementation)
                        updatedMethod.setBody(existingMethod.getBody().get().clone());
                    }
                    methodsToKeep.add(updatedMethod);
                }
            } else {
                // Mark signature as processed for new generated methods
                processedSignatures.add(signature);
                // Check if there's an existing method with the same name but different signature
                // and if it has collection type, we should skip this generated method
                boolean skipGenerated = false;
                MethodDeclaration existingMethodWithCollectionType = null;
                for (MethodDeclaration existingMethodWithSameName : allExistingMethods) {
                    if (existingMethodWithSameName.getNameAsString().equals(methodName)) {
                        String existingReturnType = existingMethodWithSameName.getType().asString();
                        String generatedReturnType = generatedMethod.getType().asString();
                        // If existing method has collection type and generated has simple type, skip generated
                        if (isCollectionType(existingReturnType) && !isCollectionType(generatedReturnType)) {
                            LOG.debug("Skipping generated method '{}' - existing has collection type: {} (generated: {})", 
                                    signature, existingReturnType, generatedReturnType);
                            skipGenerated = true;
                            replacedMethodNames.add(methodName);
                            existingMethodWithCollectionType = existingMethodWithSameName;
                            break;
                        }
                    }
                }
                if (skipGenerated) {
                    // Add the existing method with collection type instead
                    String existingSignature = getMethodSignature(existingMethodWithCollectionType);
                    if (!processedSignatures.contains(existingSignature)) {
                        methodsToKeep.add(existingMethodWithCollectionType.clone());
                        processedSignatures.add(existingSignature);
                    }
                } else {
                    // New method from generated class
                    methodsToKeep.add(generatedMethod.clone());
                }
            }
        }

        // Check if this is an interface (for interfaces, we preserve user-added methods even if name matches)
        boolean isInterface = existingClass.isInterface();
        
        // Build a set of method names from generated class (for checking name conflicts)
        Set<String> generatedMethodNames = new HashSet<>();
        for (MethodDeclaration generatedMethod : generatedClass.getMethods()) {
            generatedMethodNames.add(generatedMethod.getNameAsString());
        }
        
        // Add existing methods that weren't in generated class (user-added methods)
        // This is critical: preserve all methods that exist in original but not in generated
        // For interfaces: preserve user-added methods even if method name exists in generated code with different signature
        // For classes: if a method with the same name exists in generated code but with different signature,
        //   we should remove the old one (generated code takes precedence for method updates)
        LOG.debug("Processing {} existing methods, {} generated methods, isInterface: {}", 
                allExistingMethods.size(), generatedClass.getMethods().size(), isInterface);
        
        for (MethodDeclaration existingMethod : allExistingMethods) {
            String signature = getMethodSignature(existingMethod);
            String methodName = existingMethod.getNameAsString();
            
            if (!processedSignatures.contains(signature)) {
                // Check if this method name was replaced by a collection type version
                if (replacedMethodNames.contains(methodName)) {
                    // This method was already handled (replaced by collection type version)
                    LOG.debug("Skipping existing method '{}' - already replaced by collection type version", signature);
                    continue;
                }
                
                // For interfaces, always preserve user-added methods (even if method name exists in generated code)
                // For classes, check if a method with the same name exists in generated code
                // If yes and it's a class, it means the method signature was updated (e.g., setId(Integer) -> setId(Long))
                // In this case, we should not keep the old version for classes
                if (!isInterface && generatedMethodNames.contains(methodName)) {
                    LOG.debug("Skipping existing method '{}' - method name exists in generated code with different signature (class method update)", signature);
                } else {
                    LOG.info("Keeping user-added method '{}' (not in generated code or interface method)", signature);
                    methodsToKeep.add(existingMethod.clone());
                }
            } else {
                LOG.debug("Method '{}' exists in both, already processed", signature);
            }
        }
        
        LOG.debug("Final merged methods count: {}", methodsToKeep.size());

        // Replace existing class methods with merged methods
        // Cannot use clear() on unmodifiable collection, so remove each method individually
        List<MethodDeclaration> methodsToRemove = new ArrayList<>(existingClass.getMethods());
        methodsToRemove.forEach(existingClass::remove);
        methodsToKeep.forEach(existingClass::addMember);
    }

    /**
     * Get method signature for comparison.
     * Signature format: methodName(paramType1,paramType2,...)
     * This matches Java's method signature definition (name + parameter types, not return type).
     */
    private static String getMethodSignature(MethodDeclaration method) {
        return method.getNameAsString() + "(" +
                method.getParameters().stream()
                        .map(p -> p.getType().asString())
                        .collect(Collectors.joining(","))
                +
                ")";
    }

    /**
     * Merge imports from both compilation units.
     * Adds missing imports from generated code to existing compilation unit.
     */
    private static void mergeImports(CompilationUnit existingCu, CompilationUnit generatedCu) {
        Set<String> existingImportNames = new HashSet<>();
        
        // Collect existing import names
        for (ImportDeclaration imp : existingCu.getImports()) {
            existingImportNames.add(imp.getNameAsString());
        }

        // Add missing imports from generated code
        for (ImportDeclaration imp : generatedCu.getImports()) {
            String importName = imp.getNameAsString();
            if (!existingImportNames.contains(importName)) {
                existingCu.addImport(imp.getNameAsString(), imp.isStatic(), imp.isAsterisk());
            }
        }
    }

    /**
     * Optimize imports by removing unused imports.
     * This method collects all type names used in the code and removes imports that are not referenced.
     * 
     * Note: This is a simplified implementation. It may not catch all cases, especially:
     * - Static imports
     * - Wildcard imports (import java.util.*)
     * - Types used in annotations
     * - Types used in generic parameters
     */
    private static void optimizeImports(CompilationUnit cu) {
        // Collect all used type names from the compilation unit
        Set<String> usedTypeNames = new HashSet<>();
        
        // Visitor to collect all type names used in the code
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(com.github.javaparser.ast.type.ClassOrInterfaceType n, Void arg) {
                super.visit(n, arg);
                // Collect simple name (e.g., "List" from "List<String>")
                usedTypeNames.add(n.getNameAsString());
                // Also collect fully qualified name if available
                if (n.getScope().isPresent()) {
                    usedTypeNames.add(n.getScope().get().asString() + "." + n.getNameAsString());
                }
            }
            
            @Override
            public void visit(com.github.javaparser.ast.expr.NameExpr n, Void arg) {
                super.visit(n, arg);
                // Collect name expressions (may be used in annotations, etc.)
                usedTypeNames.add(n.getNameAsString());
            }
        }, null);
        
        // Also collect types from fields, methods, etc.
        cu.findAll(FieldDeclaration.class).forEach(field -> {
            Type type = field.getCommonType();
            collectTypeNames(type, usedTypeNames);
        });
        
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            // Collect return type
            collectTypeNames(method.getType(), usedTypeNames);
            // Collect parameter types
            method.getParameters().forEach(param -> {
                collectTypeNames(param.getType(), usedTypeNames);
            });
        });
        
        // Get the package name (if any)
        String packageName = cu.getPackageDeclaration()
                .map(p -> p.getNameAsString())
                .orElse("");
        
        // Remove unused imports
        List<ImportDeclaration> importsToRemove = new ArrayList<>();
        for (ImportDeclaration imp : cu.getImports()) {
            if (imp.isAsterisk()) {
                // For wildcard imports, we keep them (too complex to verify)
                continue;
            }
            
            String importName = imp.getNameAsString();
            String simpleName = getSimpleName(importName);
            
            // Check if this import is used
            boolean isUsed = false;
            
            // Check exact match
            if (usedTypeNames.contains(importName)) {
                isUsed = true;
            }
            
            // Check simple name match (but avoid false positives from java.lang)
            if (!isUsed && usedTypeNames.contains(simpleName)) {
                // Only consider it used if it's not from java.lang
                // (java.lang types don't need imports)
                if (!importName.startsWith("java.lang.") || 
                    !simpleName.equals(importName.substring("java.lang.".length()))) {
                    isUsed = true;
                }
            }
            
            // Static imports are kept (harder to verify)
            if (imp.isStatic()) {
                isUsed = true;
            }
            
            if (!isUsed) {
                importsToRemove.add(imp);
            }
        }
        
        // Remove unused imports
        for (ImportDeclaration imp : importsToRemove) {
            cu.remove(imp);
            LOG.debug("Removed unused import: {}", imp.getNameAsString());
        }
    }
    
    /**
     * Collect type names from a Type node recursively.
     */
    private static void collectTypeNames(Type type, Set<String> typeNames) {
        if (type.isClassOrInterfaceType()) {
            com.github.javaparser.ast.type.ClassOrInterfaceType coit = type.asClassOrInterfaceType();
            typeNames.add(coit.getNameAsString());
            if (coit.getScope().isPresent()) {
                typeNames.add(coit.getScope().get().asString() + "." + coit.getNameAsString());
            }
            // Collect type arguments
            coit.getTypeArguments().ifPresent(args -> {
                args.forEach(arg -> collectTypeNames(arg, typeNames));
            });
        } else if (type.isPrimitiveType()) {
            // Primitive types don't need imports
        } else if (type.isArrayType()) {
            collectTypeNames(type.asArrayType().getComponentType(), typeNames);
        } else if (type.isWildcardType()) {
            type.asWildcardType().getExtendedType().ifPresent(t -> collectTypeNames(t, typeNames));
            type.asWildcardType().getSuperType().ifPresent(t -> collectTypeNames(t, typeNames));
        }
    }
    
    /**
     * Get the simple name from a fully qualified name.
     * Example: "java.util.List" -> "List"
     */
    private static String getSimpleName(String fullyQualifiedName) {
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        return lastDot >= 0 ? fullyQualifiedName.substring(lastDot + 1) : fullyQualifiedName;
    }

    /**
     * Merge class-level annotations.
     * Adds missing annotations from generated code to existing class, keeps existing annotations.
     */
    private static void mergeClassAnnotations(ClassOrInterfaceDeclaration existingClass,
            ClassOrInterfaceDeclaration generatedClass) {
        Set<String> existingAnnotationNames = new HashSet<>();
        
        // Collect existing annotation names (by simple name)
        for (com.github.javaparser.ast.expr.AnnotationExpr ann : existingClass.getAnnotations()) {
            existingAnnotationNames.add(ann.getNameAsString());
        }

        // Add missing annotations from generated code
        for (com.github.javaparser.ast.expr.AnnotationExpr ann : generatedClass.getAnnotations()) {
            String annotationName = ann.getNameAsString();
            if (!existingAnnotationNames.contains(annotationName)) {
                existingClass.addAnnotation(ann.clone());
            }
        }
    }

    /**
     * Check if a type string represents a collection type.
     * Collection types include: List, Set, Map, Collection and their implementations.
     * 
     * @param typeStr The type string to check (e.g., "List<String>", "Set<Integer>", "Map<String, Object>")
     * @return true if the type is a collection type, false otherwise
     */
    private static boolean isCollectionType(String typeStr) {
        if (typeStr == null || typeStr.isEmpty()) {
            return false;
        }
        
        // Remove generic parameters for checking (e.g., "List<String>" -> "List")
        String baseType = typeStr.split("<")[0].trim();
        
        // Check for common collection types
        return baseType.equals("List") || 
               baseType.equals("Set") || 
               baseType.equals("Map") || 
               baseType.equals("Collection") ||
               baseType.equals("ArrayList") ||
               baseType.equals("LinkedList") ||
               baseType.equals("Vector") ||
               baseType.equals("HashSet") ||
               baseType.equals("TreeSet") ||
               baseType.equals("LinkedHashSet") ||
               baseType.equals("HashMap") ||
               baseType.equals("TreeMap") ||
               baseType.equals("LinkedHashMap") ||
               baseType.equals("Hashtable") ||
               baseType.startsWith("java.util.List") ||
               baseType.startsWith("java.util.Set") ||
               baseType.startsWith("java.util.Map") ||
               baseType.startsWith("java.util.Collection");
    }
}
