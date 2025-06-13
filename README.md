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
writeNFC(options: { message: string; }) => Promise<{ result: string; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ message: string; }</code> |

**Returns:** <code>Promise&lt;{ result: string; }&gt;</code>

--------------------


### scanNFC(...)

```typescript
scanNFC(simulatePayload?: string | undefined) => Promise<{ message: string; }>
```

| Param                 | Type                |
| --------------------- | ------------------- |
| **`simulatePayload`** | <code>string</code> |

**Returns:** <code>Promise&lt;{ message: string; }&gt;</code>

--------------------

</docgen-api>
