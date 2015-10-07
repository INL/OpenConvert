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
   xmlns="http://www.tei-c.org/ns/1.0"
   xpath-default-namespace="http://www.tei-c.org/ns/1.0">

  <xsl:output method="xml" indent="yes"/>
  <xsl:strip-space elements="*"/>

 <xsl:template name="copy-attributes">
       <xsl:for-each select="@*">
        <xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
    </xsl:for-each>
  </xsl:template>

<xsl:param name="datadir">/datalokaal/Scratch/Corpus/SonarBooks/NAMESCAPE/SONAR500/DATA/CMDI</xsl:param>
<xsl:template match="name">
    <ns:ne>
	<xsl:apply-templates select="@*"/>
	<xsl:apply-templates/>
    </ns:ne>
</xsl:template>


<xsl:template match="folia:s">
<s>
<xsl:call-template name="copy-attributes"/>
<xsl:apply-templates/>
</s>
</xsl:template>

<xsl:template match="folia:w">
<xsl:choose>
<xsl:when test="./folia:pos[@class='LET()']">
<pc>
<xsl:call-template name="copy-attributes"/>
<xsl:value-of select="./folia:t"/>
</pc>
</xsl:when>
<xsl:otherwise>
<w>
<xsl:call-template name="copy-attributes"/>
<xsl:attribute name="lemma"><xsl:value-of select=".//folia:lemma/@class"/></xsl:attribute>
<xsl:attribute name="type"><xsl:value-of select=".//folia:pos/@class"/></xsl:attribute>
<xsl:value-of select="./folia:t"/>
</w>
</xsl:otherwise>
</xsl:choose>
</xsl:template>
<xsl:template match="folia:text">
<text>
<body>
<xsl:apply-templates/>
</body>
</text>
</xsl:template>

<xsl:template match="folia:div">
<div>
<xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="folia:head">
<head>
<xsl:call-template name="copy-attributes"/>
<xsl:apply-templates/>
</head>
</xsl:template>

<xsl:template match="folia:p">
<p>
<xsl:call-template name="copy-attributes"/>
<xsl:apply-templates/>
</p>
</xsl:template>
<xsl:template match="folia:FoLiA">
<xsl:variable name="cmdi"><xsl:value-of select="$datadir"/>/<xsl:value-of select="@xml:id"/>.cmdi.xml</xsl:variable>
<xsl:variable name="description"><xsl:value-of select="document($cmdi)//cmd:TextDescription"/></xsl:variable>

<xsl:variable name="title"><xsl:value-of select="document($cmdi)//cmd:TextTitle"/></xsl:variable>
<xsl:variable name="author"><xsl:value-of select="document($cmdi)//cmd:Author//cmd:Name"/></xsl:variable>
<xsl:variable name="pubyear"><xsl:value-of select="document($cmdi)//cmd:PublicationDate"/></xsl:variable>
<xsl:variable name="isbn"><xsl:value-of select="replace(substring-before(substring-after($description,'isbn'),'|'),' ', '')"/></xsl:variable>
<xsl:variable name="publisher"><xsl:value-of select="document($cmdi)//cmd:Publisher"/></xsl:variable>
<xsl:variable name="pubplace"><xsl:value-of select="document($cmdi)//cmd:PublicationPlace"/></xsl:variable>
<TEI>
<teiHeader>
<fileDesc>
<titleStmt>
<title><xsl:value-of select="$title"/></title>
</titleStmt>
<publicationStmt><date><xsl:value-of select="$pubyear"/></date><idno><xsl:value-of select="@xml:id"/></idno></publicationStmt>
<sourceDesc>
<listBibl xml:id="inlMetadata">
<bibl>
<interpGrp type="title"><interp><xsl:value-of select="$title"/></interp></interpGrp>
<interpGrp type="isbn"><interp><xsl:value-of select="$isbn"/></interp></interpGrp>
<interpGrp type="pubyear"><interp><xsl:value-of select="$pubyear"/></interp></interpGrp>
<interpGrp type="author"><interp><xsl:value-of select="$author"/></interp></interpGrp>
<interpGrp type="publisher"><interp><xsl:value-of select="$publisher"/></interp></interpGrp>
<interpGrp type="pubplace"><interp><xsl:value-of select="$pubplace"/></interp></interpGrp>
</bibl>
</listBibl>
</sourceDesc>
</fileDesc>
</teiHeader>
<xsl:apply-templates select="folia:text"/>
</TEI>
</xsl:template>

</xsl:stylesheet>
