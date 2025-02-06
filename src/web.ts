import { WebPlugin } from '@capacitor/core';

import type { BasicNFCPlugin } from './definitions';

export class BasicNFCWeb extends WebPlugin implements BasicNFCPlugin {
	async echo(options: { value: string }): Promise<{ value: string }> {
		console.log('ECHO', options);
		return options;
	}
}
