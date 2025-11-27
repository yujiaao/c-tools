package com.bixuebihui;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Mojo(name = "addSourceMethod", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class AddSourceMethodMojo extends AbstractMojo {

    // 配置参数：目标类全限定名（如com.example.MyClass）
    @Parameter(property = "targetClass", required = true)
    private String targetClass;

    // 配置参数：要添加的方法名
    @Parameter(property = "methodName", required = true)
    private String methodName;

    // 配置参数：方法参数（如"String name, int age"，可选）
    @Parameter(property = "methodParams", defaultValue = "")
    private String methodParams;

    // 配置参数：方法体内容（如"System.out.println(name);"）
    @Parameter(property = "methodBody", defaultValue = "// Generated method")
    private String methodBody;

    // 项目源码目录（默认src/main/java）
    @Parameter(defaultValue = "${project.basedir}/src/main/java", required = true)
    private File sourceDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            // 1. 定位目标源码文件
            String classNamePath = targetClass.replace('.', '/') + ".java";
            File sourceFile = new File(sourceDirectory, classNamePath);
            if (!sourceFile.exists()) {
                throw new MojoExecutionException("源码文件不存在：" + sourceFile.getPath());
            }

            // 2. 解析源码为AST
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(sourceFile));

            // 3. 获取目标类节点
            ClassOrInterfaceDeclaration classDecl = cu.getClassByName(targetClass.substring(targetClass.lastIndexOf('.') + 1))
                    .orElseThrow(() -> new MojoExecutionException("类 " + targetClass + " 不存在于源码文件中"));

            // 4. 检查方法是否已存在
            if (methodExists(classDecl)) {
                getLog().info("方法 " + methodName + " 已存在，无需生成");
                return;
            }

            // 5. 生成方法节点并添加到类中
            addMethodToClass(classDecl);

            // 6. 清理不必要的imports
            cleanUnusedImports(cu);


            // 7. 格式化CompilationUnit
            formatCompilationUnit(cu);

            // 8. 将修改后的AST写回源码文件（toString()会自动格式化）
            Files.write(sourceFile.toPath(), cu.toString().getBytes());
            getLog().info("成功为类 " + targetClass + " 添加方法 " + methodName + " 到源码文件");


        } catch (Exception e) {
            throw new MojoExecutionException("生成源码方法失败", e);
        }
    }

    // 检查方法是否存在（基于AST）
    private boolean methodExists(ClassOrInterfaceDeclaration classDecl) {
        return classDecl.getMethods().stream()
                .anyMatch(m -> m.getNameAsString().equals(methodName)
                        && m.getParameters().size() == countParams(methodParams));
    }

    // 统计参数个数
    private int countParams(String params) {
        if (params == null || params.trim().isEmpty()) {
            return 0;
        }
        return params.split(",").length;
    }

    // 生成方法节点并添加到类
    private void addMethodToClass(ClassOrInterfaceDeclaration classDecl) {
        // 创建方法声明（public void methodName(...) { ... }）
        MethodDeclaration method = new MethodDeclaration()
                .setName(methodName)
                .setType(new VoidType())
                .addModifier(Modifier.Keyword.PUBLIC);

        // 添加方法参数（解析methodParams字符串）
        if (!methodParams.trim().isEmpty()) {
            String[] params = methodParams.split(",");
            for (String param : params) {
                String[] paramParts = param.trim().split(" ");
                if (paramParts.length == 2) {
                    method.addParameter(paramParts[0], paramParts[1]); // 类型 + 参数名
                }
            }
        }

        // 设置方法体
        method.setBody(StaticJavaParser.parseBlock("{" + methodBody + "}"));

        // 将方法添加到类中
        classDecl.addMember(method);
    }



    /**
     * 清理不必要的imports
     * @param cu 编译单元
     */
    private void cleanUnusedImports(CompilationUnit cu) {
        // 获取所有在代码中实际使用的类型
        Set<String> usedTypes = collectUsedTypes(cu);

        // 移除未使用的imports
        cu.getImports().removeIf(importDecl -> {
            String importName = importDecl.getNameAsString();
            // 获取简单类名（去掉包路径）
            String simpleName = importName.substring(importName.lastIndexOf('.') + 1);

            // 如果是静态导入，需要特殊处理
            if (importDecl.isStatic()) {
                return !isStaticImportUsed(importDecl, cu);
            }

            // 检查是否在java.lang包下（这些不需要显式导入）
            if (importName.startsWith("java.lang.")) {
                return true;
            }

            // 检查是否在同一个包下（这些不需要显式导入）
            String currentPackage = cu.getPackageDeclaration()
                    .map(pd -> pd.getNameAsString())
                    .orElse("");
            if (importName.startsWith(currentPackage + ".")) {
                return true;
            }

            // 检查该类型是否在代码中被使用
            return !usedTypes.contains(simpleName);
        });
    }

    /**
     * 收集代码中实际使用的所有类型名称
     * @param cu 编译单元
     * @return 使用的类型名称集合
     */
    private Set<String> collectUsedTypes(CompilationUnit cu) {
        Set<String> usedTypes = new HashSet<>();

        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(com.github.javaparser.ast.type.ClassOrInterfaceType type, Void arg) {
                usedTypes.add(type.getNameAsString());
                super.visit(type, arg);
            }

            @Override
            public void visit(com.github.javaparser.ast.body.VariableDeclarator declarator, Void arg) {
                usedTypes.add(declarator.getType().asString());
                super.visit(declarator, arg);
            }

            @Override
            public void visit(com.github.javaparser.ast.expr.MethodCallExpr call, Void arg) {
                if (call.getScope().isPresent()) {
                    String scopeStr = call.getScope().get().toString();
                    // 如果scope是一个类型名，添加到使用集合中
                    if (Character.isUpperCase(scopeStr.charAt(0))) {
                        usedTypes.add(scopeStr);
                    }
                }
                super.visit(call, arg);
            }
        }, null);

        return usedTypes;
    }

    /**
     * 检查静态导入是否被使用
     * @param importDecl 静态导入声明
     * @param cu 编译单元
     * @return 是否被使用
     */
    private boolean isStaticImportUsed(ImportDeclaration importDecl, CompilationUnit cu) {
        String importName = importDecl.getNameAsString();
        String memberName = importName.substring(importName.lastIndexOf('.') + 1);

        // 检查是否有对应的静态方法或字段调用
        final boolean[] isUsed = {false};

        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(com.github.javaparser.ast.expr.MethodCallExpr call, Void arg) {
                if (!call.getScope().isPresent() && call.getNameAsString().equals(memberName)) {
                    isUsed[0] = true;
                }
                super.visit(call, arg);
            }

            @Override
            public void visit(com.github.javaparser.ast.expr.NameExpr name, Void arg) {
                if (name.getNameAsString().equals(memberName)) {
                    isUsed[0] = true;
                }
                super.visit(name, arg);
            }
        }, null);

        return isUsed[0];
    }


    /**
     * 格式化CompilationUnit
     * 对AST进行格式化处理，包括：
     * - 导入语句排序（普通导入在前，静态导入在后，按名称排序）
     * - 确保代码结构规范
     * 
     * 注意：代码的缩进和格式会在调用 cu.toString() 时自动应用
     * 
     * @param cu 编译单元
     */
    private void formatCompilationUnit(CompilationUnit cu) {
        // 对导入语句进行排序
        cu.getImports().sort((i1, i2) -> {
            // 静态导入放在最后
            if (i1.isStatic() != i2.isStatic()) {
                return i1.isStatic() ? 1 : -1;
            }
            // 按名称排序
            return i1.getNameAsString().compareTo(i2.getNameAsString());
        });
        
        // CompilationUnit.toString() 会自动应用格式化
        // 包括：缩进、换行、代码块格式等
    }
}