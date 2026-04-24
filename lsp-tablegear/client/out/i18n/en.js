"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const en = {
    notTablegenOpen: 'Open a tablegen/es config file first.',
    notTablegenFile: 'Current file is not a tablegen/es config file.',
    savedMessage: 'TableGear configuration saved.',
    panelTitle: 'TableGear Config Editor',
    sectionGroups: 'Sections',
    currentFile: 'Current file',
    saveButton: 'Save Configuration',
    reloadButton: 'Reload Current File',
    resetButton: 'Reset Defaults',
    showConfiguredOnly: 'Show Configured Only',
    fileTooltip: 'TableGear config file',
    sections: {
        database: 'Database',
        codegen: 'Code Generation',
        advanced: 'Advanced'
    },
    fields: {
        projectname: { label: 'Project Name', description: 'Project name used in generated code.' },
        className: { label: 'JDBC Driver', description: 'JDBC driver class name, e.g. com.mysql.cj.jdbc.Driver.' },
        dburl: { label: 'Database URL', description: 'Database JDBC URL.' },
        username: { label: 'Username', description: 'Database username.' },
        password: { label: 'Password', description: 'Database password.' },
        schema: { label: 'Schema', description: 'Database schema/catalog used for filtering tables.' },
        table_owner: { label: 'Table Owner', description: 'Table owner for Oracle/DB2 etc.' },
        table_list: { label: 'Table List', description: 'Tables to generate, comma-separated or regex.' },
        exclude_table_list: { label: 'Excluded Tables', description: 'Tables to exclude; takes precedence over table_list.' },
        package_name: { label: 'Package Name', description: 'Root package name for generated code.' },
        src_dir: { label: 'Source Directory', description: 'Output directory for main source files.' },
        test_dir: { label: 'Test Directory', description: 'Output directory for test source files.' },
        resource_dir: { label: 'Resource Directory', description: 'Output directory for resources.' },
        service_dir: { label: 'Service Directory', description: 'Output directory for service classes.' },
        controller_dir: { label: 'Controller Directory', description: 'Output directory for controller classes.' },
        dto_dir: { label: 'DTO Directory', description: 'Output directory for DTO classes.' },
        cmd_dir: { label: 'CMD Directory', description: 'Output directory for command/DTO classes.' },
        enable_merge: { label: 'Enable Smart Merge', description: 'Keep user changes in Dto/SearchCmd/Converter when regenerating.' },
        generate_all: { label: 'Generate All', description: 'Enable full generation instead of incremental mode.' },
        over_write_all: { label: 'Overwrite All Files', description: 'Overwrite existing generated files.' },
        use_annotation: { label: 'Use Annotation', description: 'Enable Jakarta Validation/Swagger annotations.' },
        r2dbc: { label: 'Generate R2DBC', description: 'Generate R2DBC style code.' },
        api_doc_provider: { label: 'API Doc Provider', description: 'API doc provider. springdoc disables Swagger annotations.' },
        use_swagger: { label: 'Generate Swagger Annotation', description: 'Only effective when api_doc_provider=swagger.' },
        swagger_version: { label: 'Swagger Version', description: 'Swagger/OpenAPI version.' },
        view_list: { label: 'View List', description: 'Supports "view: select ...;" or "view: @relative/path.sql;".' },
        ignore_fields: { label: 'Ignored Fields', description: 'Format: table:field1,field2 or field1,field2.' },
        encrypted_fields: { label: 'Encrypted Fields', description: 'Format: table1:field1,field2;table2:field3,field4;' }
    }
};
exports.default = en;
//# sourceMappingURL=en.js.map