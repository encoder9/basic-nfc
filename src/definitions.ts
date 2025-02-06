export interface BasicNFCPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
