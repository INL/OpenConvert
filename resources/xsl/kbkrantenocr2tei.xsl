<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:tei="http://www.tei-c.org/ns/1.0" 
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcx="http://krait.kb.nl/coop/tel/handbook/telterms.html"
   exclude-result-prefixes="tei" version="1.0">

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

<xsl:template match="article">
<TEI xmlns="http://www.tei-c.org/ns/1.0">
<teiHeader>
<fileDesc>
<titleStmt><title/></titleStmt>
<publicationStmt><p/></publicationStmt>
</fileDesc>
<sourceDesc>
<listBibl xml:id="inlMetadata" id="inlMetadata">
<bibl>

<interpGrp type="pid">
<interp><xsl:attribute name="value"><xsl:value-of select="./header/article//dcx:recordIdentifier"/></xsl:attribute></interp>
</interpGrp>


<interpGrp type="titleLevel1">
<interp><xsl:attribute name="value"><xsl:value-of select="./header/article//dc:title"/></xsl:attribute></interp>
</interpGrp>

<interpGrp type="titleLevel2">
<interp><xsl:attribute name="value"><xsl:value-of select="./header/issue//dc:title"/></xsl:attribute></interp>
</interpGrp>

<interpGrp type="language">
<interp><xsl:attribute name="value"><xsl:value-of select="./header/issue//dc:language"/></xsl:attribute></interp>
</interpGrp>
<!-- jaren etc uit templates van Pieter/Bart halen -->

<xsl:for-each select="tokenize(./header/issue//dc:date/text(),'-')">
<xsl:if test="position()=1">
  <interpGrp type="pubYear_from"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="witnessYear_from"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="pubYear_to"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="witnessYear_to"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
</xsl:if>
<xsl:if test="position()=2">
  <interpGrp type="pubMonth_from"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="witnessMonth_from"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="pubMonth_to"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="witnessMonth_to"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
</xsl:if>
<xsl:if test="position()=3">
  <interpGrp type="pubDay_from"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="witnessDay_from"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="pubDay_to"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
  <interpGrp type="witnessDay_to"><interp><xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute></interp></interpGrp>
</xsl:if>
</xsl:for-each>
</bibl>
</listBibl>
</sourceDesc>
</teiHeader>
<text>
<body>
<div>
    <xsl:apply-templates select="./text/*"/>
</div>
</body>
</text>
</TEI>
</xsl:template>

<xsl:template match="title">
<head><xsl:apply-templates/></head>
</xsl:template>
</xsl:stylesheet>
