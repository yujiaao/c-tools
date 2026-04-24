import en from './en';
import ja from './ja';
import { LocaleCode, Messages } from './types';
import zh from './zh';

export function getLocale(language: string | undefined): LocaleCode {
	const lang = (language || 'en').toLowerCase();
	if (lang.startsWith('zh')) return 'zh';
	if (lang.startsWith('ja')) return 'ja';
	return 'en';
}

export function getMessages(locale: LocaleCode): Messages {
	switch (locale) {
		case 'zh':
			return zh;
		case 'ja':
			return ja;
		default:
			return en;
	}
}

export type { LocaleCode, Messages };
