import { registerPlugin } from '@capacitor/core';
const BasicNFC = registerPlugin('BasicNFC', {
    web: () => import('./web').then((m) => new m.BasicNFCWeb()),
});
export * from './definitions';
export { BasicNFC };
//# sourceMappingURL=index.js.map