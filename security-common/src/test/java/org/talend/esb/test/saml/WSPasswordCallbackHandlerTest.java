package org.talend.esb.test.saml;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;

import org.junit.Ignore;
import org.junit.Test;
import org.talend.esb.security.saml.WSPasswordCallbackHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class WSPasswordCallbackHandlerTest {

	private static final String ALGORITHM = "PBEWITHSHA256AND256BITAES-CBC-BC";
	private static final String PASSWORD_ENV_NAME = "TESB_ENV_PASSWORD";
	private static final String PROVIDER_NAME = "BC";

	@Test
	public void handleNotEncryptedPasswordTest() throws Exception {

		String username = "username";
		String password = "password";

		WSPasswordCallbackHandler h = new WSPasswordCallbackHandler(username, password);

		Callback c = new WSPasswordCallback(username, 0);

		List<Callback> cs = new ArrayList<Callback>();
		cs.add(c);

		h.handle(cs.toArray(new Callback[0]));

	}

	@Test
	public void handleNoUserTest() throws Exception {

		String username = null;
		String password = "password";

		WSPasswordCallbackHandler h = new WSPasswordCallbackHandler(username, password);

		Callback c = new WSPasswordCallback(username, 0);

		List<Callback> cs = new ArrayList<Callback>();
		cs.add(c);

		h.handle(cs.toArray(new Callback[0]));

	}

	@Test
	public void handleEncryptedPasswordTest() throws Exception {
		Map<String, String> newEnv = new HashMap<>();
		newEnv.put(PASSWORD_ENV_NAME, "pwd");
		setEnv(newEnv);
		
		String username = "username";
		String password = PropertyValueEncryptionUtils.encrypt("password", getEncryptor());

		WSPasswordCallbackHandler h = new WSPasswordCallbackHandler(username, password);

		Callback c = new WSPasswordCallback(username, 0);

		List<Callback> cs = new ArrayList<Callback>();
		cs.add(c);

		h.handle(cs.toArray(new Callback[0]));

	}

	private StandardPBEStringEncryptor getEncryptor() throws Exception {

		StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
		EnvironmentStringPBEConfig env = new EnvironmentStringPBEConfig();
		env.setProvider(new BouncyCastleProvider());
		env.setProviderName(PROVIDER_NAME);
		env.setAlgorithm(ALGORITHM);
		env.setPassword("pwd");
		enc.setConfig(env);

		return enc;
	}
	
	@SuppressWarnings({"unchecked"})
	protected static void setEnv(Map<String, String> newenv) throws Exception {
		try {
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
			env.putAll(newenv);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField(
					"theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.putAll(newenv);
		} catch (NoSuchFieldException e) {
			Class[] classes = Collections.class.getDeclaredClasses();
			Map<String, String> env = System.getenv();
			for (Class cl : classes) {
				if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
					Field field = cl.getDeclaredField("m");
					field.setAccessible(true);
					Object obj = field.get(env);
					Map<String, String> map = (Map<String, String>) obj;
					map.clear();
					map.putAll(newenv);
				}
			}
		}
	}

}
