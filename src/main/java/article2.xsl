<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:JSON_MAIN1="example.JSON_MAIN1">
    <xsl:output method="xml" indent="yes"/>

  
    <xsl:template>
        <order>
            
            <productdetails>
                
                        <productid> <xsl:value-of select="JSON_MAIN1:getNum()" /> </productid>
                        
            </productdetails>
        </order>
        
    </xsl:template>

</xsl:stylesheet>
