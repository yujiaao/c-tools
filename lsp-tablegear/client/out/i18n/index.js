"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.getMessages = exports.getLocale = void 0;
const en_1 = __importDefault(require("./en"));
const ja_1 = __importDefault(require("./ja"));
const zh_1 = __importDefault(require("./zh"));
function getLocale(language) {
    const lang = (language || 'en').toLowerCase();
    if (lang.startsWith('zh'))
        return 'zh';
    if (lang.startsWith('ja'))
        return 'ja';
    return 'en';
}
exports.getLocale = getLocale;
function getMessages(locale) {
    switch (locale) {
        case 'zh':
            return zh_1.default;
        case 'ja':
            return ja_1.default;
        default:
            return en_1.default;
    }
}
exports.getMessages = getMessages;
//# sourceMappingURL=index.js.map