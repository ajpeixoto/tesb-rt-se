#!/bin/bash
shopt -s xpg_echo

dstDir=$1
passphrase=$2
maven_settings=$3
getArt=$4
scrDir=`dirname $0`

. ${scrDir}/env.sh
. ${scrDir}/func_lib.sh

echo "Preparing release artifacts at ${dstDir} ..."

repUrl="https://oss.sonatype.org/service/local/staging/deploy/maven2/"

repId="sonatype-nexus-staging"
deployPrefix="execCommand mvn -B -s $maven_settings gpg:sign-and-deploy-file -Durl=${repUrl} -DrepositoryId=${repId} -Dgpg.passphrase=${passphrase}"

getPrefix="execSilent mvn -B -s $maven_settings org.apache.maven.plugins:maven-dependency-plugin:2.8:get -Dartifact="

deployScript="${dstDir}/deploy.sh"
artListFile="${dstDir}/artifacts.list"
shortLogFN="${dstDir}/short.log"
longLogFN="${dstDir}/long.log"

rm -rf "${dstDir}"
mkdir -p "${dstDir}"
echo "#!/bin/sh" > ${deployScript}
echo ". ${scrDir}/func_lib.sh" >> ${deployScript}
echo "repoDir=${dstDir}" >> ${deployScript}
echo "deployCmd=\"${deployPrefix}\"" >> ${deployScript}

echo "initLogs" >> ${deployScript}
chmod +x ${deployScript}


totalModules=0
totalErrors=0
totalArtifacts=0

errorPrint () {
	echo "   ERROR: $*"
	totalErrors=`expr $totalErrors + 1`
}

execSilent () {
	cmdLine=$*
	echo "Executing command: $cmdLine ..."
	`$cmdLine`
	cmdRC=$?
	if [ "$cmdRC" = "0" ]; then
		echo "SUCCESS"
	else
		echo "FAILED"
	fi
}

addOptional () {
	local cl=$1
	optPath="$localMavenRepoDir/$relArt-$cl.jar"
	artcl="$artIfo:$cl"
	if [ "$getArt" = "true" -a ! -f "$optPath" ]; then
		${getPrefix}${artcl}
	fi
	if [ -f "$optPath" ]; then
		echo "$artcl" >> "${artListFile}"
		cp "$optPath" "$dstPath"
		echo "   Adding optional classifier: $cl"
		cmdLine="${cmdLine} -D$cl=\${repoDir}/$relPath/$artId-$artVer-$cl.jar"
		totalArtifacts=`expr $totalArtifacts + 1`
	fi
}

getDeployFromModuleArtifactsList () {
	local artList="$1"
	local dstDir="$2"
	local cmdLine=""
	Dfiles=""
	Dclassifiers=""
	Dtypes=""
	for artIfo in $artList; do
		parseArtifactInfo $artIfo
		echo "   Copying artifact $groupId-$artId-$artVer-$artType-$artClass..."
		relPath="$groupDir/$artId/$artVer"
		relArt="$relPath/$artId-$artVer"
		dstPath="$dstDir/$relPath/"
		mkdir -p "$dstPath"
		if [ "$cmdLine" = "" ]; then
			cmdLine='${deployCmd}'
			pomPath="$localMavenRepoDir/$relArt.pom"
			if [ "$artType" != "pom" ]; then
				artpom="${groupId}:${artId}:${artVer}:pom"
				echo "${artpom}" >> "${artListFile}"
				if [ "$getArt" = "true" -a ! -f "$pomPath" ]; then
					${getPrefix}${artpom}
				fi
				if [ -f "$pomPath" ]; then
					cp "$pomPath" "$dstPath"
					totalArtifacts=`expr $totalArtifacts + 1`
				else
					errorPrint "pom file not found: $pomPath"
				fi
			fi
			cmdLine="${cmdLine} -DpomFile=\${repoDir}/$relPath/$artId-$artVer.pom"
			addOptional "sources"
			addOptional "javadoc"
			cmdLine="${cmdLine} -Dfile=\${repoDir}/$relPath/$artId-$artVer.$artType -Dpackaging=$artType"
		else
			if [ "$artClass" != "" ]; then
				relArt="$relArt-$artClass"
			fi
			if [ "$Dfiles" != "" ]; then
				Dfiles="$Dfiles,"
				Dclassifiers="$Dclassifiers,"
				Dtypes="$Dtypes,"
			fi
			Dfiles="$Dfiles\${repoDir}/$relArt.$artType"
			Dclassifiers="$Dclassifiers$artClass"
			Dtypes="$Dtypes$artType"
		fi
		relArt="$relArt.$artType"
		srcFile="$localMavenRepoDir/$relArt"
		#echo "   Copying additional artifact $relArt..."
		if [ "$getArt" = "true" -a ! -f "$srcFile" ]; then
			${getPrefix}${artIfo}
		fi
		if [ -f "$srcFile" ]; then
			cp "$srcFile" "$dstPath"
			echo "$artIfo" >> "${artListFile}"
			totalArtifacts=`expr $totalArtifacts + 1`
		else
			errorPrint "artifact file not found: $srcFile"
		fi
	done
	if [ "$Dfiles" != "" ]; then
		cmdLine="${cmdLine} -Dfiles=$Dfiles -Dclassifiers=$Dclassifiers -Dtypes=$Dtypes"
	fi
	echo "$cmdLine" >> ${deployScript}
}

prepareMavenReleaseArtifacts () {
	local src=$1
	local dst=$2
	local modules
	local module
	local arts

	if [ -f "$src/pom.xml" ]; then
		echo "Parsing $src/pom.xml..."
		totalModules=`expr $totalModules + 1`
		arts=`getArtifacsListFromPom "$src/pom.xml"`
		getDeployFromModuleArtifactsList "$arts" "$dst"
		modules=`getModulesFromPom "$src/pom.xml"`
		for module in $modules; do
			if [ -d "$src/$module" ]; then
				if [ "$module" != "examples" ]; then
					prepareMavenReleaseArtifacts "$src/$module" "$dst"
				fi
			else
				errorPrint "module dir not found: $src/$module"
			fi
		done
	else
		errorPrint "pom file not found: $src/pom.xml"
	fi
}

rootPomVer=`getVersionFromPom "${srcDirSE}/pom.xml"`
prepareMavenReleaseArtifacts "${srcDirSE}" "${dstDir}"
echo "finishLogs" >> ${deployScript}
echo "==    TOTAL MODULES: $totalModules"
echo "==  TOTAL ARTIFACTS: $totalArtifacts"
echo "==     TOTAL ERRORS: $totalErrors"
