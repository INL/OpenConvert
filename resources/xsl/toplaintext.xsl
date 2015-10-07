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
<text>
<xsl:apply-templates/>
</text>
</xsl:template>
</xsl:stylesheet>
