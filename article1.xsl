<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes"/>

  
    <xsl:template match="*">
        <order>
            <header>
                <orderid> <xsl:value-of select="header/order-id" /> </orderid>
                <amount> 
                    <xsl:value-of select="header/net-amount" />
                </amount>
                <username> <xsl:value-of select="header/customer-details/Username" /> </username>
                <name> <xsl:value-of select="header/customer-details/firstname" /><xsl:text> </xsl:text><xsl:value-of select="header/customer-details/lastname" /> </name>
            </header>
            <productdetails>
                <xsl:for-each select="order-item/element">
                    <element>
                        <quantity><xsl:value-of select="quantity" /> </quantity>
                        <productid> <xsl:value-of select="product-id" /> </productid>
                        <title> <xsl:value-of select="title" /> </title>
                        <price><xsl:value-of select="price * 100" /></price>
                    </element>
                </xsl:for-each>
            </productdetails>
        </order>
        
    </xsl:template>

</xsl:stylesheet>
