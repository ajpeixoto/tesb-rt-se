commonScriptsDir="${WORKSPACE}/tmp_deploy_maven_central/commonScripts"
xslDir="$commonScriptsDir/xsl"
localMavenRepoDir="/root/.m2/repository"

srcDirSE="${WORKSPACE}/workspaces/tesb-rt-se"
srcDirRegistry="${WORKSPACE}/workspaces/tesb-registry"
srcDirAuthz="${WORKSPACE}/workspaces/tesb-authorization"
srcDirEL="${WORKSPACE}/workspaces/tesb-eventlogging"
srcDirEE="${WORKSPACE}/workspaces/tesb-rt-ee"
srcDirProv="${WORKSPACE}/workspaces/tesb-provisioning"

mavenRepositories="http://central.maven.org/maven2,\
http://sop-ip14.talend.lan:8081/nexus/content/repositories/thirdparty-dev,\
http://repository.apache.org/snapshots,\
http://repository.springsource.com/maven/bundles/release,\
http://repository.springsource.com/maven/bundles/external,\
http://oss.sonatype.org/content/repositories/releases,\
https://repository.apache.org/content/groups/staging,\
https://oss.sonatype.org/content/groups/public\
"
depsGroupIds="org.apache.activemq,org.apache.camel,org.apache.cxf,org.apache.karaf,org.apache.qpid"
mkdir -p ${xslDir}