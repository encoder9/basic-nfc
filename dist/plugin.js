var capacitorBasicNFC = (function (exports, core) {
    'use strict';

    const BasicNFC = core.registerPlugin('BasicNFC', {
        web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.BasicNFCWeb()),
    });

    class BasicNFCWeb extends core.WebPlugin {
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
            return {
                messages: [],
                tagID: '',
            };
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        BasicNFCWeb: BasicNFCWeb
    });

    exports.BasicNFC = BasicNFC;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
