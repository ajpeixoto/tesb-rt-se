package org.talend.esb.encryptor;

import java.lang.reflect.Field;
import java.security.Security;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.registry.AlgorithmRegistry;
import org.junit.Assert;
import org.junit.Test;

public class TextEncryptorTest {

    @Test
    public void testEncryption_default() throws Exception {
        TextEncryptor encryptor = new TextEncryptor();
        encryptor.textToEncrypt = "textToEncrypt";
        encryptor.encryptionPassword = "encryptionPassword";
        encryptor.execute();
    }
	
	@Test
	public void testEncryption_env() throws Exception {
		Map<String,String> newEnv = new HashMap<>();
		newEnv.put("TESB_ENV_ALGORITHM", "PBEWITHSHA256AND256BITAES-CBC-BC");
		newEnv.put("TESB_ENV_PASSWORD", "anotherPassword");
		setEnv(newEnv);
		TextEncryptor encryptor = new TextEncryptor();
		encryptor.textToEncrypt = "textToEncrypt";
		encryptor.encryptionPassword = "encryptionPassword";
		encryptor.execute();
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
