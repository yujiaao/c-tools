export type LocaleCode = 'zh' | 'en' | 'ja';

export interface FieldI18n {
	label?: string;
	description?: string;
}

export interface Messages {
	notTablegenOpen: string;
	notTablegenFile: string;
	savedMessage: string;
	panelTitle: string;
	sectionGroups: string;
	currentFile: string;
	saveButton: string;
	reloadButton: string;
	resetButton: string;
	showConfiguredOnly: string;
	fileTooltip: string;
	sections: Record<string, string>;
	fields: Record<string, FieldI18n>;
}
