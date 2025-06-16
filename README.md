# basic-nfc

This plugin was designed to do the most basic of functionality for NFC without using other peoples bullshit!

## Install

```bash
npm install basic-nfc
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`writeNFC(...)`](#writenfc)
* [`scanNFC(...)`](#scannfc)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### writeNFC(...)

```typescript
writeNFC(options: { message: string; }) => Promise<WriteNFCResult>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ message: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#writenfcresult">WriteNFCResult</a>&gt;</code>

--------------------


### scanNFC(...)

```typescript
scanNFC(simulatePayload?: string | undefined) => Promise<ScanNFCResult>
```

| Param                 | Type                |
| --------------------- | ------------------- |
| **`simulatePayload`** | <code>string</code> |

**Returns:** <code>Promise&lt;<a href="#scannfcresult">ScanNFCResult</a>&gt;</code>

--------------------


### Interfaces


#### WriteNFCResult

| Prop         | Type                |
| ------------ | ------------------- |
| **`result`** | <code>string</code> |


#### ScanNFCResult

| Prop           | Type                     |
| -------------- | ------------------------ |
| **`messages`** | <code>NFCRecord[]</code> |
| **`tagID`**    | <code>string</code>      |


#### NFCRecord

| Prop          | Type                |
| ------------- | ------------------- |
| **`tnf`**     | <code>number</code> |
| **`type`**    | <code>string</code> |
| **`id`**      | <code>string</code> |
| **`payload`** | <code>string</code> |

</docgen-api>
