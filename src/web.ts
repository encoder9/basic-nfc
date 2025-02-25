import { WebPlugin } from '@capacitor/core';

import type { BasicNFCPlugin } from './definitions';

export class BasicNFCWeb extends WebPlugin implements BasicNFCPlugin {
	async echo(options: { value: string }): Promise<{ value: string }> {
		console.log('ECHO', options);
		return options;
	}
	
	async writeNFC(options: { message: string }): Promise<{ result: string }> {
		console.warn('NFC writing is not supported in web environment', options);
		return { result: 'NFC writing is not supported in web environment' };
	}
	
	async scanNFC(simulatePayload?: string): Promise<{ message: string }> {
		console.warn('NFC is not supported in web environment', simulatePayload);
		return { message: 'NFC is not supported in web environment' };
	}
}
