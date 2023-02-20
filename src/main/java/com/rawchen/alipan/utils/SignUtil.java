package com.rawchen.alipan.utils;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * 加/解密工具类
 *
 * @author RawChen
 * @date 2023-02-20
 */
public class SignUtil {

	public static List<String> sign(String appId, String deviceId, String userId, String nonce) {
		BigInteger privateKeyInt = new BigInteger(256, new SecureRandom());
		BigInteger publicKeyInt = Sign.publicKeyFromPrivate(privateKeyInt);
		String privateKey = privateKeyInt.toString(16);
		String publicKey = publicKeyInt.toString(16);
		byte[] dataBytes = (appId + ":" + deviceId + ":" + userId + ":" + nonce).getBytes(StandardCharsets.UTF_8);
		byte[] dataHash = Hash.sha256(dataBytes);
		ECKeyPair keyPair = new ECKeyPair(new BigInteger(privateKey, 16), new BigInteger(publicKey, 16));
		Sign.SignatureData signatureInfo = Sign.signMessage(dataHash, keyPair, false);
		String signature = Hex.toHexString(signatureInfo.getR()) + Hex.toHexString(signatureInfo.getS()) + "01";
		List<String> result = new ArrayList<>();
		result.add("04" + publicKey);
		result.add(privateKey);
		result.add(signature);
		return result;
	}
}
