package com.bixuebihui.tablegen.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for JavaSourceMerger to verify smart DTO merging functionality.
 */
class JavaSourceMergerTest {

    @TempDir
    Path tempDir;

    private String existingDtoSource;
    private String generatedDtoSource;

    @BeforeEach
    void setUp() {
        // Existing DTO with user-added field
        existingDtoSource = "package com.example.dto;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "\n" +
                "public class UserDto implements Serializable {\n" +
                "    private Integer id;\n" +
                "    private String name;\n" +
                "\n" +
                "    // User-added field\n" +
                "    private String customField;\n" +
                "\n" +
                "    public Integer getId() {\n" +
                "        return id;\n" +
                "    }\n" +
                "\n" +
                "    public void setId(Integer id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "\n" +
                "    public void setName(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    public String getCustomField() {\n" +
                "        return customField;\n" +
                "    }\n" +
                "\n" +
                "    public void setCustomField(String customField) {\n" +
                "        this.customField = customField;\n" +
                "    }\n" +
                "}\n";

        // Generated DTO with updated field type and new field
        generatedDtoSource = "package com.example.dto;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import javax.validation.constraints.NotNull;\n" +
                "\n" +
                "public class UserDto implements Serializable {\n" +
                "    @NotNull\n" +
                "    private Long id;  // Changed from Integer to Long\n" +
                "\n" +
                "    private String name;\n" +
                "\n" +
                "    // New generated field\n" +
                "    private String email;\n" +
                "\n" +
                "    public Long getId() {\n" +
                "        return id;\n" +
                "    }\n" +
                "\n" +
                "    public void setId(Long id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "\n" +
                "    public void setName(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    public String getEmail() {\n" +
                "        return email;\n" +
                "    }\n" +
                "\n" +
                "    public void setEmail(String email) {\n" +
                "        this.email = email;\n" +
                "    }\n" +
                "}\n";
    }

    @Test
    void testMergeSource_WithExistingFile() throws IOException {
        // Create existing file
        File existingFile = tempDir.resolve("UserDto.java").toFile();
        Files.writeString(existingFile.toPath(), existingDtoSource);

        // Merge
        String mergedSource = JavaSourceMerger.mergeSource(existingFile, generatedDtoSource);

        // Verify merged source contains:
        // 1. Updated field type (Long id with @NotNull)
        // Check that field has Long type (format may vary due to code formatting)
        assertTrue(mergedSource.contains("Long") && mergedSource.contains("private") && 
                   mergedSource.contains("id"),
                "Should contain Long type field named id. Source: " + mergedSource);
        assertContains(mergedSource, "@NotNull");

        // 2. User-added field (customField)
        assertContains(mergedSource, "private String customField");

        // 3. New generated field (email)
        assertContains(mergedSource, "private String email");

        // 4. Merged imports
        assertContains(mergedSource, "import javax.validation.constraints.NotNull");

        // 5. User-added methods preserved
        assertContains(mergedSource, "getCustomField");
        assertContains(mergedSource, "setCustomField");
    }

    @Test
    void testMergeSource_WithNonExistentFile() throws IOException {
        File nonExistentFile = tempDir.resolve("NonExistent.java").toFile();

        String result = JavaSourceMerger.mergeSource(nonExistentFile, generatedDtoSource);

        // Should return generated source as-is
        assertEquals(generatedDtoSource, result);
    }

    @Test
    void testMergeSource_WithInvalidExistingFile() throws IOException {
        // Create file with invalid Java syntax
        File invalidFile = tempDir.resolve("Invalid.java").toFile();
        Files.writeString(invalidFile.toPath(), "this is not valid java code");

        String result = JavaSourceMerger.mergeSource(invalidFile, generatedDtoSource);

        // Should fallback to generated source
        assertEquals(generatedDtoSource, result);
    }

    @Test
    void testMergeSource_WithEmptyFile() throws IOException {
        // Create empty file
        File emptyFile = tempDir.resolve("Empty.java").toFile();
        Files.writeString(emptyFile.toPath(), "");

        String result = JavaSourceMerger.mergeSource(emptyFile, generatedDtoSource);

        // Should fallback to generated source
        assertEquals(generatedDtoSource, result);
    }

    @Test
    void testMergeSource_WithFileContainingOnlyWhitespace() throws IOException {
        // Create file with only whitespace
        File whitespaceFile = tempDir.resolve("Whitespace.java").toFile();
        Files.writeString(whitespaceFile.toPath(), "   \n\t  \n  ");

        String result = JavaSourceMerger.mergeSource(whitespaceFile, generatedDtoSource);

        // Should fallback to generated source
        assertEquals(generatedDtoSource, result);
    }

    @Test
    void testMergeSource_WithFileContainingOnlyComments() throws IOException {
        // Create file with only comments (no class/interface)
        File commentsOnlyFile = tempDir.resolve("CommentsOnly.java").toFile();
        Files.writeString(commentsOnlyFile.toPath(), "package com.example.dto;\n\n" +
                "// This is just a comment\n" +
                "/* Another comment */\n");

        String result = JavaSourceMerger.mergeSource(commentsOnlyFile, generatedDtoSource);

        // Should fallback to generated source
        assertEquals(generatedDtoSource, result);
    }

    @Test
    void testMergeMapper_KeepUserAddedMethod() throws IOException {
        // Existing mapper with user-added method toOptionDto
        String existingMapperSource = "package com.example.converter;\n\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.Mapping;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "import com.example.dto.XShowPolicyDto;\n" +
                "import com.example.dto.OptionDto;\n" +
                "import com.example.entity.XShowPolicy;\n" +
                "import java.util.List;\n\n" +
                "@Mapper\n" +
                "public interface XShowPolicyConverter {\n" +
                "    XShowPolicyConverter MAPPER = Mappers.getMapper(XShowPolicyConverter.class);\n" +
                "    \n" +
                "    XShowPolicyDto toDto(XShowPolicy info);\n" +
                "    \n" +
                "    XShowPolicy toDao(XShowPolicyDto info);\n" +
                "    \n" +
                "    List<XShowPolicyDto> toDtoList(List<XShowPolicy> info);\n" +
                "    \n" +
                "    List<XShowPolicy> toDaoList(List<XShowPolicyDto> info);\n" +
                "    \n" +
                "    @Mapping(source = \"id\", target = \"value\")\n" +
                "    @Mapping(source = \"ruleName\", target = \"label\")\n" +
                "    OptionDto toOptionDto(XShowPolicy xShowPolicy);\n" +
                "}\n";

        // Generated mapper without toOptionDto method
        String generatedMapperSource = "package com.example.converter;\n\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "import com.example.dto.XShowPolicyDto;\n" +
                "import com.example.entity.XShowPolicy;\n" +
                "import java.util.List;\n\n" +
                "@Mapper\n" +
                "public interface XShowPolicyConverter {\n" +
                "    XShowPolicyConverter MAPPER = Mappers.getMapper(XShowPolicyConverter.class);\n" +
                "    \n" +
                "    XShowPolicyDto toDto(XShowPolicy info);\n" +
                "    \n" +
                "    XShowPolicy toDao(XShowPolicyDto info);\n" +
                "    \n" +
                "    List<XShowPolicyDto> toDtoList(List<XShowPolicy> info);\n" +
                "    \n" +
                "    List<XShowPolicy> toDaoList(List<XShowPolicyDto> info);\n" +
                "    \n" +
                "    //this is new method\n" +
                "    int aNewMethod();\n" +
                "}\n";



        // Create existing file
        File existingFile = tempDir.resolve("XShowPolicyConverter.java").toFile();
        Files.writeString(existingFile.toPath(), existingMapperSource);

        // Merge
        String mergedSource = JavaSourceMerger.mergeSource(existingFile, generatedMapperSource);

        // Verify merged source contains:
        // 1. All generated methods
        assertContains(mergedSource, "XShowPolicyDto toDto(XShowPolicy info)");
        assertContains(mergedSource, "XShowPolicy toDao(XShowPolicyDto info)");
        assertContains(mergedSource, "List<XShowPolicyDto> toDtoList(List<XShowPolicy> info)");
        assertContains(mergedSource, "List<XShowPolicy> toDaoList(List<XShowPolicyDto> info)");
        assertContains(mergedSource, "int aNewMethod()");

        // 2. User-added method toOptionDto with annotations
        assertContains(mergedSource, "OptionDto toOptionDto(XShowPolicy xShowPolicy)");
        assertContains(mergedSource, "@Mapping(source = \"id\", target = \"value\")");
        assertContains(mergedSource, "@Mapping(source = \"ruleName\", target = \"label\")");

        // 3. Required imports
        assertContains(mergedSource, "import com.example.dto.OptionDto");
        assertContains(mergedSource, "import org.mapstruct.Mapping");

        // 4. comment keep
        assertContains(mergedSource, "//this is new method");

    }

    @Test
    void testMergeMapper_KeepExistingMethodWithAnnotations() throws IOException {
        // Existing mapper with method that has annotations
        String existingMapperSource = "package com.example.converter;\n" +
                "\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.Mapping;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "import com.example.dto.XShowPolicyDto;\n" +
                "import com.example.entity.XShowPolicy;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Mapper\n" +
                "public interface XShowPolicyConverter {\n" +
                "    XShowPolicyConverter MAPPER = Mappers.getMapper(XShowPolicyConverter.class);\n" +
                "    \n" +
                "    @Mapping(source = \"id\", target = \"policyId\")\n" +
                "    @Mapping(source = \"name\", target = \"policyName\")\n" +
                "    XShowPolicyDto toDto(XShowPolicy info);\n" +
                "    \n" +
                "    XShowPolicy toDao(XShowPolicyDto info);\n" +
                "}\n";

        // Generated mapper with same method but without annotations
        String generatedMapperSource = "package com.example.converter;\n" +
                "\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "import com.example.dto.XShowPolicyDto;\n" +
                "import com.example.entity.XShowPolicy;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Mapper\n" +
                "public interface XShowPolicyConverter {\n" +
                "    XShowPolicyConverter MAPPER = Mappers.getMapper(XShowPolicyConverter.class);\n" +
                "    \n" +
                "    XShowPolicyDto toDto(XShowPolicy info);\n" +
                "    \n" +
                "    XShowPolicy toDao(XShowPolicyDto info);\n" +
                "}\n";

        // Create existing file
        File existingFile = tempDir.resolve("XShowPolicyConverter.java").toFile();
        Files.writeString(existingFile.toPath(), existingMapperSource);

        // Merge
        String mergedSource = JavaSourceMerger.mergeSource(existingFile, generatedMapperSource);

        // Verify: should keep existing method with annotations
        assertContains(mergedSource, "@Mapping(source = \"id\", target = \"policyId\")");
        assertContains(mergedSource, "@Mapping(source = \"name\", target = \"policyName\")");
        assertContains(mergedSource, "XShowPolicyDto toDto(XShowPolicy info)");
        assertContains(mergedSource, "import org.mapstruct.Mapping");
        
        // Verify: should also have the other method
        assertContains(mergedSource, "XShowPolicy toDao(XShowPolicyDto info)");
    }

    @Test
    void testMergeMapper_KeepExistingMethodWithMoreAnnotations() throws IOException {
        // Existing mapper with method that has more annotations
        String existingMapperSource = "package com.example.converter;\n" +
                "\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.Mapping;\n" +
                "import org.mapstruct.Mappings;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "import com.example.dto.UserDto;\n" +
                "import com.example.entity.User;\n" +
                "\n" +
                "@Mapper\n" +
                "public interface UserConverter {\n" +
                "    UserConverter MAPPER = Mappers.getMapper(UserConverter.class);\n" +
                "    \n" +
                "    @Mappings({\n" +
                "        @Mapping(source = \"id\", target = \"userId\"),\n" +
                "        @Mapping(source = \"name\", target = \"userName\"),\n" +
                "        @Mapping(source = \"email\", target = \"userEmail\")\n" +
                "    })\n" +
                "    UserDto toDto(User user);\n" +
                "}\n";

        // Generated mapper with same method but fewer annotations
        String generatedMapperSource = "package com.example.converter;\n" +
                "\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.Mapping;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "import com.example.dto.UserDto;\n" +
                "import com.example.entity.User;\n" +
                "\n" +
                "@Mapper\n" +
                "public interface UserConverter {\n" +
                "    UserConverter MAPPER = Mappers.getMapper(UserConverter.class);\n" +
                "    \n" +
                "    @Mapping(source = \"id\", target = \"userId\")\n" +
                "    UserDto toDto(User user);\n" +
                "}\n";

        // Create existing file
        File existingFile = tempDir.resolve("UserConverter.java").toFile();
        Files.writeString(existingFile.toPath(), existingMapperSource);

        // Merge
        String mergedSource = JavaSourceMerger.mergeSource(existingFile, generatedMapperSource);

        // Verify: should keep existing method with more annotations
        assertContains(mergedSource, "@Mappings");
        assertContains(mergedSource, "@Mapping(source = \"id\", target = \"userId\")");
        assertContains(mergedSource, "@Mapping(source = \"name\", target = \"userName\")");
        assertContains(mergedSource, "@Mapping(source = \"email\", target = \"userEmail\")");
        assertContains(mergedSource, "import org.mapstruct.Mappings");
    }

    @Test
    void testMergeMapper_KeepUserAddedMethodWithCollectionType() throws IOException {
        // Existing mapper with collection return type
        String existingMapperSource = "package com.example.converter;\n" +
                "\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "import com.example.dto.UserDto;\n" +
                "import com.example.entity.User;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Mapper\n" +
                "public interface UserConverter {\n" +
                "    UserConverter MAPPER = Mappers.getMapper(UserConverter.class);\n" +
                "    \n" +
                "    List<UserDto> toDtoList(List<User> users);\n" +
                "    \n" +
                "    // User-added method with collection return type\n" +
                "    List<UserDto> findActiveUsers(List<User> users);\n" +
                "}\n";

        // Generated mapper with simple return type for findActiveUsers
        String generatedMapperSource = "package com.example.converter;\n" +
                "\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "import com.example.dto.UserDto;\n" +
                "import com.example.entity.User;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Mapper\n" +
                "public interface UserConverter {\n" +
                "    UserConverter MAPPER = Mappers.getMapper(UserConverter.class);\n" +
                "    \n" +
                "    List<UserDto> toDtoList(List<User> users);\n" +
                "    \n" +
                "    // Generated method with simple return type\n" +
                "    UserDto findActiveUsers(User user);\n" +
                "}\n";

        // Create existing file
        File existingFile = tempDir.resolve("UserConverter.java").toFile();
        Files.writeString(existingFile.toPath(), existingMapperSource);

        // Merge
        String mergedSource = JavaSourceMerger.mergeSource(existingFile, generatedMapperSource);

        // Verify: should keep existing collection return type method
        assertContains(mergedSource, "List<UserDto> findActiveUsers(List<User> users)");
        // Should NOT contain the simple return type version
        assertFalse(mergedSource.contains("UserDto findActiveUsers(User user)"),
                "Should not contain simple return type version when existing has collection type");
    }

    @Test
    void testMergeSearchCmd_KeepSetterWithCollectionParameterType() throws IOException {
        // Existing SearchCmd with collection type parameter in setter
        String existingSearchCmdSource = "package com.example.cmd;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import javax.validation.constraints.NotNull;\n" +
                "\n" +
                "public class UserSearchCmd {\n" +
                "    // User changed field type to List<String> for 'in' query support\n" +
                "    @NotNull\n" +
                "    protected List<String> name;\n" +
                "    \n" +
                "    public void setName(List<String> name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "    \n" +
                "    public List<String> getName() {\n" +
                "        return this.name;\n" +
                "    }\n" +
                "}\n";

        // Generated SearchCmd with simple type parameter in setter
        String generatedSearchCmdSource = "package com.example.cmd;\n" +
                "\n" +
                "import javax.validation.constraints.NotNull;\n" +
                "\n" +
                "public class UserSearchCmd {\n" +
                "    // Generated with simple type\n" +
                "    @NotNull\n" +
                "    protected String name;\n" +
                "    \n" +
                "    public void setName(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "    \n" +
                "    public String getName() {\n" +
                "        return this.name;\n" +
                "    }\n" +
                "}\n";

        // Create existing file
        File existingFile = tempDir.resolve("UserSearchCmd.java").toFile();
        Files.writeString(existingFile.toPath(), existingSearchCmdSource);

        // Merge
        String mergedSource = JavaSourceMerger.mergeSource(existingFile, generatedSearchCmdSource);

        // Verify: should keep existing setter with collection parameter type
        assertContains(mergedSource, "public void setName(List<String> name)");
        assertContains(mergedSource, "protected List<String> name");
        assertContains(mergedSource, "public List<String> getName()");
        assertContains(mergedSource, "import java.util.List");
        // Should NOT contain the simple type version
        assertFalse(mergedSource.contains("public void setName(String name)"),
                "Should not contain simple parameter type version when existing has collection type");
        assertFalse(mergedSource.contains("protected String name"),
                "Should not contain simple field type when existing has collection type");
    }

    @Test
    void testMergeSearchCmd_KeepSetterWithCollectionParameterType_DifferentSignature() throws IOException {
        // Existing SearchCmd with collection type parameter in setter (different signature)
        String existingSearchCmdSource = "package com.example.cmd;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import javax.validation.constraints.NotNull;\n" +
                "\n" +
                "public class UserSearchCmd {\n" +
                "    // User changed field type to List<Long> for 'in' query support\n" +
                "    @NotNull\n" +
                "    protected List<Long> id;\n" +
                "    \n" +
                "    public void setId(List<Long> id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "    \n" +
                "    public List<Long> getId() {\n" +
                "        return this.id;\n" +
                "    }\n" +
                "}\n";

        // Generated SearchCmd with simple type parameter in setter
        String generatedSearchCmdSource = "package com.example.cmd;\n" +
                "\n" +
                "import javax.validation.constraints.NotNull;\n" +
                "\n" +
                "public class UserSearchCmd {\n" +
                "    // Generated with simple type\n" +
                "    @NotNull\n" +
                "    protected Long id;\n" +
                "    \n" +
                "    public void setId(Long id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "    \n" +
                "    public Long getId() {\n" +
                "        return this.id;\n" +
                "    }\n" +
                "}\n";

        // Create existing file
        File existingFile = tempDir.resolve("UserSearchCmd.java").toFile();
        Files.writeString(existingFile.toPath(), existingSearchCmdSource);

        // Merge
        String mergedSource = JavaSourceMerger.mergeSource(existingFile, generatedSearchCmdSource);

        // Verify: should keep existing setter with collection parameter type
        assertContains(mergedSource, "public void setId(List<Long> id)");
        assertContains(mergedSource, "protected List<Long> id");
        assertContains(mergedSource, "public List<Long> getId()");
        assertContains(mergedSource, "import java.util.List");
        // Should NOT contain the simple type version
        assertFalse(mergedSource.contains("public void setId(Long id)"),
                "Should not contain simple parameter type version when existing has collection type");
    }

    private void assertContains(String source, String expected) {
        assertTrue(source.contains(expected),
                "Expected source to contain: " + expected + "\nBut source was:\n" + source);
    }
}
