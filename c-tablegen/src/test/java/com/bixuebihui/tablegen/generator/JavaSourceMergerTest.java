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
        existingDtoSource = """
                package com.example.dto;

                import java.io.Serializable;

                public class UserDto implements Serializable {
                    private Integer id;
                    private String name;

                    // User-added field
                    private String customField;

                    public Integer getId() {
                        return id;
                    }

                    public void setId(Integer id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getCustomField() {
                        return customField;
                    }

                    public void setCustomField(String customField) {
                        this.customField = customField;
                    }
                }
                """;

        // Generated DTO with updated field type and new field
        generatedDtoSource = """
                package com.example.dto;

                import java.io.Serializable;
                import jakarta.validation.constraints.NotNull;

                public class UserDto implements Serializable {
                    @NotNull
                    private Long id;  // Changed from Integer to Long

                    private String name;

                    // New generated field
                    private String email;

                    public Long getId() {
                        return id;
                    }

                    public void setId(Long id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getEmail() {
                        return email;
                    }

                    public void setEmail(String email) {
                        this.email = email;
                    }
                }
                """;
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
        assertContains(mergedSource, "import jakarta.validation.constraints.NotNull");

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
        Files.writeString(commentsOnlyFile.toPath(), """
                package com.example.dto;
                
                // This is just a comment
                /* Another comment */
                """);

        String result = JavaSourceMerger.mergeSource(commentsOnlyFile, generatedDtoSource);

        // Should fallback to generated source
        assertEquals(generatedDtoSource, result);
    }

    @Test
    void testMergeMapper_KeepUserAddedMethod() throws IOException {
        // Existing mapper with user-added method toOptionDto
        String existingMapperSource = """
                package com.example.converter;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.Mapping;
                import org.mapstruct.factory.Mappers;
                import com.example.dto.XShowPolicyDto;
                import com.example.dto.OptionDto;
                import com.example.entity.XShowPolicy;
                import java.util.List;
                
                @Mapper
                public interface XShowPolicyConverter {
                    XShowPolicyConverter MAPPER = Mappers.getMapper(XShowPolicyConverter.class);
                    
                    XShowPolicyDto toDto(XShowPolicy info);
                    
                    XShowPolicy toDao(XShowPolicyDto info);
                    
                    List<XShowPolicyDto> toDtoList(List<XShowPolicy> info);
                    
                    List<XShowPolicy> toDaoList(List<XShowPolicyDto> info);
                    
                    @Mapping(source = "id", target = "value")
                    @Mapping(source = "ruleName", target = "label")
                    OptionDto toOptionDto(XShowPolicy xShowPolicy);
                }
                """;

        // Generated mapper without toOptionDto method
        String generatedMapperSource = """
                package com.example.converter;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.factory.Mappers;
                import com.example.dto.XShowPolicyDto;
                import com.example.entity.XShowPolicy;
                import java.util.List;
                
                @Mapper
                public interface XShowPolicyConverter {
                    XShowPolicyConverter MAPPER = Mappers.getMapper(XShowPolicyConverter.class);
                    
                    XShowPolicyDto toDto(XShowPolicy info);
                    
                    XShowPolicy toDao(XShowPolicyDto info);
                    
                    List<XShowPolicyDto> toDtoList(List<XShowPolicy> info);
                    
                    List<XShowPolicy> toDaoList(List<XShowPolicyDto> info);
                    
                    //this is new method
                    int aNewMethod();
                }""";



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
        String existingMapperSource = """
                package com.example.converter;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.Mapping;
                import org.mapstruct.factory.Mappers;
                import com.example.dto.XShowPolicyDto;
                import com.example.entity.XShowPolicy;
                import java.util.List;
                
                @Mapper
                public interface XShowPolicyConverter {
                    XShowPolicyConverter MAPPER = Mappers.getMapper(XShowPolicyConverter.class);
                    
                    @Mapping(source = "id", target = "policyId")
                    @Mapping(source = "name", target = "policyName")
                    XShowPolicyDto toDto(XShowPolicy info);
                    
                    XShowPolicy toDao(XShowPolicyDto info);
                }
                """;

        // Generated mapper with same method but without annotations
        String generatedMapperSource = """
                package com.example.converter;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.factory.Mappers;
                import com.example.dto.XShowPolicyDto;
                import com.example.entity.XShowPolicy;
                import java.util.List;
                
                @Mapper
                public interface XShowPolicyConverter {
                    XShowPolicyConverter MAPPER = Mappers.getMapper(XShowPolicyConverter.class);
                    
                    XShowPolicyDto toDto(XShowPolicy info);
                    
                    XShowPolicy toDao(XShowPolicyDto info);
                }
                """;

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
        String existingMapperSource = """
                package com.example.converter;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.Mapping;
                import org.mapstruct.Mappings;
                import org.mapstruct.factory.Mappers;
                import com.example.dto.UserDto;
                import com.example.entity.User;
                
                @Mapper
                public interface UserConverter {
                    UserConverter MAPPER = Mappers.getMapper(UserConverter.class);
                    
                    @Mappings({
                        @Mapping(source = "id", target = "userId"),
                        @Mapping(source = "name", target = "userName"),
                        @Mapping(source = "email", target = "userEmail")
                    })
                    UserDto toDto(User user);
                }
                """;

        // Generated mapper with same method but fewer annotations
        String generatedMapperSource = """
                package com.example.converter;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.Mapping;
                import org.mapstruct.factory.Mappers;
                import com.example.dto.UserDto;
                import com.example.entity.User;
                
                @Mapper
                public interface UserConverter {
                    UserConverter MAPPER = Mappers.getMapper(UserConverter.class);
                    
                    @Mapping(source = "id", target = "userId")
                    UserDto toDto(User user);
                }
                """;

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
        String existingMapperSource = """
                package com.example.converter;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.factory.Mappers;
                import com.example.dto.UserDto;
                import com.example.entity.User;
                import java.util.List;
                
                @Mapper
                public interface UserConverter {
                    UserConverter MAPPER = Mappers.getMapper(UserConverter.class);
                    
                    List<UserDto> toDtoList(List<User> users);
                    
                    // User-added method with collection return type
                    List<UserDto> findActiveUsers(List<User> users);
                }
                """;

        // Generated mapper with simple return type for findActiveUsers
        String generatedMapperSource = """
                package com.example.converter;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.factory.Mappers;
                import com.example.dto.UserDto;
                import com.example.entity.User;
                import java.util.List;
                
                @Mapper
                public interface UserConverter {
                    UserConverter MAPPER = Mappers.getMapper(UserConverter.class);
                    
                    List<UserDto> toDtoList(List<User> users);
                    
                    // Generated method with simple return type
                    UserDto findActiveUsers(User user);
                }
                """;

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
        String existingSearchCmdSource = """
                package com.example.cmd;
                
                import java.util.List;
                import jakarta.validation.constraints.NotNull;
                
                public class UserSearchCmd {
                    // User changed field type to List<String> for 'in' query support
                    @NotNull
                    protected List<String> name;
                    
                    public void setName(List<String> name) {
                        this.name = name;
                    }
                    
                    public List<String> getName() {
                        return this.name;
                    }
                }
                """;

        // Generated SearchCmd with simple type parameter in setter
        String generatedSearchCmdSource = """
                package com.example.cmd;
                
                import jakarta.validation.constraints.NotNull;
                
                public class UserSearchCmd {
                    // Generated with simple type
                    @NotNull
                    protected String name;
                    
                    public void setName(String name) {
                        this.name = name;
                    }
                    
                    public String getName() {
                        return this.name;
                    }
                }
                """;

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
        String existingSearchCmdSource = """
                package com.example.cmd;
                
                import java.util.List;
                import jakarta.validation.constraints.NotNull;
                
                public class UserSearchCmd {
                    // User changed field type to List<Long> for 'in' query support
                    @NotNull
                    protected List<Long> id;
                    
                    public void setId(List<Long> id) {
                        this.id = id;
                    }
                    
                    public List<Long> getId() {
                        return this.id;
                    }
                }
                """;

        // Generated SearchCmd with simple type parameter in setter
        String generatedSearchCmdSource = """
                package com.example.cmd;
                
                import jakarta.validation.constraints.NotNull;
                
                public class UserSearchCmd {
                    // Generated with simple type
                    @NotNull
                    protected Long id;
                    
                    public void setId(Long id) {
                        this.id = id;
                    }
                    
                    public Long getId() {
                        return this.id;
                    }
                }
                """;

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
