<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
 xmlns="http://www.tei-c.org/ns/1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xs="http://www.w3.org/2001/XMLSchema" 
 xmlns:xlink="http://www.w3.org/1999/xlink"
 xmlns:alto="http://www.loc.gov/standards/alto/ns-v2#" 
 xmlns:tei="http://www.tei-c.org/ns/1.0"
 xmlns:mets="http://www.loc.gov/METS/" 
 xmlns:mods="http://www.loc.gov/mods/v3"
 xmlns:local="http://library.princeton.edu/cew" 
 exclude-result-prefixes="xs alto mets local xlink" 
 version="2.0">
 
 <xsl:strip-space elements="*"/>
 <xsl:output indent="yes"/>
 


<xsl:param name="pathtodata"></xsl:param>
 <!--
 <xsl:variable name="path" as="xs:string"
  >/opt/local/BlueMountain/metadata/periodicals/bmtnaab/issues/1920/02_01/</xsl:variable>
-->
 
 
 
 <xsl:key name="files" match="mets:file" use="@ID"/>

 <xsl:function name="local:altopath">
  <xsl:param name="rawpath"/>
  <xsl:variable name="theresult" select="replace($rawpath, 'file://./', $pathtodata)" as="xs:string"/>
  <xsl:value-of select="$theresult"/>
 </xsl:function>
 
 <!-- <alto:MeasurementUnit> is usually 1/10 millimeter (mm10). -->
 <xsl:function name="local:to-millimeter">
  <xsl:param name="value" as="xs:integer"/>
  <xsl:value-of select="$value * 10"/>
 </xsl:function>
 


 <xsl:template match="mets:mets">
  <TEI xmlns="http://www.tei-c.org/ns/1.0">
   <teiHeader>
    <fileDesc>
     <titleStmt>
      <title>Later</title>
     </titleStmt>
     <publicationStmt>
      <publisher>Princeton University</publisher>
      <idno type="bmtnid"><xsl:value-of select="@OBJID" /></idno>
     </publicationStmt>
     <seriesStmt>
      <title level="s">Children's Literature</title>
     </seriesStmt>
    <sourceDesc>
      <xsl:apply-templates select="mets:dmdSec[@ID='dmd1']/mets:mdWrap/mets:xmlData/mods:mods"/>
    </sourceDesc>
    </fileDesc>
   </teiHeader>
   <facsimile>
    <xsl:apply-templates select="mets:structMap[@TYPE='PHYSICAL']" mode="facsimile"/>
   </facsimile>
   <text>

    <body>
     <xsl:apply-templates select="mets:structMap[@TYPE='PHYSICAL']"/>
    </body>
   </text>
  
  </TEI>

 </xsl:template>

 <xsl:template match="mets:structMap[@TYPE='PHYSICAL']" mode="#all">
  <xsl:apply-templates mode="#current"/>
 </xsl:template>

 <xsl:template match="mets:div" mode="#all">
  <xsl:apply-templates mode="#current"/>
 </xsl:template>

 <xsl:template match="mets:fptr" mode="#all">
  <xsl:apply-templates mode="#current"/>
 </xsl:template>

 <xsl:template match="mets:par" mode="#all">
  <xsl:apply-templates mode="#current"/>
 </xsl:template>

 <xsl:template match="mets:area[@BETYPE = 'IDREF']" mode="#all">
  <xsl:variable name="rawpath" select="key('files', @FILEID)/mets:FLocat/@xlink:href"/>
  <xsl:variable name="altopath"
   select="local:altopath($rawpath)" as="xs:string"/>
  <xsl:apply-templates select="document($altopath, /)//alto:Page" mode="#current" />
 
 </xsl:template>

<!--
 <xsl:template match="alto:Layout">
  <text>
   <body>
    <xsl:apply-templates/>
   </body>
  </text>
 </xsl:template>
 -->
 
 <xsl:template match="alto:Page">
  <pb>
   <xsl:attribute name="facs" select="@ID"/>
  </pb>
  <xsl:apply-templates/>
 </xsl:template>
 
 <xsl:template
  match="alto:TopMargin | alto:LeftMargin | alto:RightMargin | alto:BottomMargin | alto:PrintSpace">
  <xsl:apply-templates/>
 </xsl:template>
 
 <xsl:template match="alto:TextBlock">
  <ab>
   <xsl:attribute name="facs" select="@ID"/>
   <xsl:apply-templates/>
  </ab>
 </xsl:template>
 
 <xsl:template match="alto:TextLine">
  <lb>
   <xsl:attribute name="facs" select="@ID"/>
  </lb>
  <xsl:apply-templates/>
 </xsl:template>
 
 
 <xsl:template match="alto:String">
  <xsl:value-of select="@CONTENT"/>
  <xsl:text/>
 </xsl:template>
 
 <xsl:template match="alto:SP">
  <xsl:text> </xsl:text>
 </xsl:template>
 
 <xsl:template match="alto:HYP">
  <pc type="endhypen" pre="false">-</pc>
 </xsl:template>
 


<!-- Facsimile templates -->
 
 <!-- <alto:Page> corresponds with <tei:surface> -->
 <xsl:template match="alto:Page" mode="facsimile">
  <tei:surface>
   <xsl:attribute name="xml:id" select="@ID"/>
   <xsl:attribute name="ulx">0</xsl:attribute>
   <xsl:attribute name="uly">0</xsl:attribute>
   <xsl:attribute name="lrx" select="local:to-millimeter(@WIDTH)"/>
   <xsl:attribute name="lry" select="local:to-millimeter(@HEIGHT)"/>
   
   <tei:graphic>
    <xsl:attribute name="url">
     <xsl:value-of
      select="ancestor::alto:alto/alto:Description/alto:sourceImageInformation/alto:fileName"/>
    </xsl:attribute>
   </tei:graphic>
   <xsl:apply-templates mode="#current"/>
  </tei:surface>
 </xsl:template>
 
 <xsl:template
  match="alto:TopMargin | alto:LeftMargin | alto:RightMargin | alto:BottomMargin | alto:PrintSpace"
  mode="facsimile">
  <tei:zone>
   <xsl:attribute name="xml:id" select="@ID"/>
   <xsl:attribute name="ulx" select="local:to-millimeter(xs:integer(@HPOS))"/>
   <xsl:attribute name="uly" select="local:to-millimeter(xs:integer(@VPOS))"/>
   <xsl:attribute name="lrx"
    select="local:to-millimeter(xs:integer(@HPOS)) + local:to-millimeter(xs:integer(@WIDTH))"/>
   <xsl:attribute name="lry"
    select="local:to-millimeter(xs:integer(@VPOS)) + local:to-millimeter(xs:integer(@HEIGHT))"/>
   
   <xsl:apply-templates mode="#current"/>
  </tei:zone>
 </xsl:template>
 
 <xsl:template match="alto:TextBlock" mode="facsimile">
  <tei:zone type="TextBlock">
   <xsl:attribute name="xml:id" select="@ID"/>
   <xsl:attribute name="ulx" select="local:to-millimeter(xs:integer(@HPOS))"/>
   <xsl:attribute name="uly" select="local:to-millimeter(xs:integer(@VPOS))"/>
   <xsl:attribute name="lrx"
    select="local:to-millimeter(xs:integer(@HPOS)) + local:to-millimeter(xs:integer(@WIDTH))"/>
   <xsl:attribute name="lry"
    select="local:to-millimeter(xs:integer(@VPOS)) + local:to-millimeter(xs:integer(@HEIGHT))"/>
   
   <xsl:apply-templates mode="#current"/>
  </tei:zone>
 </xsl:template>
 
 <xsl:template match="alto:TextLine" mode="facsimile">
  <tei:zone type="TextLine">
   <xsl:attribute name="xml:id" select="@ID"/>
   <xsl:attribute name="ulx" select="local:to-millimeter(xs:integer(@HPOS))"/>
   <xsl:attribute name="uly" select="local:to-millimeter(xs:integer(@VPOS))"/>
   <xsl:attribute name="lrx"
    select="local:to-millimeter(xs:integer(@HPOS)) + local:to-millimeter(xs:integer(@WIDTH))"/>
   <xsl:attribute name="lry"
    select="local:to-millimeter(xs:integer(@VPOS)) + local:to-millimeter(xs:integer(@HEIGHT))"/>
   
   <xsl:apply-templates mode="#current"/>
  </tei:zone>
 </xsl:template>
 
 <xsl:template match="alto:String" mode="facsimile">
  <tei:zone type="String">
   <xsl:attribute name="xml:id" select="@ID"/>
   <xsl:attribute name="ulx" select="local:to-millimeter(xs:integer(@HPOS))"/>
   <xsl:attribute name="uly" select="local:to-millimeter(xs:integer(@VPOS))"/>
   <xsl:attribute name="lrx"
    select="local:to-millimeter(xs:integer(@HPOS)) + local:to-millimeter(xs:integer(@WIDTH))"/>
   <xsl:attribute name="lry"
    select="local:to-millimeter(xs:integer(@VPOS)) + local:to-millimeter(xs:integer(@HEIGHT))"/>
   
   <xsl:value-of select="@CONTENT"/>
   
  </tei:zone>
  
 </xsl:template>
 
 <xsl:template match="alto:SP" mode="facsimile">
  <xsl:text> </xsl:text>
 </xsl:template>
 
 <xsl:template match="alto:HYP" mode="facsimile">
  <tei:pc type="endhypen" pre="false">-</tei:pc>
 </xsl:template>
 

 <!-- MODS templates -->
 <xsl:template match="mods:mods">
   <biblStruct>
     <monogr>
       <title level="j">
	 <xsl:if test="mods:titleInfo/mods:nonSort">
	   <seg type="nonSort"><xsl:apply-templates select="mods:titleInfo/mods:nonSort"/></seg>
	 </xsl:if>
	 <seg type="main"><xsl:apply-templates select="mods:titleInfo/mods:title"/></seg>
       </title>
       <imprint>
	 <xsl:if test="mods:part/mods:detail[@type='volume']">
	   <biblScope type="vol">
	     <xsl:value-of select="mods:part/mods:detail[@type='volume']/mods:number"/>
	   </biblScope>
	 </xsl:if>
	 <xsl:if test="mods:part/mods:detail[@type='number']">
	   <biblScope type="issue">
	     <xsl:value-of select="mods:part/mods:detail[@type='number']/mods:number"/>
	   </biblScope>
	 </xsl:if>
	 <date>
	   <xsl:attribute name="when">
	     <xsl:value-of select="mods:originInfo/mods:dateIssued[@encoding='w3cdtf']"/>
	   </xsl:attribute>
	     <xsl:value-of select="mods:originInfo/mods:dateIssued[empty(@keyDate)]"/>
	 </date>
       </imprint>
     </monogr>
     <xsl:apply-templates select="mods:relatedItem[@type='constituent']" />
   </biblStruct>
 </xsl:template>

 <xsl:template match="mods:relatedItem[@type='constituent']">
   <relatedItem type="constituent" xml:id="{@ID}">
     <biblStruct>
       <analytic>
	 <xsl:apply-templates select="mods:titleInfo"/>
	 <xsl:apply-templates select="mods:name"/>
	 <xsl:apply-templates select="mods:language"/>
       </analytic>
       <monogr>
	 <imprint>
	   <biblScope type="pp" corresp="{mods:part/mods:extent[@unit='page']/mods:start}"/>
	 </imprint>
       </monogr>
       <xsl:apply-templates select="mods:note"/>
       <xsl:apply-templates select="mods:relatedItem[@type='constituent']" />
     </biblStruct>
   </relatedItem>
 </xsl:template>

 <xsl:template match="mods:titleInfo">
   <title level="a">
     <xsl:if test="mods:nonSort">
       <seg type="nonSort"><xsl:apply-templates select="mods:nonSort"/></seg>
     </xsl:if>
     <seg type="main"><xsl:apply-templates select="mods:title"/></seg>
   </title>
 </xsl:template>

 <xsl:template match="mods:name">
   <respStmt>
     <persName><xsl:value-of select="mods:displayForm"/></persName>
     <resp><xsl:value-of select="mods:role/mods:roleTerm"/></resp>
   </respStmt>
 </xsl:template>

 <xsl:template match="mods:language">
   <textLang mainLang="{mods:languageTerm}"/>
 </xsl:template>


</xsl:stylesheet>



