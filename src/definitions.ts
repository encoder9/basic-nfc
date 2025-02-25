export interface BasicNFCPlugin {
	echo(options: { value: string }): Promise<{ value: string }>;
	writeNFC(options: { message: string }): Promise<{ result: string }>;
	scanNFC(simulatePayload?: string): Promise<{ message: string }>;
}
