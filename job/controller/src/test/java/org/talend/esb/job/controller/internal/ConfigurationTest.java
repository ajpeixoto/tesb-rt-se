/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.job.controller.internal;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Test;
import org.osgi.service.cm.ConfigurationException;

import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ConfigurationTest {

    public static final String KEY_1 = "key1";

    public static final String KEY_2 = "key2";

    public static final String KEY_3 = "key3";

    public static final String VALUE_1 = "value";

    public static final String VALUE_2 = "value2";

    public static final String VALUE_3 = "value3";

    public static final String CONTEXT_KEY = "context";

    public static final String CONTEXT_VALLUE = "contextValue";

    @Test
    public void noPropertiesSetProvidesEmptyArgumentList() throws Exception {

        Configuration configuration = new Configuration();
        configuration.setTimeout(0);

        String[] args = configuration.awaitArguments();
        assertArrayEquals(new String[0], args);
    }

    @Test
    public void nonStringPropertyValueResultsInConfigurationException() {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(KEY_1, 1);

        try {
            new Configuration(properties);
            fail("A ConfigurationException should have been thrown.");
        } catch (ConfigurationException e) {
        }
    }

    @Test
    public void configurationPropertysResultsInContextArgument() throws Exception {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(CONTEXT_KEY, CONTEXT_VALLUE);

        Configuration configuration = new Configuration(properties);

        String[] args = configuration.awaitArguments();
        assertArrayEquals(new String[]{"--context=" + CONTEXT_VALLUE}, args);
    }

    @Test
    public void setPropertiesAfterCreationHasSameEffectAsSettingDuringCreation() throws Exception {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(CONTEXT_KEY, CONTEXT_VALLUE);

        Configuration configuration = new Configuration();
        configuration.setProperties(properties);

        String[] args = configuration.awaitArguments();
        assertArrayEquals(new String[]{"--context=" + CONTEXT_VALLUE}, args);
    }

    @Test
    public void setPropertiesOverridesPropertiesSetBefore() throws Exception {
        Dictionary<String, String> properties1 = new Hashtable<String, String>();
        properties1.put(CONTEXT_KEY, CONTEXT_VALLUE);

        Dictionary<String, String> properties2 = new Hashtable<String, String>();
        properties2.put(KEY_1, VALUE_1);

        Configuration configuration = new Configuration(properties1);
        configuration.setProperties(properties2);

        String[] expectedArgs = new String[]{"--context_param=" + KEY_1 + "=" + VALUE_1};
        String[] args = configuration.awaitArguments();
        assertArrayEquals(expectedArgs, args);
    }

    @Test
    public void setNullPropertiesReturnsEmptyArgumentList() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setProperties(null);

        String[] args = configuration.awaitArguments();
        assertArrayEquals(new String[0], args);
    }

    @Test
    public void nonSpecialPropertyResultsInContextParamArgument() throws Exception {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(KEY_1, VALUE_1);
        properties.put(KEY_2, VALUE_2);

        Configuration configuration = new Configuration(properties);

        String[] args = configuration.awaitArguments();
        String[] expectedArgs = new String[]{"--context_param=" + KEY_1 + "=" + VALUE_1,
                "--context_param=" + KEY_2 + "=" + VALUE_2};
        assertThat(args, arrayContainingInAnyOrder(expectedArgs));
    }

    @Test
    public void propertyInFilterNotInArgumentList() throws Exception {
        String[] filter = new String[]{KEY_1};
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(KEY_1, "value1");
        properties.put(KEY_2, "value2");

        Configuration configuration = new Configuration(properties, filter);

        String[] args = configuration.awaitArguments();
        String[] expectedArgs = new String[]{"--context_param=" + KEY_2 + "=" + VALUE_2};
        assertThat(args, arrayContainingInAnyOrder(expectedArgs));
    }

    @Test
    public void decrypt_password_configured() throws Exception {
        String encryptedValue = "ENC(RSrmiMKf8H64z3+2ZxVbs0PHZHgfg6Up8LpadsegQio=)";
        String clearValue = "textToEncrypt";
        String encryptionPassword = "encryptionPassword";

        decrypt_password(encryptedValue, clearValue, encryptionPassword, null);
        decrypt_password(encryptedValue, clearValue, encryptionPassword, "PBEWITHSHA256AND256BITAES-CBC-BC");

    }

    private void decrypt_password(String encryptedValue, String clearValue, String encryptionPassword,
            String algorithm) throws Exception {
        String[] filter = new String[]{};
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(KEY_1, encryptedValue);

        Map<String, String> newEnv = new HashMap<>();
        if (algorithm != null) {
            newEnv.put("TESB_ENV_ALGORITHM", algorithm);
        }
        newEnv.put("TESB_ENV_PASSWORD", encryptionPassword);
        setEnv(newEnv);
        Configuration configuration = new Configuration(properties, filter);
        String[] args = configuration.awaitArguments();
        String[] expectedArgs = new String[]{"--context_param=" + KEY_1 + "=" + clearValue};
        assertThat(args, arrayContainingInAnyOrder(expectedArgs));
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
