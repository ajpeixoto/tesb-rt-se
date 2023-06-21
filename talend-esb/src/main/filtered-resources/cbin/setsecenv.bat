@echo off
rem
rem    Licensed to the Apache Software Foundation (ASF) under one or more
rem    contributor license agreements.  See the NOTICE file distributed with
rem    this work for additional information regarding copyright ownership.
rem    The ASF licenses this file to You under the Apache License, Version 2.0
rem    (the "License"); you may not use this file except in compliance with
rem    the License.  You may obtain a copy of the License at
rem
rem       http://www.apache.org/licenses/LICENSE-2.0
rem
rem    Unless required by applicable law or agreed to in writing, software
rem    distributed under the License is distributed on an "AS IS" BASIS,
rem    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem    See the License for the specific language governing permissions and
rem    limitations under the License.
rem

rem
rem User names and passwords used in the configurations in the "etc" directory.
rem It is recommended to replace the default passwords as soon as the Talend
rem ESB runtime installation is not just used for testing.
rem Furthermore, it is recommended not to keep the passwords as cleartext
rem passwords in this script but retrieve them from the credential vault.
rem

set TESB_AMQ_GUI_USER_NAME=${org.talend.script.ctesb}
set TESB_AUXSTORE_AUTHENTICATION_USERNAME=${org.talend.script.ckar}
set TESB_AUXSTORE_SAML_USERNAME=${org.talend.script.ctad}
set TESB_LOCATOR_AUTHENTICATION_USERNAME=${org.talend.script.ctesb}
set TESB_REGISTRY_AUTHENTICATION_USERNAME=${org.talend.script.ckar}
set TESB_REGISTRY_SAML_USERNAME=${org.talend.script.ctad}
set TESB_SAML_SECURITY_USERNAME=${org.talend.script.ctad}
set TESB_SAM_SERVICE_SECURITY_USERNAME=${org.talend.script.ctesb}
set TESB_TIDM_USERNAME=${org.talend.script.cadm}

set TESB_AMQ_GUI_USER_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.ctesb}
set TESB_AUXSTORE_AUTHENTICATION_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.ckar}
set TESB_AUXSTORE_SAML_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.ctad}
set TESB_LOCATOR_AUTHENTICATION_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.ctesb}
set TESB_REGISTRY_AUTHENTICATION_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.ckar}
set TESB_REGISTRY_SAML_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.ctad}
set TESB_SAML_SECURITY_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.ctad}
set TESB_SAM_SERVICE_SECURITY_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.ctesb}
set TESB_TIDM_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.cps}${org.talend.script.cwd}

set TESB_TLS_KEYSTORE_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.cps}${org.talend.script.cwd}
set TESB_TLS_KEY_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.cps}${org.talend.script.cwd}
set TESB_STS_KEYSTORE_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=st${org.talend.script.csrvs}${org.talend.script.cps}
set TESB_CLIENT_KEYSTORE_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.cclis}${org.talend.script.cps}
set TESB_CLIENT_KEY_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.cclik}${org.talend.script.cps}
set TESB_SERVICE_KEYSTORE_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.csrvs}${org.talend.script.cps}
set TESB_SERVICE_KEY_${org.talend.script.cprefix}${org.talend.script.cinfix}${org.talend.script.cpostfix}=${org.talend.script.csrvk}${org.talend.script.cps}
