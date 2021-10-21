<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pom="http://maven.apache.org/POM/4.0.0">
	
	<xsl:output method="text"/>
	<xsl:template match="/">
		<xsl:for-each select="pom:project/pom:build/pom:plugins/pom:plugin[pom:artifactId='maven-install-plugin']/pom:executions/pom:execution[pom:goals/pom:goal='install-file']/pom:configuration">
			<xsl:value-of select="pom:file" />
			<xsl:text>:</xsl:text>
			<xsl:value-of select="pom:groupId" />
			<xsl:text>:</xsl:text>
			<xsl:value-of select="pom:artifactId" />
			<xsl:text>:</xsl:text>
			<xsl:value-of select="pom:version" />
			<xsl:text>:</xsl:text>
			<xsl:value-of select="pom:packaging" />
			<xsl:text>:</xsl:text>
			<xsl:value-of select="pom:classifier" />
			<xsl:text> </xsl:text>
		</xsl:for-each>
    </xsl:template>
</xsl:stylesheet>