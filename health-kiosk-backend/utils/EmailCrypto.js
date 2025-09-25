// utils/emailCrypto.js
const crypto = require('crypto');

const AES_KEY = Buffer.from(process.env.EMAIL_AES_KEY_BASE64, 'base64'); // 32 bytes
const HMAC_KEY = Buffer.from(process.env.EMAIL_HMAC_KEY_BASE64, 'base64'); // 32 bytes
const KEY_VERSION = process.env.EMAIL_KEY_VERSION || 'v1';

if (AES_KEY.length !== 32) throw new Error('EMAIL_AES_KEY_BASE64 must be 32 bytes (base64)');
if (HMAC_KEY.length < 16) throw new Error('EMAIL_HMAC_KEY_BASE64 should be sufficiently long');

function encryptEmail(plaintext) {
  // AES-256-GCM, 12 byte iv
  const iv = crypto.randomBytes(12);
  const cipher = crypto.createCipheriv('aes-256-gcm', AES_KEY, iv);
  const ciphertext = Buffer.concat([cipher.update(Buffer.from(plaintext, 'utf8')), cipher.final()]);
  const tag = cipher.getAuthTag(); // 16 bytes

  // store as base64 of iv|tag|ciphertext, 以及 key version
  const payload = Buffer.concat([iv, tag, ciphertext]).toString('base64');
  return { payload, keyVersion: KEY_VERSION };
}

function decryptEmail(payloadBase64) {
  const data = Buffer.from(payloadBase64, 'base64');
  const iv = data.slice(0, 12);
  const tag = data.slice(12, 28);
  const ciphertext = data.slice(28);
  const decipher = crypto.createDecipheriv('aes-256-gcm', AES_KEY, iv);
  decipher.setAuthTag(tag);
  const decrypted = Buffer.concat([decipher.update(ciphertext), decipher.final()]);
  return decrypted.toString('utf8');
}

// Deterministic indexable HMAC (hex)
function emailIndexHash(email) {
  // normalize email BEFORE hashing! e.g. lower-case and trim
  const normalized = (email || '').trim().toLowerCase();
  const hmac = crypto.createHmac('sha256', HMAC_KEY).update(normalized).digest('hex');
  // optionally include key version prefix to allow rotation
  return `${KEY_VERSION}:${hmac}`;
}

module.exports = {
  encryptEmail,
  decryptEmail,
  emailIndexHash,
};
