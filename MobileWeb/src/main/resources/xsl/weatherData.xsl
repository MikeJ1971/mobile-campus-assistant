<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://org.ilrt.mca/registry#"
                xmlns:lookup="http://example.org/lookup"
                extension-element-prefixes="lookup">

    <!-- URI for the weather data -->
    <xsl:param name="uri" select="'http://portal.bris.ac.uk/portal-weather/newXml'"/>

    <!-- create key -->
    <xsl:key name='month' match='lookup:months/month' use='@id'/>
    <xsl:variable name='months' select='lookup:months'/>

    <xsl:template match="/">
        <rdf:RDF>
            <rdf:Description rdf:about="{$uri}">
                <mca:hasHtmlFragment>
                    <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
                    <xsl:value-of select="concat('!','[','CDATA','[')"/>
                    <xsl:call-template name="site"/>
                    <xsl:value-of select="concat(']',']')"/>
                    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
                </mca:hasHtmlFragment>
            </rdf:Description>
        </rdf:RDF>
    </xsl:template>

    <xsl:template name="site">

        <!-- numerical value for the month -->
        <xsl:variable name="currentMonth" select="site/@mon"/>

        <!-- lookup the literal value -->
        <xsl:variable name="currentMonthLiteral">
            <xsl:for-each select='$months'>
                <xsl:value-of select='key("month", $currentMonth)'/>
            </xsl:for-each>
        </xsl:variable>

        <!-- time -->
        <xsl:variable name="time" select="site/@hr"/>

        <!-- display the date and time of the forecast -->
        <p class="weatherDateTime">
            <xsl:value-of select="site/@day"/><xsl:text> </xsl:text>
            <xsl:value-of select="site/@dayn"/><xsl:text> </xsl:text>
            <xsl:value-of select="$currentMonthLiteral"/><xsl:text>, </xsl:text>
            <xsl:value-of select="site/@yr"/><xsl:text> </xsl:text>
            <xsl:value-of select="substring($time,0,3)"/><xsl:text>:</xsl:text>
            <xsl:value-of select="substring($time,3,5)"/>
        </p>

        <!-- get today's data -->
        <xsl:for-each select="site/day[@no=1]">

            <!-- create a link to the University image -->
            <xsl:variable name="image">
                <xsl:text>http://portal.bris.ac.uk/portal-weather/images/w</xsl:text>
                <xsl:value-of select="@wx"/>
                <xsl:text>x15.gif</xsl:text>
            </xsl:variable>

            <!-- display weather details -->
            <p class="weatherIcon">
                <img src='{$image}' alt="Weather icon"/>
            </p>
            <p class="weatherMinTemp">Minimum: <xsl:value-of select="@mn"/>&#176;C
            </p>
            <p class="weatherMaxTemp">Maximum: <xsl:value-of select="@mx"/>&#176;C
            </p>

        </xsl:for-each>

    </xsl:template>

    <!-- Lookup Table of Months -->
    <lookup:months>
        <month id="01">January</month>
        <month id="02">February</month>
        <month id="03">March</month>
        <month id="04">April</month>
        <month id="05">May</month>
        <month id="06">June</month>
        <month id="07">July</month>
        <month id="08">August</month>
        <month id="09">September</month>
        <month id="10">October</month>
        <month id="11">November</month>
        <month id="12">December</month>
    </lookup:months>

</xsl:stylesheet>