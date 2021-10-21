#!/bin/bash

getGroupIdFromPom() {
	local pomFile=$1
	local groupId
	groupId=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getGroupIdFromPom.xsl`
	if [ "${groupId}" = "" ]; then
	   groupId=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getParentGroupIdFromPom.xsl`
	fi
	echo "$groupId"
}

getParentGroupIdFromPom() {
	local pomFile=$1
	local groupId
	groupId=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getParentGroupIdFromPom.xsl`
	if [ "${groupId}" = "" ]; then
	   groupId=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getGroupIdFromPom.xsl`
	fi
	echo "$groupId"
}

getArtifactIdFromPom() {
	local pomFile=$1
	local artId
	artId=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getArtifactIdFromPom.xsl`
	echo "$artId"
}

getParentArtifactIdFromPom() {
	local pomFile=$1
	local artId
	artId=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getParentArtifactIdFromPom.xsl`
	echo "$artId"
}

getVersionFromPom() {
	local pomFile=$1
	local pomVer
	pomVer=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getVersionFromPom.xsl`
	if [ "${pomVer}" = "" ]; then
	   pomVer=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getParentVersionFromPom.xsl`
	fi
	echo "$pomVer"
}

getParentVersionFromPom() {
	local pomFile=$1
	local pomVer
	pomVer=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getParentVersionFromPom.xsl`
	if [ "${pomVer}" = "" ]; then
	   pomVer=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getVersionFromPom.xsl`
	fi
	echo "$pomVer"
}

getPackagingFromPom() {
	local pomFile=$1
	local packaging
	packaging=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getPackagingFromPom.xsl`
	if [ "${packaging}" = "bundle" ]; then
		packaging="jar"
	fi
	echo "$packaging"
}

getAttachedArtifactsFromPom() {
	local pomFile=$1
	local attached
	attached=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getAttachedArtifactsFromPom.xsl`
	echo "$attached"
}

getArtifacsListFromPom () {
	local pomFile=$1
	local groupId
	local artId
	local pomVer
	local packaging
	local attached
	
	groupId=`getGroupIdFromPom "${pomFile}"`
	artId=`getArtifactIdFromPom "${pomFile}"`
	pomVer=`getVersionFromPom "${pomFile}"`
	containsDollar=`echo "${pomVer}" | grep '\\$'`
	if [ "$containsDollar" != "" ]; then
		>&2 echo "     ***** Found version specified by property: $pomVer"
		>&2 echo "     ***** Usin root pom version instead: $rootPomVer"
		pomVer="$rootPomVer"
	fi
	packaging=`getPackagingFromPom "${pomFile}"`
	echo "${groupId}:${artId}:${pomVer}:${packaging}"
	
	attached=`getAttachedArtifactsFromPom "${pomFile}"`
	for attArt in $attached; do
		echo "${groupId}:${artId}:${pomVer}:${attArt}"
	done
}

getParentFromPom() {
	local pomFile=$1
	local groupId
	local artId
	local pomVer

	artId=`getParentArtifactIdFromPom "${pomFile}"`
	if [ "$artId" != "" ]; then
		groupId=`getParentGroupIdFromPom "${pomFile}"`
		pomVer=`getParentVersionFromPom "${pomFile}"`
		echo "${groupId}:${artId}:${pomVer}:pom"
	fi
}

getModulesFromPom() {
	local pomFile=$1
	local modules
	modules=`java -jar ${xslDir}/xalan-command-line.jar -IN "${pomFile}" -XSL ${xslDir}/getModulesFromPom.xsl`
	echo "$modules"
}

listMavenArtifacs2File () {
	local src=$1
	local dst=$2
	local modules
	local module
	
	if [ -f "$src/pom.xml" ]; then
		echo "Parsing $src/pom.xml..."
		getArtifacsListFromPom "$src/pom.xml" >> $2
		modules=`getModulesFromPom "$src/pom.xml"`
		for module in $modules; do
			if [ -d "$src/$module" ]; then
				if [ "$module" != "examples" ]; then
					listMavenArtifacs2File "$src/$module" "$dst"
				fi
			else
				echo "ERROR: module dir not found: $src/$module" 
			fi
		done
	else
		echo "ERROR: pom file not found: $src/pom.xml"
	fi
}

copyArtifactsByList() {
	local artList=$1
	local dstDir=$2
	for artIfo in $artList; do
		parseArtifactInfo $artIfo
		echo "Copying artifact $groupId-$artId-$artVer-$artType-$artClass..."
		srcPath="$localMavenRepoDir/$groupDir/$artId/$artVer/$artId-$artVer"
		dstPath="$dstDir/$groupDir/$artId/$artVer/"
		mkdir -p "$dstPath"
		if [ "$artType" != "pom" ]; then
			pomPath="$srcPath.pom"
			if [ -f "$pomPath" ]; then
				cp "$pomPath" "$dstPath"
			else
				echo "ERROR: pom file not found: $pomPath"
			fi
		fi
		if [ "$artClass" != "" ]; then
			srcPath="$srcPath-$artClass"
		fi
		srcPath="$srcPath.$artType"
		if [ -f "$srcPath" ]; then
			cp "$srcPath" "$dstPath"
		else
			echo "ERROR: artifact file not found: $srcPath"
		fi
	done
}

parseArtifactInfo() {
	artInfo=$1
	#artInfo="org.talend.esb.sam.service:sam-service-rest:5.4.0-SNAPSHOT:properties:clientKeystore"
	groupId=`echo $artInfo | sed 's/^\([^:]*\):.*$/\1/'`
	if [ "$groupId" != "$artInfo" ]; then
		artInfo=`echo $artInfo | sed "s/^${groupId}://"`
		artId=`echo $artInfo | sed 's/^\([^:]*\):.*$/\1/'`
		if [ "$artId" != "$artInfo" ]; then
			artInfo=`echo $artInfo | sed "s/^${artId}://"`
			artVer=`echo $artInfo | sed 's/^\([^:]*\):.*$/\1/'`
			if [ "$artVer" != "$artInfo" ]; then
				artInfo=`echo $artInfo | sed "s/^${artVer}://"`
				artType=`echo $artInfo | sed 's/^\([^:]*\):.*$/\1/'`
				if [ "$artType" != "$artInfo" ]; then
					artClass=`echo $artInfo | sed "s/^${artType}://"`
				else
					artClass=""
				fi
			else
				artType=""
				artClass=""
			fi
		else
			artVer=""
			artType=""
			artClass=""
		fi
	else
		artId=""
		artVer=""
		artType=""
		artClass=""
	fi
	groupDir=`echo $groupId | sed 's/\./\//g'`
}

initLogs () {
	_scriptDir="`dirname \"$0\"`"
	if [ "$shortLogFN" = "" ]; then
		shortLogFN="$_scriptDir/short.log"
	fi
	echo " ==== Log started $(date)" > $shortLogFN
	if [ "$longLogFN" = "" ]; then
		longLogFN="$_scriptDir/long.log"
	fi
	echo " ==== Log started $(date)" > $longLogFN
	_errorCount=0
}

finishLogs () {
	echo " ==== Log finished $(date)" >> $shortLogFN
	echo " ==== Log finished $(date)" >> $longLogFN
}

execCommand () {
	cmdLine=$*
	echo "Executing command: $cmdLine ..."
	cmdOut=`$cmdLine`
	cmdRC=$?
	if [ "$cmdRC" = "0" ]; then
		echo "SUCCESS"
		echo " ==SUCCESS: $cmdLine" >> $shortLogFN
		echo " ==SUCCESS: $cmdLine" >> $longLogFN
		echo "$cmdOut" >> $longLogFN
	else
		_errorCount=`expr $_errorCount + 1`
		echo "FAILED with output:"
		echo "$cmdOut"
		echo " == FAILED: $cmdLine" >> $shortLogFN
		echo "$cmdOut" >> $shortLogFN
		echo " == FAILED: $cmdLine" >> $longLogFN
		echo "$cmdOut" >> $longLogFN
	fi
}

