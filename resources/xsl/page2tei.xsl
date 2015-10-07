<xsl:stylesheet 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:ns="http://www.namescape.nl/"
   xmlns:alto="http://schema.ccs-gmbh.com/ALTO"
   xmlns:page="http://schema.primaresearch.org/PAGE/gts/pagecontent/2010-03-19"
   xmlns:tei="http://www.tei-c.org/ns/1.0"
   exclude-result-prefixes="tei"
   xpath-default-namespace="http://www.tei-c.org/ns/1.0"
   xmlns="http://www.tei-c.org/ns/1.0"
   version="2.0">

<xsl:template match="/">
<TEI>
<teiHeader>
<fileDesc/>
<sourceDesc/>
</teiHeader>
<text>
<body>
<div>
<xsl:apply-templates select=".//page:TextRegion"/>
</div>
</body>
</text>
</TEI>
</xsl:template>

<!--

"drop-capital">
"heading">
"paragraph">
"signature-mark">

-->

<xsl:template match="page:TextRegion">
<xsl:variable name="element">
<xsl:choose>
<xsl:when test="./@type='heading'">head</xsl:when>
<xsl:when test="./@type='paragraph'">p</xsl:when>
<!--
<xsl:when test="./@type='signature-mark'">ab</xsl:when>
<xsl:when test="./@type='drop-capital'">ab</xsl:when>
-->
<xsl:otherwise>ab</xsl:otherwise>
</xsl:choose>
</xsl:variable>
<xsl:element name="{$element}">
<xsl:if test="$element = 'ab'">
<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
</xsl:if>
<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
<xsl:apply-templates select=".//page:TextEquiv/page:Unicode"/>
</xsl:element>
</xsl:template>

<xsl:template match="page:TextLine">
<xsl:apply-templates/> <lb/>
</xsl:template>


<xsl:template match="page:Word">
<w><xsl:attribute name="xml:id"><xsl:value-of select="@ID"/></xsl:attribute> 
<!-- <xsl:text> </xsl:text> -->
<xsl:value-of select="@CONTENT"/>
<!-- <xsl:text> </xsl:text> -->
</w>
</xsl:template>

</xsl:stylesheet>
