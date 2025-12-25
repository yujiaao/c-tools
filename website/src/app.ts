// src/app.ts
import Prism from 'prism-react-renderer/prism';

(typeof global !== 'undefined' ? global : window).Prism = Prism;

require('prismjs/components/prism-dart');
require('prismjs/components/prism-swift');
require('prismjs/components/prism-kotlin');
