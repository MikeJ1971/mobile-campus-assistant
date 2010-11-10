<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfTemp="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns="http://www.w3.org/2002/12/cal/ical#"
    xmlns:i="http://www.w3.org/2002/12/cal/ical#"
    xmlns:iTemp="http://www.w3.org/2002/12/cal/ical#">

    <xsl:namespace-alias stylesheet-prefix="i" result-prefix=""/>
    <xsl:namespace-alias stylesheet-prefix="rdf" result-prefix="rdfTemp"/>

<xsl:template match="/">
<rdfTemp:RDF>
  <xsl:element name="Vcalendar">
    <xsl:for-each select="events/event">
      <xsl:element name="component">
          <xsl:element name="Vevent">
            <iTemp:uid><xsl:value-of select="normalize-space(event_id)"/></iTemp:uid>
            <iTemp:summary><xsl:value-of select="normalize-space(event_title)"/></iTemp:summary>
            <iTemp:description><xsl:text disable-output-escaping="yes">&lt;</xsl:text><xsl:value-of select="concat('!','[','CDATA','[')"/><xsl:if test="string-length(normalize-space(event_description)) &gt; 0"><xsl:value-of select="normalize-space(event_description)"/><xsl:element name="br"/></xsl:if>
    <xsl:if test="string-length(normalize-space(event_advance_booking_details)) &gt; 0"><xsl:value-of select="normalize-space(event_advance_booking_details)"/><xsl:element name="br"/></xsl:if>
    <xsl:if test="string-length(normalize-space(event_speaker)) &gt; 0">Speaker: <xsl:value-of select="normalize-space(event_speaker)"/>
    <xsl:element name="br"/>
    </xsl:if>
    <xsl:if test="string-length(normalize-space(event_contact_details_email)) &gt; 0">Contact:
        <xsl:value-of select="normalize-space(event_contact_details_email)"/><xsl:element name="br"/>
    </xsl:if>
    <xsl:value-of select="normalize-space(event_organisation)"/><xsl:value-of select="concat(']',']')"/><xsl:text disable-output-escaping="yes">&gt;</xsl:text></iTemp:description>
            <iTemp:location><xsl:text disable-output-escaping="yes">&lt;</xsl:text><xsl:value-of select="concat('!','[','CDATA','[')"/><xsl:value-of select="normalize-space(event_venue)"/><xsl:value-of select="concat(']',']')"/><xsl:text disable-output-escaping="yes">&gt;</xsl:text></iTemp:location>
            <xsl:element name="dtstart">
              <xsl:attribute name="rdf:parseType" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">Resource</xsl:attribute>
              <iTemp:dateTime>
              <xsl:call-template name="formatDate">
                <xsl:with-param name="text" select="event_start_date"/>
              </xsl:call-template></iTemp:dateTime>
            </xsl:element>
            <xsl:apply-templates select="event_end_date"/>
            <iTemp:created><xsl:value-of select="normalize-space(event_created_date)"/></iTemp:created>
          </xsl:element>
      </xsl:element>
    </xsl:for-each>
  </xsl:element>
</rdfTemp:RDF>
</xsl:template>

<xsl:template match="event_end_date" xmlns:i="http://www.w3.org/2002/12/cal/ical#">
    <xsl:element name="dtend">
        <xsl:attribute name="rdf:parseType" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">Resource</xsl:attribute>
        <xsl:choose>
            <xsl:when test="string-length(.) &lt; 12">
                <xsl:variable name="stime">
                    <xsl:value-of select="(substring-after(../event_start_date,.))"/>
                </xsl:variable>
                <xsl:variable name="sremainder">
                    <xsl:value-of select="substring($stime,3)"/>
                </xsl:variable>                
                <xsl:variable name="shour">
                    <xsl:value-of select="number((substring($stime,1,2)))+1"/>
                </xsl:variable>             
                <iTemp:dateTime>
                    <xsl:call-template name="formatDate"><xsl:with-param name="text"><xsl:value-of select="concat(normalize-space(.),' ')"/><xsl:copy-of select="$shour" /><xsl:copy-of select="$sremainder" /></xsl:with-param></xsl:call-template>
                </iTemp:dateTime>
            </xsl:when>
            <xsl:otherwise>
                <iTemp:dateTime>
                   <xsl:call-template name="formatDate">
                       <xsl:with-param name="text" select="."/>
                   </xsl:call-template></iTemp:dateTime>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:element>
</xsl:template>

<xsl:template name="formatDate">
    <xsl:param name="text"/>
    <xsl:variable name="day">
        <xsl:value-of select="substring($text,1,2)"/>
    </xsl:variable>
    <xsl:variable name="month">
        <xsl:value-of select="substring($text,4,2)"/>
    </xsl:variable>
    <xsl:variable name="year">
        <xsl:value-of select="substring($text,7,4)"/>
    </xsl:variable>
    <xsl:variable name="time">
        <xsl:value-of select="substring($text,12)"/>
    </xsl:variable>
    <xsl:copy-of select="$year" />-<xsl:copy-of select="$month" />-<xsl:copy-of select="$day" />T<xsl:copy-of select="$time" />:00+00:00</xsl:template>

<xsl:template name="removeNewLines">
  <xsl:param name="text"/>
  <xsl:if test="normalize-space($text)">
    <xsl:value-of select="concat(normalize-space($text), '&#xA;')"/></xsl:if></xsl:template>

</xsl:stylesheet>