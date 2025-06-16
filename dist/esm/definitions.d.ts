export interface NFCRecord {
    tnf: number;
    type: string;
    id: string;
    payload: string;
}
export interface ScanNFCResult {
    messages: NFCRecord[];
    tagID: string;
}
export interface WriteNFCResult {
    result: string;
}
export interface BasicNFCPlugin {
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    writeNFC(options: {
        message: string;
    }): Promise<WriteNFCResult>;
    scanNFC(simulatePayload?: string): Promise<ScanNFCResult>;
}
