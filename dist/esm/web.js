import { WebPlugin } from '@capacitor/core';
export class BasicNFCWeb extends WebPlugin {
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
    async writeNFC(options) {
        console.warn('NFC writing is not supported in web environment', options);
        return { result: 'NFC writing is not supported in web environment' };
    }
    async scanNFC(simulatePayload) {
        console.warn('NFC is not supported in web environment', simulatePayload);
        return { message: 'NFC is not supported in web environment' };
    }
}
//# sourceMappingURL=web.js.map