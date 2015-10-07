<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:opf="http://www.idpf.org/2007/opf"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
		xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:urn="urn:oasis:names:tc:opendocument:xmlns:container"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns="http://www.tei-c.org/ns/1.0"
                exclude-result-prefixes="dc fn opf urn xhtml"
                version="2.0">

  <xsl:output media-type="xml" indent="yes"/>

  <xsl:param name="unzipTo">not set</xsl:param>

  <xsl:template match="/">
    <xsl:message>unzipTo: <xsl:value-of select="$unzipTo"/></xsl:message>
    <xsl:apply-templates select="document(concat(concat($unzipTo,'/'), /urn:container/urn:rootfiles/urn:rootfile/@full-path), .)/opf:package"/>
  </xsl:template>

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
          <date><xsl:value-of select="dc:date"/></date>
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

          <!-- <biblStruct> -->
          <!--   <monogr> -->
          <!--     <author><xsl:value-of select="dc:creator"/></author> -->
          <!--     <title><xsl:value-of select="dc:title"/></title> -->
          <!--     <edition></edition> -->
          <!--     <imprint> -->
          <!--       <publisher><xsl:value-of select="dc:publisher"/></publisher> -->
          <!--       <pubPlace></pubPlace> -->
          <!--       <date><xsl:value-of select="dc:date"/></date> -->
          <!--     </imprint> -->
          <!--   </monogr><\!- - dc:identifier[@opf:scheme='ISBN'] doesn't cut it since everybody seems to want to make something up themselves -\-> -->
          <!--   <idno type="ISBN"><xsl:value-of select="(dc:identifier[ matches(., '^(isbn\s*)?\d([\s.-]?\d){8}(([\s.-]?\d){3})?([\s.-]?[\dx])$', 'i') ] )[1]"/></idno> -->

          <!-- </biblStruct> -->
        </sourceDesc>
      </fileDesc>
    </teiHeader>
  </xsl:template>

  <xsl:template match="xhtml:html">
    <xsl:message>Dealing with: <xsl:value-of select=".//title"/></xsl:message>
    <xsl:apply-templates select="xhtml:body"/>
  </xsl:template>

  <xsl:template match="xhtml:body[./xhtml:*[position() = 1 and not(self::xhtml:h1)]]">
    <div>
      <xsl:apply-templates select="xhtml:*[1]" mode="a"/>
    </div>
    <xsl:apply-templates select="xhtml:h1"/>
    <xsl:apply-templates select="xhtml:h1/*" mode="a"/>
  </xsl:template>

  <xsl:template match="xhtml:body">
    <xsl:apply-templates select="xhtml:h1"/>
    <xsl:apply-templates select="xhtml:h1/*" mode="a"/>
  </xsl:template>

  <xsl:template match="xhtml:h1">
    <div type="chapter">
      <head><xsl:apply-templates /></head>
      <xsl:apply-templates select="following-sibling::xhtml:*[1]" mode="a"/>
    </div>
  </xsl:template>

  <xsl:template match="xhtml:h1" mode="a"/>

  <xsl:template match="xhtml:h2" mode="a">
    <div type="section">
      <head><xsl:apply-templates /></head>
      <xsl:apply-templates select="following-sibling::xhtml:*[1]" mode="a"/>
    </div>
  </xsl:template>

  <xsl:template match="xhtml:p" mode="a">
    <p>
      <xsl:apply-templates />
    </p>
    <xsl:apply-templates select="following-sibling::xhtml:*[1]" mode="a"/>
  </xsl:template>

  <xsl:template match="xhtml:p">
    <p>
      <xsl:apply-templates />
    </p>
  </xsl:template>

  <xsl:template match="xhtml:br">
    <lb/>
  </xsl:template>

  <xsl:template match="xhtml:i">
    <hi rend="italic">
      <xsl:apply-templates />
    </hi>
  </xsl:template>

  <xsl:template match="xhtml:*" mode="a">
    <xsl:apply-templates />
    <xsl:apply-templates select="following-sibling::xhtml:*[1]" mode="a"/>
  </xsl:template>

  <xsl:template match="xhtml:*/text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <!-- page breaks =[ -->
  <xsl:template match="xhtml:*[fn:matches(@id, '^([Pp]age[_-]?\d+|p[bg]\d+|pl-\d+(-\d+)?|p_\d+)$')]">
    <pb n="{fn:replace(@id, '^\D+(\d+).*', '$1')}"/>
  </xsl:template>

  <xsl:template match="xhtml:*[fn:matches(@class, '(^|\s)(mbppagebreak|pagebreak|wpo-newpage)(\s|$)')]">
    <pb/>
  </xsl:template>
  <!-- end page breaks -->

  <xsl:template match="xhtml:*[@class = 'pagebreak'][not(*)]">
    <pb/>
  </xsl:template>

  <xsl:template match="text()"/>
</xsl:stylesheet>
