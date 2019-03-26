var RSAEncrypt = {
	encrypt : function(key, data) {
		var encrypt = new JSEncrypt();
		encrypt.setPublicKey(key);
		var result = "";
		var array = data.split("");
		while (array.length > 117) {
			var _tmp = array.splice(0, 117);
			_tmp = encrypt.encrypt(_tmp.join(""));
			result += _tmp;
		}
		result += encrypt.encrypt(array.join(""));
		return result;
	}
}
