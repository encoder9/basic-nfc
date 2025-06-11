import { WebPlugin } from '@capacitor/core';
import type { BasicNFCPlugin } from './definitions';
export declare class BasicNFCWeb extends WebPlugin implements BasicNFCPlugin {
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    writeNFC(options: {
        message: string;
    }): Promise<{
        result: string;
    }>;
    scanNFC(simulatePayload?: string): Promise<{
        message: string;
    }>;
}
