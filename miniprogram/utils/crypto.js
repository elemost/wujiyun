import CryptoJS from 'crypto-js';
import { isString } from "lodash";

const DEFAULT_KEY = 'wujicloudkey2025';
//加密
export const cryptoEncode = (data, key=DEFAULT_KEY) => {
  if (!isString(data)) {
    console.error('加密失败: 输入必须为字符串');
    return '';
  }
  try {
    const keyHex = CryptoJS.enc.Utf8.parse(key);
    const encrypted = CryptoJS.AES.encrypt(data, keyHex, {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7
    });
    return encrypted.toString();
  } catch (error) {
    console.error('加密过程中发生错误:', error);
    return '';
  }
};
// 解密
export const cryptoDecode = (data, key=DEFAULT_KEY) => {
  if (!isString(data)) {
    console.error('解密失败: 输入必须为字符串');
    return '';
  }

  try {
    const keyHex = CryptoJS.enc.Utf8.parse(key);
    const decrypted = CryptoJS.AES.decrypt(data, keyHex, {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7
    });
    return decrypted.toString(CryptoJS.enc.Utf8);
  } catch (error) {
    console.error('解密过程中发生错误:', error);
    return '';
  }
};