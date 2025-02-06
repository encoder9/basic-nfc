import { registerPlugin } from '@capacitor/core';

import type { BasicNFCPlugin } from './definitions';

const BasicNFC = registerPlugin<BasicNFCPlugin>('BasicNFC', {
  web: () => import('./web').then((m) => new m.BasicNFCWeb()),
});

export * from './definitions';
export { BasicNFC };
