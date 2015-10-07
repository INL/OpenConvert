<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
   xmlns:edate="http://exslt.org/dates-and-times"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:tei="http://www.tei-c.org/ns/1.0"
   exclude-result-prefixes="tei edate ns0 ns1 ns pm"
   version="2.0"
   xmlns:pm="http://www.politicalmashup.nl"
   xmlns:ns="http://www.namescape.nl/"
   xmlns:ns0="http://www.politicalmashup.nl"
   xmlns:ns1="http://www.politicalmashup.nl"
   xmlns="http://www.tei-c.org/ns/1.0"
   xpath-default-namespace="http://www.tei-c.org/ns/1.0">


<xsl:template match="/">

<CMD CMDVersion="1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
     xsi:schemaLocation="http://www.clarin.eu/cmd/ http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1369752611615/xsd"
     xmlns="http://www.clarin.eu/cmd/">
    <Header>
        <MdCreator>servicedesk@inl.nl</MdCreator>
        <MdCreationDate>2013-06-11</MdCreationDate>
        <MdSelfLink>
            <!-- Link naar deze file                  -->
        </MdSelfLink>
        <MdCollectionDisplayName>Namescape collection of novels</MdCollectionDisplayName>        
    </Header>
    <Resources>
        <ResourceProxyList>
            <ResourceProxy id="resource">
                <ResourceType>Resource</ResourceType>
                <ResourceRef>
                    <!-- Link naar resource (file) locatie  -->
                </ResourceRef>
            </ResourceProxy>
        </ResourceProxyList>
        <JournalFileProxyList></JournalFileProxyList>
        <ResourceRelationList></ResourceRelationList>
    </Resources>
<Components>
    <documentMetadataINLCorpus>
        <titleLevel2><xsl:value-of select="//interpGrp[@type='title']"/></titleLevel2>
        <authorLevel2><xsl:value-of select="//interpGrp[@type='author']"/></authorLevel2>
        <isbn><xsl:value-of select="//interpGrp[@type='isbn']"/></isbn>
        <pubYear_from><xsl:value-of select="//interpGrp[@type='pubyear']"/></pubYear_from>
        <pubYear_to><xsl:value-of select="//interpGrp[@type='pubyear']"/></pubYear_to>
        <witnessYear_from><xsl:value-of select="//interpGrp[@type='pubyear']"/></witnessYear_from>
        <witnessYear_to><xsl:value-of select="//interpGrp[@type='pubyear']"/></witnessYear_to>
        <publisher><xsl:value-of select="//interpGrp[@type='publisher']"/></publisher>
        <placePublication><xsl:value-of select="//interpGrp[@type='pubplace']"/></placePublication>            
    </documentMetadataINLCorpus>
</Components>
</CMD>
</xsl:template>
</xsl:stylesheet>
