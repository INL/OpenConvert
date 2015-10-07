<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
   xmlns:folia="http://ilk.uvt.nl/folia" 
   xmlns:edate="http://exslt.org/dates-and-times" 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:tei="http://www.tei-c.org/ns/1.0" 
   exclude-result-prefixes="tei edate" version="1.0"  
   xmlns:pm="http://www.politicalmashup.nl"
   xmlns:ns="http://www.namescape.nl/" 
   xmlns:ns0="http://www.politicalmashup.nl"  
   xmlns:ns1="http://www.politicalmashup.nl" 
   xmlns="http://www.tei-c.org/ns/1.0" 
   xpath-default-namespace="http://www.tei-c.org/ns/1.0">

 <xsl:template name="copy-attributes">
       <xsl:for-each select="@*">
        <xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="*">
   <xsl:element name="{name(.)}">
   <xsl:call-template name="copy-attributes"/>
     <xsl:apply-templates select="*|processing-instruction()|comment()|text()"/>
   </xsl:element>
  </xsl:template>

<xsl:template match="name">
    <ns:ne>
	<xsl:apply-templates select="@*"/>
	<xsl:apply-templates/>
    </ns:ne>
</xsl:template>


<xsl:template match="*[local-name()='w' or local-name()='pc']">
	<xsl:apply-templates/>
  </xsl:template>

<!--
  <xsl:template match="s">
	<s>
		<xsl:apply-templates select="@*"/>
		<xsl:apply-templates/>
	</s>
  </xsl:template>
-->

</xsl:stylesheet>
