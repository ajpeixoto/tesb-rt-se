<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pom="http://maven.apache.org/POM/4.0.0">
	
	<xsl:output method="text"/>
	<xsl:template match="/">
		<xsl:value-of select="pom:project/pom:artifactId" />
    </xsl:template>
</xsl:stylesheet>