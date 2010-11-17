<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#"
                xmlns:html="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="html">

    <xsl:output encoding="utf-8"/>

    <!-- URI for the weather data -->
    <xsl:param name="uri" select="'http://is-freepcs.cse.bris.ac.uk/'"/>

    <xsl:template match="/">
        <rdf:RDF>
            <rdf:Description rdf:about="{$uri}">
                <mca:hasHtmlFragment>
                    <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
                    <xsl:value-of select="concat('!','[','CDATA','[')"/>
                    <div id="pcavailability">
                    <xsl:call-template name="update"/>
                    <table>
                        <xsl:call-template name="availability"/>
                    </table>
                    </div>
                    <xsl:value-of select="concat(']',']')"/>
                    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
                </mca:hasHtmlFragment>
            </rdf:Description>
        </rdf:RDF>
    </xsl:template>

    <xsl:template name="update">
        <p>
            <xsl:value-of select="html:html/html:body/html:h1"/>
        </p>
    </xsl:template>

    <xsl:template name="availability">
        <xsl:for-each select="html:html/html:body/html:table/html:tbody/html:tr">
            <tr>
                <td><xsl:value-of select="html:td[1]"/></td>
                <td><xsl:value-of select="html:td[2]"/></td>
            </tr>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
