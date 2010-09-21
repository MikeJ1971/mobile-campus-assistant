<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text"/>

	<xsl:template match="/">
{
	"markers" : [
	<xsl:apply-templates select="//Stop"/>
	]
}
	</xsl:template>

    <xsl:template match="Stop">
    {
    	"id": "<xsl:value-of select="ATCOCode"></xsl:value-of>",
    	"lat": "<xsl:value-of select="Lat"></xsl:value-of>",
    	"lng": "<xsl:value-of select="Lon"></xsl:value-of>"
    }<xsl:if test="position()!=last()">,</xsl:if>
    </xsl:template>

</xsl:stylesheet>