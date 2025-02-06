import { BasicNFC } from 'basic-nfc';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    BasicNFC.echo({ value: inputValue })
}
