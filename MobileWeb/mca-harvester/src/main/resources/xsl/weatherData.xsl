<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#"
                xmlns:lookup="http://example.org/lookup"
                extension-element-prefixes="lookup">

    <!-- URI for the weather data -->
    <xsl:param name="uri" select="'http://portal.bris.ac.uk/portal-weather/newXml'"/>

    <!-- create key -->
    <xsl:key name='month' match='lookup:months/lookup:month' use='@id'/>
    <xsl:variable name='months' select='document("")/xsl:stylesheet/lookup:months'/>

    <xsl:template match="/">
        <rdf:RDF>
            <rdf:Description rdf:about="{$uri}">
                <mca:hasHtmlFragment>
                    <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
                    <xsl:value-of select="concat('!','[','CDATA','[')"/>
                    <div id="weatherData">
                    <xsl:call-template name="site"/>
                    </div>
                    <xsl:value-of select="concat(']',']')"/>
                    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
                </mca:hasHtmlFragment>
            </rdf:Description>
        </rdf:RDF>
    </xsl:template>

    <xsl:template name="site">

        <!-- time -->
        <xsl:variable name="time" select="site/@hr"/>

        <!-- get today's data -->
        <xsl:for-each select="site/day[@no=1]">

            <!-- create a link to the University image -->
            <xsl:variable name="image">
                <xsl:text>http://portal.bris.ac.uk/portal-weather/images/w</xsl:text>
                <xsl:value-of select="@wx"/>
                <xsl:text>x15.gif</xsl:text>
            </xsl:variable>

            <!-- display weather details -->
            <p class="weatherIcon"><img src='{$image}' alt="Weather icon"/></p>
            <p class="weatherDetails">
                Minimum: <xsl:value-of select="@mn"/><xsl:text>&#176;C, </xsl:text>
                Maximum: <xsl:value-of select="@mx"/><xsl:text>&#176;C</xsl:text>
            </p>

        </xsl:for-each>

        <!-- display the date and time of the forecast -->
        <p class="weatherDateTime">
            <xsl:value-of select="site/@day"/><xsl:text> </xsl:text>
            <xsl:value-of select="site/@dayn"/><xsl:text>-</xsl:text>
            <xsl:value-of select="site/@mon"/><xsl:text>-</xsl:text>
            <xsl:value-of select="site/@yr"/><xsl:text> </xsl:text>
            <xsl:value-of select="substring($time,0,3)"/><xsl:text>:</xsl:text>
            <xsl:value-of select="substring($time,3,5)"/>
        </p>

    </xsl:template>

</xsl:stylesheet>