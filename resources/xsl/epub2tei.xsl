<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:opf="http://www.idpf.org/2007/opf"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
		xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:urn="urn:oasis:names:tc:opendocument:xmlns:container"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns="http://www.tei-c.org/ns/1.0"
                xpath-default-namespace="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="dc fn opf urn xhtml"
                version="2.0">

  <xsl:output media-type="xml" indent="yes"/>

  <xsl:param name="unzipTo">not set</xsl:param>

  <xsl:template match="/">
    <xsl:message>unzipTo: <xsl:value-of select="$unzipTo"/></xsl:message>
    <xsl:apply-templates select="document(concat(concat($unzipTo,'/'), /urn:container/urn:rootfiles/urn:rootfile/@full-path), .)/opf:package"/>
  </xsl:template>

<!-- dit gaat niet werken -->

  <xsl:variable name="css"><xsl:value-of select="//style"/></xsl:variable>

  <xsl:template match="opf:package">
    <xsl:element name="TEI">
      <xsl:apply-templates select="opf:metadata"/>

      <xsl:element name="text">
        <!-- maybe use /package/metadata/language here? -->
        <xsl:attribute name="xml:lang">nl</xsl:attribute>
        <xsl:element name="body">
          <xsl:for-each select="opf:spine/opf:itemref">
            <xsl:apply-templates select="document(//opf:item[@id = current()/@idref]/@href)//xhtml:html"/>
          </xsl:for-each>
        </xsl:element>
      </xsl:element>

    </xsl:element>
  </xsl:template>

  <xsl:template match="opf:metadata">
    <teiHeader xml:lang="en">
      <fileDesc>
        <titleStmt>
          <title><xsl:value-of select="dc:title"/></title>
          <author><xsl:value-of select="dc:creator"/></author>
        </titleStmt>
        <publicationStmt>
          <p><date><xsl:value-of select="dc:date"/></date></p>
        </publicationStmt>
        <sourceDesc>

          <listBibl xml:id="inlMetadata">
            <bibl>
              <interpGrp type="title"><interp><xsl:value-of select="dc:title"/></interp></interpGrp>
              <interpGrp type="isbn"><interp><xsl:value-of select="fn:replace( ( dc:identifier[ matches(.,
                                                                                                       '^(isbn\s*)?\d([\s.-]?\d){8}(([\s.-]?\d){3})?([\s.-]?[\dx])$',
                                                                                                       'i') ] )[1],
                                                                               '[^0-9xX]+',
                                                                               '' )"/></interp></interpGrp>
              <interpGrp type="pubyear"><interp><xsl:value-of select="dc:date"/></interp></interpGrp>
              <interpGrp type="author"><interp><xsl:value-of select="dc:creator"/></interp></interpGrp>
              <interpGrp type="publisher"><interp><xsl:value-of select="dc:publisher"/></interp></interpGrp>
            </bibl>
          </listBibl>

        </sourceDesc>
      </fileDesc>
    </teiHeader>
  </xsl:template>

<xsl:template match="head"/>

<xsl:template match="html">
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="body">
<xsl:apply-templates/>
<!--
        <xsl:call-template name="level1">
            <xsl:with-param name="nodes" select="*|text()"/>
            <xsl:with-param name="level" select="1"/>
        </xsl:call-template>
-->
</xsl:template>


<xsl:template match="div">
<div>
<xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="p[following-sibling::h1]">
    <xsl:choose>
        <xsl:when test="normalize-space(.) = ''"></xsl:when>
        <xsl:otherwise>
            <head type='pre_h1'> <xsl:apply-templates/></head>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="level1">
    <xsl:param name="nodes" as="element()*"/>
    <xsl:param name="level" as="xs:integer"/>
    
    <xsl:choose>
        <xsl:when test="$nodes/self::h1">
            <xsl:for-each-group select="$nodes" group-starting-with="h1">
                <xsl:element name="{concat('div', $level)}">
                    <xsl:call-template name="level2">
                        <xsl:with-param name="nodes" select="current-group()"/>
                        <xsl:with-param name="level" select="$level + 1"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:for-each-group>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="level2">
                <xsl:with-param name="nodes" select="$nodes"/>
                <xsl:with-param name="level" select="$level"/>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="level2">
    <xsl:param name="nodes" as="element()*"/>
    <xsl:param name="level" as="xs:integer"/>
    
    <xsl:choose>
        <xsl:when test="$nodes/self::h2">
            <xsl:for-each-group select="$nodes" group-starting-with="h2">
                <xsl:element name="{concat('div', $level)}">
                    <xsl:call-template name="level3">
                        <xsl:with-param name="nodes" select="current-group()"/>
                        <xsl:with-param name="level" select="$level + 1"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:for-each-group>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="level3">
                <xsl:with-param name="nodes" select="$nodes"/>
                <xsl:with-param name="level" select="$level"/>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="level3">
    <xsl:param name="nodes" as="element()*"/>
    <xsl:param name="level" as="xs:integer"/>
    
    <xsl:choose>
        <xsl:when test="$nodes/self::h3">
            <xsl:for-each-group select="$nodes" group-starting-with="h3">
                <xsl:element name="{concat('div', $level)}">
                    <xsl:call-template name="level4">
                        <xsl:with-param name="nodes" select="current-group()"/>
                        <xsl:with-param name="level" select="$level + 1"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:for-each-group>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="level4">
                <xsl:with-param name="nodes" select="$nodes"/>
                <xsl:with-param name="level" select="$level"/>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="level4">
    <xsl:param name="nodes" as="element()*"/>
    <xsl:param name="level" as="xs:integer"/>
    
    <xsl:choose>
        <xsl:when test="$nodes/self::h4">
            <xsl:for-each-group select="$nodes" group-starting-with="h4">
                <xsl:element name="{concat('div', $level)}">
                    <xsl:call-template name="level5">
                        <xsl:with-param name="nodes" select="current-group()"/>
                        <xsl:with-param name="level" select="$level + 1"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:for-each-group>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="level5">
                <xsl:with-param name="nodes" select="$nodes"/>
                <xsl:with-param name="level" select="$level"/>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="level5">
    <xsl:param name="nodes" as="element()*"/>
    <xsl:param name="level" as="xs:integer"/>
    
    <xsl:choose>
        <xsl:when test="$nodes/self::h5">
            <xsl:for-each-group select="$nodes" group-starting-with="h5">
                <xsl:element name="{concat('div', $level)}">
                    <xsl:call-template name="level6">
                        <xsl:with-param name="nodes" select="current-group()"/>
                        <xsl:with-param name="level" select="$level + 1"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:for-each-group>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="level6">
                <xsl:with-param name="nodes" select="$nodes"/>
                <xsl:with-param name="level" select="$level"/>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template name="level6">
    <xsl:param name="nodes" as="element()*"/>
    <xsl:param name="level" as="xs:integer"/>

    <xsl:apply-templates select="$nodes/self::*"/>
</xsl:template>


<!-- Paragraphs -->
<xsl:template match="p">
    <xsl:choose>
        <xsl:when test="normalize-space(.) = ''"></xsl:when>
        <xsl:otherwise>
            <xsl:text>
</xsl:text>
            <p>
                <xsl:apply-templates/>
            </p>
            <xsl:text>
</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="td/p|TD/p">
<xsl:apply-templates/>
</xsl:template>

<!-- Ignore empty paragraphs -->

<xsl:template priority='100000' match="p[normalize-space(.) = '' and count(*) = 0]"/>


<xsl:template match="text()">
    <xsl:copy/>
</xsl:template>



<!-- inline formatting -->
<xsl:template match="b">
    <hi rend="bold">
        <xsl:apply-templates/>
    </hi>
</xsl:template>

<xsl:template match="small">
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match="br">
    <lb/>
</xsl:template>



<!-- External links -->
<xsl:template match="a[contains(@href,'http://') or contains(@href,'ftp://')]">
 <xref>
  <xsl:attribute name="url">
   <xsl:value-of select="normalize-space(@href)"/>
  </xsl:attribute>
  <xsl:apply-templates/>
 </xref>
</xsl:template>

<xsl:template match="link">
 <xref>
  <xsl:attribute name="url">
   <xsl:value-of select="normalize-space(@href)"/>
  </xsl:attribute>
  <xsl:apply-templates/>
 </xref>
</xsl:template>

<!-- Internal cross references -->
<xsl:template match="a[contains(@href,'#')]">
 <ref>
  <xsl:attribute name="target">
    <xsl:value-of select="substring-after(@href,'#')"/>
  </xsl:attribute>
  <xsl:apply-templates/>
 </ref>
</xsl:template>

<xsl:template match="a">
  <xsl:apply-templates/>
</xsl:template>



<!-- Block level elements -->

<xsl:template match="h1|h2|h3|h4|h5|h6">
    <head>
        <xsl:apply-templates/>
    </head>
</xsl:template>


<xsl:template match="hr">
    <milestone unit="tb"/>
</xsl:template>


<xsl:template match="span">
<xsl:variable name="class"><xsl:value-of select="@class"/></xsl:variable>
<xsl:variable name="style">
<xsl:if test="$class!=''">
<xsl:call-template name="fetchStyle"><xsl:with-param name="class"><xsl:value-of select="$class"/></xsl:with-param></xsl:call-template>
</xsl:if>
</xsl:variable>
    <!--
<xsl:message>Class:<xsl:value-of select="$class"/>-:<xsl:value-of select="$style"/></xsl:message>
-->
<xsl:choose>
<xsl:when test="$style!=''">
<hi>
<xsl:attribute name="rend"><xsl:value-of select="$style"/></xsl:attribute>
<xsl:apply-templates/>
</hi>
</xsl:when>
<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
</xsl:choose>
</xsl:template>

<!--
<xsl:variable name="pattern">.<xsl:value-of select="$class"/>{font-style:italic</xsl:variable>
<xsl:choose>
<xsl:when test="contains($css,$pattern)">
<hi rend="italic">
<xsl:apply-templates/>
</hi>
</xsl:when>
<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
</xsl:choose>
-->


<xsl:template match="form"><gap type="form"/></xsl:template>
<xsl:template match="noscript"><gap type="noscript"/></xsl:template>
<xsl:template match="embed"><gap type="embed"/></xsl:template>

<xsl:template match="sup|sub|em|strong|i|b">
<hi>
<xsl:attribute name="rend"><xsl:value-of select="name(.)"/></xsl:attribute>
<xsl:apply-templates/>
</hi>
</xsl:template>

<xsl:template match="center">
<ab type='center'>
<xsl:apply-templates/>
</ab>
</xsl:template>

<xsl:template match="font">
<hi>
<xsl:attribute name="rend">font-family:<xsl:value-of select="@face"/></xsl:attribute>
<xsl:apply-templates/>
</hi>
</xsl:template>

<xsl:template match="pre">
<hi rend='pre'>
    <xsl:apply-templates/>
</hi>
</xsl:template>



<!-- Images -->
<xsl:template match="img">
 
 
</xsl:template>




<!-- Lists -->
<xsl:template match="ul">
 <list>
  <xsl:apply-templates/>
 </list>
</xsl:template>

<xsl:template match="ol">
 <list>
  <xsl:apply-templates/>
 </list>
</xsl:template>
        
<xsl:template match="li">
 <item>
    <xsl:apply-templates/>
 </item>
</xsl:template>
        



<!-- Tables -->
<xsl:template match="table">
    <table>
        <xsl:apply-templates/>
    </table>
</xsl:template>


<xsl:template match="tbody">
    <xsl:apply-templates/>
</xsl:template>


<xsl:template match="tr">
    <row>
        <xsl:apply-templates/>
    </row>
</xsl:template>

<xsl:template match="th|td">
 <xsl:variable name="position" select="count(preceding-sibling::*) + 1"/>
 <cell>
  <xsl:if test="@colspan &gt; 1">
   <xsl:attribute name="cols">
    <xsl:value-of select="@colspan"/>
   </xsl:attribute>
  </xsl:if>
  <xsl:if test="@rowspan &gt; 1">
   <xsl:attribute name="rows">
    <xsl:value-of select="@rowspan"/>
   </xsl:attribute>
  </xsl:if>
  <xsl:apply-templates/>
 </cell>
</xsl:template>


<!-- Stop processing when an unknown element is encountered -->     
<xsl:template match="*">
 <xsl:message>Unhandled element <xsl:value-of select="name()"/>
 </xsl:message>
 <xsl:apply-templates/>
</xsl:template>

<xsl:template match="script|style" priority="100"/>

<xsl:template name="fetchStyle">
<xsl:param name="class"></xsl:param>
<xsl:variable name="regex">.<xsl:value-of select="$class"/>\s*\{([^\}]*)\}</xsl:variable>
<!--
<xsl:message><xsl:value-of select="$regex"/></xsl:message>
-->
<xsl:analyze-string select="$css" regex="{$regex}">
<xsl:matching-substring>
<xsl:variable name="x" select="regex-group(1)"/>
<xsl:value-of select="$x"/>
<!--
<xsl:if test="contains($x,'font-style:italic')">italic</xsl:if>
<xsl:if test="contains($x,'super')">superscript</xsl:if>
<xsl:if test="contains($x,'bold')">bold</xsl:if>
<xsl:if test="contains($x,'sub')">subscript</xsl:if>
-->
</xsl:matching-substring>
</xsl:analyze-string>
</xsl:template>

<xsl:template match="div/text()[normalize-space(.)='']"/>
<xsl:template match="p/text()[position()=1]"><xsl:value-of select="normalize-space(.)"/></xsl:template>

</xsl:stylesheet>
