<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
   xmlns:cmd="http://www.clarin.eu/cmd/"
   xmlns:folia="http://ilk.uvt.nl/folia" 
   xmlns:edate="http://exslt.org/dates-and-times" 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:tei="http://www.tei-c.org/ns/1.0" 
   exclude-result-prefixes="tei edate" version="1.0"  
   xmlns:pm="http://www.politicalmashup.nl"
   xmlns:ns="http://www.namescape.nl/" 
   xmlns:ns0="http://www.politicalmashup.nl"  
   xmlns:ns1="http://www.politicalmashup.nl" 
   xmlns="http://ilk.uvt.nl/folia"
   xpath-default-namespace="http://www.tei-c.org/ns/1.0">

  <xsl:output method="xml" indent="yes"/>
<!--
  <xsl:strip-space elements="*"/>
-->
 <xsl:template name="copy-attributes">
       <xsl:for-each select="@*">
        <xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
    </xsl:for-each>
  </xsl:template>

<xsl:param name='generateIds'>true</xsl:param>

<xsl:template name="setId">
<xsl:if test="@xml:id or $generateIds='true'">
<xsl:attribute name="xml:id">
<xsl:choose>
<xsl:when test="@ID"><xsl:value-of select="@xml:id"/></xsl:when>
<xsl:otherwise>e<xsl:number level="any" count="*"/></xsl:otherwise>
</xsl:choose>
</xsl:attribute>
</xsl:if>
</xsl:template>

<xsl:template match="div|head|p|table|row|cell">
<xsl:element name="{name(.)}"><xsl:attribute name="xml:id"><xsl:value-of select="generate-id(.)"/></xsl:attribute><xsl:apply-templates/></xsl:element>
</xsl:template>

<xsl:template match="div0|div1|div2|div3|div4|div5|div6|div7">
<xsl:element name="div"><xsl:apply-templates/></xsl:element>
</xsl:template>

<!-- ToDo named entity standoff annotation-->
<!-- ToDo: untokenized text: not possible in folia (comment by proycon: yes it is, just associate <t> elements with higher-level structural elements) -->

<xsl:template match="w|pc">
<w>
<xsl:attribute name="xml:id">
<xsl:value-of select="@xml:id"/>
</xsl:attribute>
<t><xsl:apply-templates/></t>
<xsl:if test="@lemma">
<lemma><xsl:attribute name="class"><xsl:value-of select="@lemma"/></xsl:attribute></lemma>
</xsl:if>
<xsl:if test="@type">
<pos><xsl:attribute name="class"><xsl:value-of select="@type"/></xsl:attribute></pos>
</xsl:if>
</w>
</xsl:template>

<xsl:template match="s">
        <s>
               <xsl:call-template name="setId"/>
               <xsl:apply-templates/>
        </s>
    <xsl:if test="./name">
       <folia:entities>
        <xsl:for-each select="./name">
          <folia:entity>
                <xsl:attribute name="class"><xsl:value-of select="@type"/></xsl:attribute>
                <folia:feat subset='normalizedForm'>
                <xsl:attribute name='class'><xsl:for-each select="./w"><xsl:value-of select="./text()"/><xsl:if test="position() &lt; last()"><xsl:text> </xsl:text></xsl:if></xsl:for-each></xsl:attribute>
                </folia:feat>
                <xsl:for-each select="./w">
                <folia:wref>
                        <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
                        <xsl:attribute name="t"><xsl:value-of select="./text()"/></xsl:attribute>
                </folia:wref>
                </xsl:for-each>
          </folia:entity>
        </xsl:for-each>
        </folia:entities>
     </xsl:if>
  </xsl:template>


<xsl:template match="TEI|TEI.2">
<FoLiA xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://ilk.uvt.nl/folia" 
  version="0.8.0" generator="tei2folia.xsl">
  <xsl:attribute name="xml:id"><xsl:value-of select="generate-id(.)"/></xsl:attribute>
  <metadata>
    <annotations> <!-- dit kan je allemaal niet weten, niet uit TEI metadata te halen -->
      <xsl:if test="//w"> <token-annotation annotatortype="auto"/></xsl:if>
      <xsl:if test="//w/@type"><pos-annotation annotatortype="auto" set="unknown"/></xsl:if>
      <xsl:if test="//w/@lemma">
      <lemma-annotation annotatortype="auto" set="unknown"/></xsl:if>
      <xsl:if test="//name"><entity-annotation annotatortype="auto" set="unknown"/></xsl:if>
    </annotations>
  </metadata>
  <text>
    <xsl:call-template name="setId"/>
    <xsl:apply-templates select="//text/*"/>
  </text>
</FoLiA>
</xsl:template>
</xsl:stylesheet>
